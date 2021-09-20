package library;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import library.exceptions.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.json.simple.JSONArray;

public class Library {
  private HashMap<Book, Integer> booksAndAvailability = new HashMap<>();
  private HashMap<String, Book> bookTitleToObject = new HashMap<>();
  private HashMap<String, Customer> customerNameToObject = new HashMap<>();
  private String activeCustomer;

  public void buildLibrary() throws Exception {
    loadBooksFromJson();
    loadCustomersFromJson();
  }

  private void loadBooksFromJson() throws IOException, ParseException {
    JSONParser jsonParser = new JSONParser();
    FileReader reader = new FileReader("jsonFiles\\books.json");
    JSONArray bookList = (JSONArray) jsonParser.parse(reader);
    for (int i = 0; i < bookList.size(); i++) {
      JSONObject jsonBook = (JSONObject) bookList.get(i);
      JSONObject jsonBookObject = (JSONObject) jsonBook.get("book");
      Book curBook = parseBookObject(jsonBookObject);
      booksAndAvailability.put(
          curBook, Integer.parseInt((String) jsonBookObject.get("availability")));
    }
    for (Book book : booksAndAvailability.keySet()) {
      bookTitleToObject.put(book.getTitle().toLowerCase(), book);
    }
  }

  private void loadCustomersFromJson() throws Exception {
    JSONParser jsonParser = new JSONParser();
    FileReader reader = new FileReader("jsonFiles\\customers.json");
    JSONArray customerList = (JSONArray) jsonParser.parse(reader);
    for (int i = 0; i < customerList.size(); i++) {
      JSONObject jsonCustomer = (JSONObject) customerList.get(i);
      JSONObject jsonCustomerObject = (JSONObject) jsonCustomer.get("customer");
      Customer curCustomer = parseCustomerObject(jsonCustomerObject);
      customerNameToObject.put(curCustomer.getFullNameLowerCase(), curCustomer);
    }
  }

  private Book parseBookObject(JSONObject book) {
    String title = (String) book.get("title");
    String author = (String) book.get("author");
    String genre = (String) book.get("genre");
    String yearOfRelease = (String) book.get("yearOfRelease");
    return new Book(title, author, genre, Integer.parseInt(yearOfRelease));
  }

  private Customer parseCustomerObject(JSONObject customer) {
    String firstName = ((String) customer.get("firstName")).toLowerCase();
    String lastName = ((String) customer.get("lastName")).toLowerCase();
    JSONArray booksArray = (JSONArray) customer.get("booksBorrowed");
    Set<Book> booksBorrowed = new HashSet<>();
    for (Object book : booksArray) {
      String bookTitle = (String) book;
      booksBorrowed.add(bookTitleToObject.get(bookTitle.toLowerCase()));
    }
    return new Customer(firstName, lastName, booksBorrowed);
  }

  public void closeLibrary() {
    updateBooksJson();
    updateCustomerJson();
  }

