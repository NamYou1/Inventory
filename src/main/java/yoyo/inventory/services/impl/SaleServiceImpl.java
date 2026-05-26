package yoyo.inventory.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yoyo.inventory.dto.request.SaleItemRequest;
import yoyo.inventory.dto.request.SaleRequest;
import yoyo.inventory.dto.response.SaleResponse;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.Sale;
import yoyo.inventory.entities.SaleItem;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.SaleMapper;
import yoyo.inventory.repository.SaleRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.SaleService;
import yoyo.inventory.services.StockService;
import yoyo.inventory.services.StoreService;
import yoyo.inventory.services.TransactionService;

import java.math.BigDecimal;
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

    @Override
//    @CacheEvict(cacheNames = {"sale-page", "sale-response", "txn-summary"}, allEntries = true)
    public SaleResponse create(SaleRequest request, String createdBy) {
        Stores store = storeService.findById(request.getStoreId());

        Sale sale = new Sale();
        sale.setInvoiceNo("INV-" + System.currentTimeMillis());
        sale.setSaleDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.PENDING);
        sale.setStore(store);
        sale.setNote(request.getNote());
        sale.setCreatedBy(createdBy);

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

        sale.setItems(saleItems);
        sale.setSubTotal(subTotal);
        sale.setDiscountAmount(discountAmount);
        sale.setTaxAmount(taxAmount);
        sale.setTotalAmount(totalAmount);

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
//    @Cacheable(cacheNames = "sale-response", key = "#id")
    public SaleResponse getById(Long id) {
        return saleMapper.toResponse(findById(id));
    }

    @Override
//    @Cacheable(cacheNames = "sale-page", key = "#pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()")
    public Page<SaleResponse> getAll(Pageable pageable) {
        return saleRepository.findAll(pageable).map(saleMapper::toResponse);
    }

    @Override
//    @CacheEvict(cacheNames = {"sale-page", "sale-response", "txn-summary"}, allEntries = true)
    public SaleResponse update(Long id, SaleRequest request, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new ResourceNotFoundException("Only PENDING sale can be updated");
        }

        sale.setNote(request.getNote());
        sale.setUpdatedBy(updatedBy);

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
//    @CacheEvict(cacheNames = {"sale-page", "sale-response", "txn-summary"}, allEntries = true)
    public SaleResponse approve(Long id, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new ResourceNotFoundException("Only PENDING sale can be approved");
        }
        sale.setStatus(SaleStatus.COMPLETED);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
//    @CacheEvict(cacheNames = {"sale-page", "sale-response", "txn-summary"}, allEntries = true)
    public SaleResponse complete(Long id, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new ResourceNotFoundException("Only PENDING sale can be completed");
        }

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
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
//    @CacheEvict(cacheNames = {"sale-page", "sale-response", "txn-summary"}, allEntries = true)
    public SaleResponse cancel(Long id, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() == SaleStatus.CANCELLED || sale.getStatus() == SaleStatus.RETURNED) {
            throw new ResourceNotFoundException("Sale already closed");
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
//    @CacheEvict(cacheNames = {"sale-page", "sale-response", "txn-summary"}, allEntries = true)
    public SaleResponse returnSale(Long id, String updatedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() == SaleStatus.RETURNED) {
            throw new ResourceNotFoundException("Sale already returned");
        }
        if (sale.getStatus() != SaleStatus.COMPLETED) {
            throw new ResourceNotFoundException("Only COMPLETED sale can be returned");
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
//    @CacheEvict(cacheNames = {"sale-page", "sale-response", "txn-summary"}, allEntries = true)
    public void delete(Long id, String deletedBy) {
        Sale sale = findById(id);
        if (sale.getStatus() == SaleStatus.COMPLETED) {
            throw new ResourceNotFoundException("Cannot delete completed sale");
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
        sale.setStatus(SaleStatus.PENDING);
        sale.setUpdatedBy(updatedBy);
        return saleMapper.toResponse(saleRepository.save(sale));
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
