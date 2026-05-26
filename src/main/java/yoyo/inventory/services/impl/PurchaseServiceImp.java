package yoyo.inventory.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.InvoiceService;
import yoyo.inventory.common.PageUtil;
import yoyo.inventory.dto.request.PurchaseItemRequest;
import yoyo.inventory.dto.request.PurchaseRequest;
import yoyo.inventory.dto.response.PurchaseResponse;
import yoyo.inventory.entities.Product;
import yoyo.inventory.entities.PurchaseItem;
import yoyo.inventory.entities.Purchases;
import org.springframework.http.HttpStatus;
import yoyo.inventory.entities.Stores;
import yoyo.inventory.entities.status.PaymentStatus;
import yoyo.inventory.entities.status.PurchaseStatus;
import yoyo.inventory.entities.status.Status;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;
import yoyo.inventory.execption.ApiException;
import yoyo.inventory.execption.ResourceNotFoundException;
import yoyo.inventory.mappers.PurchaseMapper;
import yoyo.inventory.repository.PurchaseRepository;
import yoyo.inventory.services.ProductService;
import yoyo.inventory.services.PurchaseService;
import yoyo.inventory.services.StockService;
import yoyo.inventory.services.StoreService;
import yoyo.inventory.services.TransactionService;
import yoyo.inventory.specification.purchase.PurchaseFilter;
import yoyo.inventory.specification.purchase.PurchaseSpec;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PurchaseServiceImp implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;
    private final InvoiceService invoiceService;
    private final ProductService productService;
    private final StoreService storeService;
    private final StockService stockService;
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
//    @CacheEvict(cacheNames = {"purchase-page", "purchase-response", "purchase-entity", "txn-summary"}, allEntries = true)
    public PurchaseResponse create(PurchaseRequest request) {
        Purchases purchase = purchaseMapper.toEntityPurchase(request);
        purchase.setPurchaseStatus(PurchaseStatus.ORDERED);
        purchase.setPaymentStatus(PaymentStatus.PENDING);
        purchase.setStatus(Status.ACTIVE);
        purchase.setNo(invoiceService.generate("PUR"));

        Stores store = storeService.findById(request.getStoreId());
        purchase.setTblStore(store);

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<PurchaseItem> items = new ArrayList<>();

        for (PurchaseItemRequest itemRequest : request.getItems()) {
            Product product = productService.findById(itemRequest.getProductId());
            PurchaseItem item = purchaseMapper.toItemEntity(itemRequest);
            item.setTblProduct(product);
            item.setTblPurchase(purchase);

            BigDecimal costPrice = product.getCostPrice() != null ? product.getCostPrice() : BigDecimal.ZERO;
            item.setCostPrice(costPrice);

            BigDecimal subtotal = itemRequest.getQuantity().multiply(costPrice);
            item.setSubtotal(subtotal);

            BigDecimal itemDiscount = itemRequest.getTotalDiscount() != null
                    ? BigDecimal.valueOf(itemRequest.getTotalDiscount())
                    : BigDecimal.ZERO;

            total = total.add(subtotal);
            totalDiscount = totalDiscount.add(itemDiscount);
            items.add(item);
        }

        BigDecimal orderDiscount = request.getOrderDiscount() != null
                ? BigDecimal.valueOf(request.getOrderDiscount())
                : BigDecimal.ZERO;
        totalDiscount = totalDiscount.add(orderDiscount);

        purchase.setTotal(total);
        purchase.setTotalDiscount(totalDiscount);
        purchase.setGrandTotal(total.subtract(totalDiscount));
        purchase.setTblPurchaseItem(items);

        return purchaseMapper.toResponsePurchase(purchaseRepository.save(purchase));
    }

    @Override