  private void updateBooksJson() {
    JSONArray bookList = new JSONArray();
    for (Book book : booksAndAvailability.keySet()) {
      JSONObject bookObject = new JSONObject();
      JSONObject bookData = new JSONObject();
      bookData.put("title", book.getTitle());
      bookData.put("author", book.getAuthor());
      bookData.put("genre", book.getGenre());
      bookData.put("yearOfRelease", Integer.toString(book.getYearOfRelease()));
      bookData.put("availability", Integer.toString(booksAndAvailability.get(book)));
      bookObject.put("book", bookData);
      bookList.add(bookObject);
    }
    try (FileWriter file = new FileWriter("jsonFiles\\books.json")) {
      file.write(bookList.toJSONString());
      file.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void updateCustomerJson() {
    JSONArray customerList = new JSONArray();
    for (Customer customer : customerNameToObject.values()) {
      JSONObject customerObject = new JSONObject();
      JSONObject customerData = new JSONObject();
      JSONArray booksBorrowed = new JSONArray();
      for (Book book : customer.getBooksBorrowed()) {
        String bookTitle = book.getTitle();
        booksBorrowed.add(bookTitle);
      }
      customerData.put("firstName", customer.getFirstName());
      customerData.put("lastName", customer.getLastName());
      customerData.put("booksBorrowed", booksBorrowed);
      customerObject.put("customer", customerData);
      customerList.add(customerObject);
    }
    try (FileWriter file = new FileWriter("jsonFiles\\customers.json")) {
      file.write(customerList.toJSONString());
      file.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Customer getActiveCustomer() {
    return customerNameToObject.get(activeCustomer);
  }

  public boolean isAdmin() {
    if (activeCustomer.equals("admin admin")) return true;
    return false;
  }

  public void customerLogin(String name) throws CustomerDoesntExistException {
    name = name.toLowerCase();
    if (customerExists(name)) {
      this.activeCustomer = name;
    } else {
      throw new CustomerDoesntExistException("library.Customer doesn't exist");
    }
  }

  public void printListOfBooks() {
    for (Book book : booksAndAvailability.keySet()) {
      System.out.println(
          "Title: "
              + book.getTitle()
              + "; Author: "
              + book.getAuthor()
              + "; Copies available: "
              + booksAndAvailability.get(book));
    }
    System.out.println();
  }

  public void borrowBook(String bookTitle) {
    bookTitle = bookTitle.toLowerCase();
    Customer customer = customerNameToObject.get(activeCustomer);
    Book book = bookTitleToObject.get(bookTitle);
    booksAndAvailability.put(book, booksAndAvailability.get(book) - 1);
    customer.addBorrowedBook(book);
  }

  public boolean isBookAvailable(String bookTitle) {
    bookTitle = bookTitle.toLowerCase();
    Book book = bookTitleToObject.get(bookTitle);
    if (book != null && booksAndAvailability.get(book) > 0) {
      return true;
    }
    return false;
  }

  public void returnBook(String bookTitle) throws BookDoesntExistException {
    Book book = bookTitleToObject.get(bookTitle.toLowerCase());
    Customer customer = customerNameToObject.get(activeCustomer);
    if (book == null) {
      throw new BookDoesntExistException("library.Book doesn't exist.");
    }
    booksAndAvailability.put(book, booksAndAvailability.get(book) + 1);
    customer.returnBorrowedBook(book);
  }

  public boolean customerHasBook(String bookTitle) {
    Book book = bookTitleToObject.get(bookTitle.toLowerCase());
    Customer customer = customerNameToObject.get(activeCustomer);
    return customer.alreadyBorrowed(book);
  }

  public void addNewCustomer(String name)
      throws CustomerNameTakenException, CustomerNameInvalidException {
    name = name.toLowerCase();
    if (customerExists(name)) {
      throw new CustomerNameTakenException("Customer already exists.");
    }
    if (!isValidName(name)) {
      throw new CustomerNameInvalidException("Invalid name.");
    } else {
      Customer customer =
          new Customer(name.substring(0, name.indexOf(' ')), name.substring(name.indexOf(' ') + 1));
      customerNameToObject.put(name, customer);
    }
  }

  public boolean isValidName(String name) {
    if (name.indexOf(' ') != -1 && Character.isAlphabetic(name.charAt(0))) return true;
    return false;
  }

  public boolean customerExists(String name) {
    if (customerNameToObject.get(name) != null) return true;
    return false;
  }

  public void printCustomersBorrowedBooks() {
    Customer customer = customerNameToObject.get(activeCustomer);
    customer.printBorrowedBooks();
  }

  public void printCustomerListAndBorowedBooks() {
    for (Customer customer : customerNameToObject.values()) {
      if (!customer.getFullName().equals("admin admin")) {
        System.out.println(customer.getFullName() + "'s borrowed books:");
        if (customer.hasBooks()) {
          customer.printBorrowedBooks();
        } else {
          System.out.println("NO BOOKS BORROWED");
        }
        System.out.println();
      }
    }
  }

  public void addBookToLibrary(
      String title, String author, String genre, int yearOfRelase, int copiesAvailable) {
    Book book = new Book(title, author, genre, yearOfRelase);
    booksAndAvailability.put(book, copiesAvailable);
    bookTitleToObject.put(title, book);
  }
}
