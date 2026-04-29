package yoyo.inventory.services;

import org.springframework.data.domain.Page;
import yoyo.inventory.dto.request.PurchaseRequest;
import yoyo.inventory.dto.response.PurchaseResponse;

import java.util.Map;

public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest request);
    Page<PurchaseResponse> getAllPurchases(Map<String, String> params);
    PurchaseResponse getPurchaseById(Long id);
    /** Mark as PAID (or any other status). Cannot update a REFUNDED purchase. */
//    PurchaseResponse updatePaymentStatus(Long id, PaymentStatusUpdateRequest request);
    /** Reverse all stock additions from this purchase and mark it REFUNDED (cancelled). */
    PurchaseResponse cancelPurchase(Long id);
}
