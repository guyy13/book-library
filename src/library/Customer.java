package library;

import java.util.HashSet;
import java.util.Set;

public class Customer {
  private String firstName;
  private String lastName;
  private Set<Book> booksBorrowed;

  public Customer(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.booksBorrowed = new HashSet<>();
  }

  public Customer(String firstName, String lastName, Set<Book> booksBorrowed) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.booksBorrowed = booksBorrowed;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Set<Book> getBooksBorrowed() {
    return booksBorrowed;
  }

  public boolean alreadyBorrowed(Book book) {
    if (booksBorrowed.contains(book)) {
      return true;
    }
    return false;
  }

  public void addBorrowedBook(Book book) {
    booksBorrowed.add(book);
  }

  public void returnBorrowedBook(Book book) {
    booksBorrowed.remove(book);
  }

  public void printBorrowedBooks() {
    for (Book book : booksBorrowed) {
      System.out.println(book.getTitle());
    }
  }

  public boolean hasBooks() {
    return !booksBorrowed.isEmpty();
  }

  public String getFullNameLowerCase() {
    String lowerFistrName = this.firstName.toLowerCase();
    String lowerLastName = this.lastName.toLowerCase();
    return lowerFistrName.concat(" ").concat(lowerLastName);
  }

  public String getFullName() {
    return this.firstName.concat(" ").concat(this.lastName);
  }
}
