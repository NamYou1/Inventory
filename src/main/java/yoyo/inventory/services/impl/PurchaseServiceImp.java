package yoyo.inventory.services.impl;

import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.InvoiceService;
import yoyo.inventory.common.PageUtil;
import yoyo.inventory.dto.request.PurchaseItemRequest;
import yoyo.inventory.dto.request.PurchaseRequest;
import yoyo.inventory.dto.response.PurchaseResponse;
import yoyo.inventory.entities.*;
import yoyo.inventory.entities.status.PaymentStatus;
import yoyo.inventory.entities.status.PurchaseStatus;
import yoyo.inventory.enums.SaleStatus;
import yoyo.inventory.enums.TransactionType;
import yoyo.inventory.mappers.PurchaseMapper;
import yoyo.inventory.repository.PurchaseItemRepository;
import yoyo.inventory.repository.PurchaseRepository;
import yoyo.inventory.repository.StockRepository;
import yoyo.inventory.services.*;
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
    private  final ProductService productService ;
    private  final StoreService storeService ;
    private  final StockService stockService ;
    private  final TransactionService transactionService ;
    private final ObjectMapper objectMapper;
    @Transactional
    @Override
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        Purchases purchases  = purchaseMapper.toEntityPurchase(request);
//        purchases.setNo(invoiceService.generate("PUR" , purchases.getId()));
        purchases.setPurchaseStatus(PurchaseStatus.ORDERED);
        purchases.setPaymentStatus(PaymentStatus.PENDING);
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        Stores stores = storeService.findById(request.getStoreId());

        List<PurchaseItem> purchaseItems = new ArrayList<>();
        for (PurchaseItemRequest itemRequest : request.getItems()){
            // find product
            Product product = productService.findById(itemRequest.getProductId());
            PurchaseItem purchaseItem = purchaseMapper.toItemEntity(itemRequest);
            // Set all required relationships and fields
            purchaseItem.setTblProduct(product);
//            purchaseItem.setTblStore(stores);
            purchaseItem.setTblPurchase(purchases);
            BigDecimal costPrice  = product.getCostPrice() != null ? product.getCostPrice() : BigDecimal.ZERO;
            purchaseItem.setCostPrice(costPrice);

            BigDecimal subTotal = itemRequest.getQuantity().multiply(costPrice);
            BigDecimal itemDiscount = itemRequest.getTotalDiscount() != null
                ? BigDecimal.valueOf(itemRequest.getTotalDiscount())
                : BigDecimal.ZERO;
            purchaseItems.add(purchaseItem);
    // Calculate Total
            purchaseItem.setSubtotal(subTotal);
            total = total.add(subTotal);
            totalDiscount = totalDiscount.add(itemDiscount);
    // update stock
            stockService.increaseStock(itemRequest.getProductId(), request.getStoreId() , itemRequest.getQuantity() ,costPrice);
        }




        // Add order-level discount
        BigDecimal orderDiscount = request.getOrderDiscount() != null ? BigDecimal.valueOf(request.getOrderDiscount()) : BigDecimal.ZERO;
        totalDiscount = totalDiscount.add(orderDiscount);

        // Calculate grand total: total - totalDiscount
        BigDecimal grandTotal = total.subtract(totalDiscount);

        // Set totals on purchase

        purchases.setTotal(total);
        purchases.setTotalDiscount(totalDiscount);
        purchases.setGrandTotal(grandTotal);
        purchases.setTblPurchaseItem(purchaseItems);

        Purchases savedPurchase = purchaseRepository.save(purchases);
        String generatedInvoiceNo = invoiceService.generate("PUR", purchases.getId());
        purchases.setNo(generatedInvoiceNo);
        purchaseRepository.save(purchases);


        for (PurchaseItem savedItem : savedPurchase.getTblPurchaseItem()) {
            transactionService.logStockMovement(
                    TransactionType.PURCHASE,
                    savedPurchase.getId(),                 // ពេលនេះមាន ID ពិតប្រាកដហើយ (មិន NULL)
                    savedPurchase.getNo(),                 // មានលេខវិក្កយបត្រពិតប្រាកដ (មិន NULL)
                    savedItem.getTblProduct().getId(),
                    request.getStoreId(),
                    savedItem.getTblUnit().getId(),        // យក Unit ID ចេញពី Item
                    savedItem.getQuantity(),
                    BigDecimal.ONE,                        // unitQuantity default 1
                    savedItem.getCostPrice(),
                    SaleStatus.COMPLETED,
                    "CURRENT_USER_ID"                      // ឈ្មោះអ្នកបង្កើត
            );
        }
        return purchaseMapper.toResponsePurchase(savedPurchase);
    }




//    private void updateProductStock(Long productId, Long storeId, Double quantity) {
//        Product product = productService.findById(productId);
//        Stores store = storeService.findById(storeId);
//
//        Stock stock = stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)
//                .orElseGet(() -> {
//                    Stock newStock = new Stock();
//                    newStock.setTblProduct(product);
//                    newStock.setTblStore(store);
//                    newStock.setQuantity(BigDecimal.ZERO);
//                    return newStock;
//                });
//        stock.setQuantity(stock.getQuantity().add(BigDecimal.valueOf(quantity)));
//        stock.setLastRestockDate(LocalDateTime.now());
//        stockRepository.save(stock);
//    }



    @Override
    public Page<PurchaseResponse> getAllPurchases(Map<String, String> params) {
        PurchaseFilter filter = objectMapper.convertValue(params, PurchaseFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        
        Specification<Purchases> spec = PurchaseSpec.filterBy(filter);
        return purchaseRepository.findAll(spec, pageable).map(purchaseMapper::toResponsePurchase);
    }

    @Override
    public PurchaseResponse getPurchaseById(Long id) {
        return null;
    }

    @Override
    public PurchaseResponse cancelPurchase(Long id) {
        return null;
    }
}
