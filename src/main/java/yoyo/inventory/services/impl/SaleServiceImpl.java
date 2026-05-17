package yoyo.inventory.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import yoyo.inventory.execption.ResourceNotFoundExecption;
import yoyo.inventory.mappers.SaleMapper;
import yoyo.inventory.repository.ProductRepository;
import yoyo.inventory.repository.SaleRepository;
import yoyo.inventory.repository.StoreRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.SaleService;
import yoyo.inventory.services.StockService;
import yoyo.inventory.services.StoreService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl
        implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final StockService stockService;
    private final ProductService productService;
    private final StoreService storeService;
    @Override
    public SaleResponse create(SaleRequest request, String createdBy) {
    Stores stores = storeService.findById(request.getStoreId());
        Sale sale = new Sale();
        sale.setInvoiceNo("INV-" + System.currentTimeMillis());
        sale.setSaleDate(LocalDateTime.now());
        sale.setStatus(SaleStatus.COMPLETED);
        sale.setStore(stores);
        sale.setNote(request.getNote());
        sale.setCreatedBy(createdBy);
        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;
        // =====================================
        // LOOP ITEMS
        // =====================================
        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productService.findById(itemRequest.getProductId());
            // =================================
            // DECREASE STOCK
            // =================================
            stockService.decreaseStock(
                    itemRequest.getProductId(),
                    request.getStoreId(),
                    itemRequest.getQuantity()
            );
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
        saleRepository.save(sale);
        return saleMapper.toResponse(sale);
    }

    @Override
    public SaleResponse getById(Long id) {
        return saleMapper.toResponse(findById(id));
    }

    @Override
    public Page<SaleResponse> getAll(Pageable pageable) {
        return saleRepository.findAll(pageable).map(saleMapper::toResponse);
    }

    @Override
    public Sale findById(Long id) {
        return saleRepository.findById(id).orElseThrow(()->new ResourceNotFoundExecption("Sale" , id));
    }

    @Override
    public SaleResponse updateStatus(Long id, SaleStatus status, String updatedBy) {
        Sale sale = findById(id);
        sale.setStatus(status);
        sale.setUpdatedBy(updatedBy);
        saleRepository.save(sale);
        return saleMapper.toResponse(sale);
    }

    @Override
    public void delete(Long id) {
        Sale sale = findById(id);
        saleRepository.delete(sale);
    }
}