//    @Cacheable(cacheNames = "purchase-page", key = "#params.toString()")
    public Page<PurchaseResponse> getAll(Map<String, String> params) {
        PurchaseFilter filter = objectMapper.convertValue(params, PurchaseFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Purchases> spec = PurchaseSpec.filterBy(filter);
        return purchaseRepository.findAll(spec, pageable).map(purchaseMapper::toResponsePurchase);
    }

    @Override
//    @Cacheable(cacheNames = "purchase-response", key = "#id")
    public PurchaseResponse getById(Long id ) {
        return purchaseMapper.toResponsePurchase(findPurchaseById(id));
    }

    @Override
    @Transactional
//    @CacheEvict(cacheNames = {"purchase-page", "purchase-response", "purchase-entity", "txn-summary"}, allEntries = true)
    public PurchaseResponse update(Long id, PurchaseRequest request, String updatedBy) {
        Purchases purchase = findPurchaseById(id);
        if (purchase.getPurchaseStatus() != PurchaseStatus.ORDERED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only ORDERED purchase can be updated");
        }

        purchase.setNote(request.getNote());
        if (request.getDate() != null) {
            purchase.setDate(request.getDate());
        }
        purchase.setUpdatedBy(updatedBy);
        purchase.setUpdatedAt(LocalDateTime.now());
        return purchaseMapper.toResponsePurchase(purchaseRepository.save(purchase));
    }

    @Override
    @Transactional
//    @CacheEvict(cacheNames = {"purchase-page", "purchase-response", "purchase-entity", "txn-summary"}, allEntries = true)
    public PurchaseResponse approve(Long id, String updatedBy) {
        Purchases purchase = findPurchaseById(id);
        if (purchase.getPurchaseStatus() != PurchaseStatus.ORDERED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only ORDERED purchase can be approved");
        }

        purchase.setPurchaseStatus(PurchaseStatus.APPROVED);
        purchase.setUpdatedBy(updatedBy);
        purchase.setUpdatedAt(LocalDateTime.now());
        return purchaseMapper.toResponsePurchase(purchaseRepository.save(purchase));
    }

    @Override
    @Transactional
//    @CacheEvict(cacheNames = {"purchase-page", "purchase-response", "purchase-entity", "txn-summary"}, allEntries = true)
    public PurchaseResponse complete(Long id, String updatedBy) {
        Purchases purchase = findPurchaseById(id);
        if (purchase.getPurchaseStatus() != PurchaseStatus.APPROVED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only APPROVED purchase can be completed");
        }

        Long storeId = getStoreIdOrThrow(purchase);
        for (PurchaseItem item : purchase.getTblPurchaseItem()) {
            stockService.increaseStock(
                    item.getTblProduct().getId(),
                    storeId,
                    item.getQuantity(),
                    item.getCostPrice()
            );

            transactionService.logStockMovement(
                    TransactionType.PURCHASE,
                    purchase.getId(),
                    purchase.getNo(),
                    item.getTblProduct().getId(),
                    storeId,
                    resolveUnitId(item),
                    item.getQuantity(),
                    BigDecimal.ONE,
                    item.getCostPrice(),
                    SaleStatus.COMPLETED,
                    updatedBy
            );
        }

        purchase.setPurchaseStatus(PurchaseStatus.COMPLETED);
        purchase.setPaymentStatus(PaymentStatus.PAID);
        purchase.setUpdatedBy(updatedBy);
        purchase.setUpdatedAt(LocalDateTime.now());
        return purchaseMapper.toResponsePurchase(purchaseRepository.save(purchase));
    }

    @Override
    @Transactional
//    @CacheEvict(cacheNames = {"purchase-page", "purchase-response", "purchase-entity", "txn-summary"}, allEntries = true)
    public PurchaseResponse cancel(Long id, String updatedBy) {
        Purchases purchase = findPurchaseById(id);
        if (purchase.getPurchaseStatus() == PurchaseStatus.CANCELLED
                || purchase.getPurchaseStatus() == PurchaseStatus.RETURNED
                || purchase.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Purchase already cancelled/refunded");
        }

        if (purchase.getPurchaseStatus() == PurchaseStatus.COMPLETED) {
            reverseStockAndSaveTransactions(purchase, updatedBy);
        }

        purchase.setPurchaseStatus(PurchaseStatus.RETURNED);
        purchase.setPaymentStatus(PaymentStatus.REFUNDED);
        purchase.setUpdatedBy(updatedBy);
        purchase.setUpdatedAt(LocalDateTime.now());
        return purchaseMapper.toResponsePurchase(purchaseRepository.save(purchase));
    }

    @Override
    @Transactional
//    @CacheEvict(cacheNames = {"purchase-page", "purchase-response", "purchase-entity", "txn-summary"}, allEntries = true)
    public void delete(Long id, String deletedBy) {
        Purchases purchase = findPurchaseById(id);
        if (purchase.getStatus() == Status.INACTIVE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Purchase already deleted");
        }
        if (purchase.getPurchaseStatus() == PurchaseStatus.COMPLETED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot delete completed purchase");
        }

        purchase.setStatus(Status.INACTIVE);
        purchase.setDeletedBy(deletedBy);
        purchase.setDeletedAt(LocalDateTime.now());
        purchase.setUpdatedBy(deletedBy);
        purchase.setUpdatedAt(LocalDateTime.now());
        purchaseRepository.save(purchase);
    }

    private Purchases findPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase", id));
    }

    private Long getStoreIdOrThrow(Purchases purchase) {
        if (purchase.getTblStore() == null || purchase.getTblStore().getId() == null) {
            throw new IllegalStateException("Purchase store is required");
        }
        return purchase.getTblStore().getId();
    }

    private void reverseStockAndSaveTransactions(Purchases purchase, String updatedBy) {
        Long storeId = getStoreIdOrThrow(purchase);
        for (PurchaseItem item : purchase.getTblPurchaseItem()) {
            stockService.decreaseStock(item.getTblProduct().getId(), storeId, item.getQuantity());

            transactionService.logStockMovement(
                    TransactionType.ADJUSTMENT_OUT,
                    purchase.getId(),
                    purchase.getNo(),
                    item.getTblProduct().getId(),
                    storeId,
                    resolveUnitId(item),
                    item.getQuantity(),
                    BigDecimal.ONE,
                    item.getCostPrice(),
                    SaleStatus.CANCELLED,
                    updatedBy
            );
        }
    }

    private Long resolveUnitId(PurchaseItem item) {
        if (item.getTblUnit() != null && item.getTblUnit().getId() != null) {
            return item.getTblUnit().getId();
        }
        if (item.getTblProduct() != null
                && item.getTblProduct().getTblUnit() != null
                && item.getTblProduct().getTblUnit().getId() != null) {
            return item.getTblProduct().getTblUnit().getId();
        }
        throw new IllegalStateException("Unit is required to log purchase transaction for product " + item.getTblProduct().getId());
    }
}
