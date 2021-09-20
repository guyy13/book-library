package library.exceptions;

public class CustomerDoesntExistException extends Exception {
  public CustomerDoesntExistException(String errorMessage) {
    super(errorMessage);
  }
}
