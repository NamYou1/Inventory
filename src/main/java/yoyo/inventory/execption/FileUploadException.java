package yoyo.inventory.execption;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class FileUploadException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final Throwable cause;

    public FileUploadException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
        this.cause = null;
    }

    public FileUploadException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.message = message;
        this.cause = cause;
    }
}
