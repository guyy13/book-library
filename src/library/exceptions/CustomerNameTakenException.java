package library.exceptions;

public class CustomerNameTakenException extends Exception{
    public CustomerNameTakenException (String errorMessage) {
        super(errorMessage);
    }
}
