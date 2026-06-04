package yoyo.inventory.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.InvoiceService;
import yoyo.inventory.dto.request.SaleItemRequest;
import yoyo.inventory.dto.request.SaleRequest;
import yoyo.inventory.dto.response.SaleResponse;
import yoyo.inventory.entities.*;
import yoyo.inventory.enums.InvoiceStatus;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;
import yoyo.inventory.execption.ApiException;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.SaleMapper;
import yoyo.inventory.repository.CustomerRepository;
import yoyo.inventory.repository.InvoiceRepository;
import yoyo.inventory.repository.SaleRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.SaleService;
import yoyo.inventory.services.StockService;
import yoyo.inventory.services.StoreService;
import yoyo.inventory.services.TransactionService;
import yoyo.inventory.specification.sale.SaleFilter;
import yoyo.inventory.specification.sale.SaleSpec;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.jpa.domain.Specification;
import yoyo.inventory.common.PageUtil;
import java.util.Map;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final StockService stockService;
    private final ProductService productService;
    private final StoreService storeService;
    private final TransactionService transactionService;
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final ObjectMapper objectMapper;

    @Override
    public SaleResponse create(SaleRequest request, String createdBy) {
        Stores store = storeService.findById(request.getStoreId());
        Sale sale = new Sale();
        sale.setInvoiceNo(invoiceService.generate("SAL"));
        sale.setSaleDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.COMPLETED);
        sale.setStore(store);
        sale.setNote(request.getNote());
        sale.setCreatedBy(createdBy);
        // Set customer if provided
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));
            sale.setCustomer(customer);
        }
        // Build items and calculate amounts
        buildSaleItems(sale, request);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public SaleResponse getById(Long id) {
        return saleMapper.toResponse(findById(id));
    }

    @Override
    public Page<SaleResponse> getAll(Map<String, String> params) {
        SaleFilter filter = objectMapper.convertValue(params, SaleFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Sale> spec = SaleSpec.filterBy(filter);
        return saleRepository.findAll(spec, pageable).map(saleMapper::toResponse);
    }

    @Override
    public SaleResponse update(Long id, SaleRequest request, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() != SaleStatus.HOLD) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only HOLD sale can be updated");
        }

        // Update store if changed
        if (request.getStoreId() != null) {
            Stores store = storeService.findById(request.getStoreId());
            sale.setStore(store);
        }

        // Update customer
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));
            sale.setCustomer(customer);
        } else {
            sale.setCustomer(null);
        }

        // Update note
        sale.setNote(request.getNote());
        sale.setUpdatedBy(updatedBy);

        // Clear existing items and rebuild
        sale.getItems().clear();
        buildSaleItems(sale, request);

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public SaleResponse approve(Long id, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() != SaleStatus.HOLD) {
            throw new ResourceNotFoundException("Only HOLD sale can be approved");
        }
        sale.setStatus(SaleStatus.COMPLETED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public SaleResponse complete(Long id, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() != SaleStatus.HOLD) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only HOLD sale can be completed");
        }

        // Deduct stock and log transactions
        for (SaleItem item : sale.getItems()) {
            stockService.decreaseStock(
                    item.getProduct().getId(),
                    sale.getStore().getId(),
                    item.getQuantity()
            );

            transactionService.logStockMovement(
                    TransactionType.SALE,
                    sale.getId(),
                    sale.getInvoiceNo(),
                    item.getProduct().getId(),
                    sale.getStore().getId(),
                    resolveUnitId(item),
                    item.getQuantity(),
                    BigDecimal.ONE,
                    item.getUnitPrice(),
                    SaleStatus.COMPLETED,
                    updatedBy
            );
        }

        sale.setStatus(SaleStatus.COMPLETED);
        sale.setUpdatedBy(updatedBy);
        Sale savedSale = saleRepository.save(sale);

        // Generate Invoice
        generateInvoice(savedSale);

        return saleMapper.toResponse(savedSale);
    }

    @Override
    public SaleResponse cancel(Long id, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() == SaleStatus.CANCELLED || sale.getStatus() == SaleStatus.RETURNED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Sale already closed");
        }

        if (sale.getStatus() == SaleStatus.COMPLETED) {
            for (SaleItem item : sale.getItems()) {
                stockService.increaseStock(
                        item.getProduct().getId(),
                        sale.getStore().getId(),
                        item.getQuantity(),
                        item.getProduct().getCostPrice()
                );
                transactionService.logStockMovement(
                        TransactionType.ADJUSTMENT_IN,
                        sale.getId(),
                        sale.getInvoiceNo(),
                        item.getProduct().getId(),
                        sale.getStore().getId(),
                        resolveUnitId(item),
                        item.getQuantity(),
                        BigDecimal.ONE,
                        item.getUnitPrice(),
                        SaleStatus.CANCELLED,
                        updatedBy
                );
            }
        }

        sale.setStatus(SaleStatus.CANCELLED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public SaleResponse returnSale(Long id, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() == SaleStatus.RETURNED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Sale already returned");
        }
        if (sale.getStatus() != SaleStatus.COMPLETED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only COMPLETED sale can be returned");
        }

        for (SaleItem item : sale.getItems()) {
            stockService.increaseStock(
                    item.getProduct().getId(),
                    sale.getStore().getId(),
                    item.getQuantity(),
                    item.getProduct().getCostPrice()
            );

            transactionService.logStockMovement(
                    TransactionType.ADJUSTMENT_IN,
                    sale.getId(),
                    sale.getInvoiceNo(),
                    item.getProduct().getId(),
                    sale.getStore().getId(),
                    resolveUnitId(item),
                    item.getQuantity(),
                    BigDecimal.ONE,
                    item.getUnitPrice(),
                    SaleStatus.RETURNED,
                    updatedBy
            );
        }

        sale.setStatus(SaleStatus.RETURNED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public void delete(Long id, String deletedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() == SaleStatus.COMPLETED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot delete completed sale");
        }
        saleRepository.delete(sale);
    }

    @Override
    public Sale findById(Long id) {
        return saleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sale", id));
    }

    @Override
    public SaleResponse updateStatus(Long id, SaleStatus status, String updatedBy) {
        if (status == SaleStatus.COMPLETED) {
            return complete(id, updatedBy);
        }
        if (status == SaleStatus.CANCELLED) {
            return cancel(id, updatedBy);
        }
        if (status == SaleStatus.RETURNED) {
            return returnSale(id, updatedBy);
        }
        Sale sale = findById(id);
        sale.setStatus(SaleStatus.HOLD);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    // =====================================
    // PRIVATE HELPERS
    // =====================================

    /**
     * Build sale items from request and set calculated amounts on the sale.
     */
    private void buildSaleItems(Sale sale, SaleRequest request) {
        BigDecimal subTotal = BigDecimal.ZERO;
        List<SaleItem> saleItems = new ArrayList<>();

        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productService.findById(itemRequest.getProductId());
            BigDecimal discount = itemRequest.getDiscountAmount() == null ? BigDecimal.ZERO : itemRequest.getDiscountAmount();
            BigDecimal totalPrice = itemRequest.getUnitPrice().multiply(itemRequest.getQuantity()).subtract(discount);

            SaleItem saleItem = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(itemRequest.getUnitPrice())
                    .discountAmount(discount)
                    .totalPrice(totalPrice)
                    .build();

            saleItems.add(saleItem);
            subTotal = subTotal.add(totalPrice);
        }

        BigDecimal discountAmount = request.getDiscountAmount() == null ? BigDecimal.ZERO : request.getDiscountAmount();
        BigDecimal taxAmount = request.getTaxAmount() == null ? BigDecimal.ZERO : request.getTaxAmount();
        BigDecimal totalAmount = subTotal.subtract(discountAmount).add(taxAmount);

        sale.getItems().addAll(saleItems);
        sale.setSubTotal(subTotal);
        sale.setDiscountAmount(discountAmount);
        sale.setTaxAmount(taxAmount);
        sale.setTotalAmount(totalAmount);
    }

    /**
     * Generate an Invoice entity from a completed sale.
     */
    private void generateInvoice(Sale sale) {
        Invoice invoice = Invoice.builder()
                .invoiceNo(invoiceService.generate("INV"))
                .invoiceDate(LocalDateTime.now())
                .dueDate(LocalDate.now().plusDays(30))
                .status(InvoiceStatus.UNPAID)
                .subTotal(sale.getSubTotal())
                .discountAmount(sale.getDiscountAmount())
                .taxAmount(sale.getTaxAmount())
                .grandTotal(sale.getTotalAmount())
                .paidAmount(BigDecimal.ZERO)
                .balanceDue(sale.getTotalAmount())
                .sale(sale)
                .customer(sale.getCustomer())
                .payments(new ArrayList<>())
                .build();

        invoiceRepository.save(invoice);
        sale.setInvoice(invoice);
    }

    private Long resolveUnitId(SaleItem item) {
        if (item.getProduct() != null
                && item.getProduct().getTblUnit() != null
                && item.getProduct().getTblUnit().getId() != null) {
            return item.getProduct().getTblUnit().getId();
        }
        throw new IllegalStateException("Unit is required to log sale transaction for product " + item.getProduct().getId());
    }
}
