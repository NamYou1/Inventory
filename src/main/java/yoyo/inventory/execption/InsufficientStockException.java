package yoyo.inventory.execption;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends ApiExecption {
    public InsufficientStockException(String productName, int required, int available) {
        super(HttpStatus.UNPROCESSABLE_ENTITY,
                String.format("Insufficient stock for '%s': required %d, available %d",
                        productName, required, available));
    }
}

