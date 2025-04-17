package mate.academy.exception;

public class StripeSessionException extends RuntimeException {

    public StripeSessionException(String message) {
        super(message);
    }

    public StripeSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
