package yoyo.inventory.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import yoyo.inventory.common.InvoiceService;
import yoyo.inventory.dto.request.PurchaseRequest;
import yoyo.inventory.dto.response.PurchaseResponse;
import yoyo.inventory.entities.PurchaseItem;
import yoyo.inventory.entities.Purchases;
import yoyo.inventory.entities.Stock;
import yoyo.inventory.mappers.PurchaseMapper;
import yoyo.inventory.repository.PurchaseRepository;
import yoyo.inventory.repository.StockRepository;
import yoyo.inventory.services.PurchaseService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PurchaseServiceImp implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final StockRepository stockRepository;
    private final PurchaseMapper purchaseMapper;
    private final InvoiceService invoiceService;

    @Override
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        Purchases purchases = purchaseMapper.toEntityPurchase(request);
        purchases.setNo(invoiceService.generate("PUR"));
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItem item : purchases.getTblPurchaseItem()) {
            item.setTblPurchase(purchases);
            BigDecimal subTotal = item.getCostPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            item.setSubtotal(subTotal);
            total = total.add(subTotal);
        }
        purchases.setGrandTotal(total);
        Purchases savedPurchase = purchaseRepository.save(purchases);
        for (PurchaseItem item : savedPurchase.getTblPurchaseItem()) {
            Long productId = item.getTblProduct().getId();
            Long storeId = item.getTblStore() != null ? item.getTblStore().getId() : null;
            Stock stock;
            if (storeId != null) {
                stock = stockRepository.findByTblProductIdAndTblStoreId(productId, storeId)
                        .orElseGet(() -> {
                            Stock s = new Stock();
                            s.setTblProduct(item.getTblProduct());
                            s.setTblStore(item.getTblStore());
                            s.setQuantity(BigDecimal.ZERO);
                            s.setReorderLevel(0);
                            return s;
                        });
            } else {
                stock = stockRepository.findByTblProductId(productId)
                        .orElseGet(() -> {
                            Stock s = new Stock();
                            s.setTblProduct(item.getTblProduct());
                            s.setQuantity(BigDecimal.ZERO);
                            s.setReorderLevel(0);
                            return s;
                        });
            }
            stock.setQuantity(stock.getQuantity().add(BigDecimal.valueOf(item.getQuantity())));
            stock.setLastRestockDate(LocalDateTime.now());
            stockRepository.save(stock);
        }
        return purchaseMapper.toResponsePurchase(savedPurchase);
    }

    @Override
    public Page<PurchaseResponse> getAllPurchases(Map<String, String> params) {
        return null;
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
