package yoyo.inventory.execption;

public class SameStoreException extends RuntimeException {
    public SameStoreException(String message) {
        super(message);
    }

}