package library.exceptions;

public class CustomerNameInvalidException extends Exception {
    public CustomerNameInvalidException (String errorMessage) {
        super(errorMessage);
    }
}
