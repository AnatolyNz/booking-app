package mate.academy.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long message) {
        super(String.valueOf(message));
    }
}
