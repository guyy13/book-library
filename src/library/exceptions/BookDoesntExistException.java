package library.exceptions;

public class BookDoesntExistException extends Exception{
    public BookDoesntExistException (String errorMessage) {
        super(errorMessage);
    }
}
