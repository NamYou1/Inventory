package yoyo.inventory.execption;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CloudinaryUploadException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final Throwable cause;

    public CloudinaryUploadException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
        this.cause = null;
    }

    public CloudinaryUploadException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.message = message;
        this.cause = cause;
    }
}
