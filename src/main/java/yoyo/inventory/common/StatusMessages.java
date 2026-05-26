package yoyo.inventory.common;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StatusMessages {
    PURCHASE_ONLY_ORDERED_UPDATE("Only ORDERED purchase can be updated"),
    PURCHASE_ALREADY_RECEIVED("Purchase already received"),
    PURCHASE_ALREADY_CANCELLED("Purchase already cancelled"),
    // SALE
    SALE_ONLY_PENDING_CANCEL("Only PENDING sale can be cancelled"),

    SALE_ALREADY_PAID("Sale already paid"),
    // TRANSFER
    TRANSFER_ALREADY_COMPLETED("Transfer already completed"),
    TRANSFER_ONLY_PENDING_APPROVE("Only PENDING transfer can be approved"),
    // ADJUSTMENT
    ADJUSTMENT_ALREADY_APPROVED("Adjustment already approved"),
    // COMMON
    INVALID_STATUS("Invalid status");
    private final String message;
}
