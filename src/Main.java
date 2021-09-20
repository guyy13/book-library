import library.Library;
import library.exceptions.BookDoesntExistException;
import library.exceptions.CustomerDoesntExistException;
import library.exceptions.CustomerNameInvalidException;
import library.exceptions.CustomerNameTakenException;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws Exception {
    Library library = new Library();
    try {
      library.buildLibrary();
      }
    catch (Exception e) {
      throw new Exception("Please make sure that the files: \"books.json\" and \"customers.json\" are accessible");
    }
    Scanner scanner = new Scanner(System.in);
    System.out.println("Welcome to Guy's library!");
    login(library);
    boolean run = true;
    while (run) {
      boolean admin = library.isAdmin();
      if (admin) {
        System.out.println("Welcome, what would you like to do?");
        boolean session = true;
        while (session) {
          showOptions(admin);
          String operation = scanner.nextLine();
          switch (operation) {
            case "1":
              printBookList(library);
              break;
            case "2":
              library.printCustomerListAndBorowedBooks();
              break;
            case "3":
              addBook(library, scanner);
              break;
            case "4":
              session = false;
              logout(library);
              break;
            case "5":
              session = false;
              run = false;
              exit(library);
              break;
            default:
              System.out.println("Invalid option. Please Try again.");
              break;
          }
        }
      } else {
        System.out.println("How can we help you today?");
        boolean session = true;
        while (session) {
          showOptions(admin);
          String operation = scanner.nextLine();
          switch (operation) {
            case "1":
              printBookList(library);
              break;
            case "2":
              borrowBook(library, scanner);
              break;
            case "3":
              returnBook(library, scanner);
              break;
            case "4":
              printBorrowedBooks(library);
              break;
            case "5":
              session = false;
              logout(library);
              break;
            case "6":
              session = false;
              run = false;
              exit(library);
              break;
            default:
              System.out.println("Invalid option. Please Try again.");
              break;
          }
        }
      }
    }
  }

  public static void login(Library library) {
    Scanner scanner = new Scanner(System.in);
    boolean loginSuccessful = false;
    while (!loginSuccessful) {
      System.out.println("Already a customer? Please enter your full name.");
      System.out.println("New customer? Please type CREATE.");
      String name = scanner.nextLine();
      if (name.equalsIgnoreCase("create")) {
        newCustomerCreation(name, scanner, library);
      } else {
        try {
          library.customerLogin(name);
          loginSuccessful = true;
        } catch (CustomerDoesntExistException error) {
          System.out.println("Sorry, no such customer exists.");
        }
      }
    }
  }

  public static void newCustomerCreation(String name, Scanner scanner, Library library) {
    System.out.println("Enter your full name:");
    boolean addedSuccessfully = false;
    boolean goBack = false;
    do {
      name = scanner.nextLine();
      if (name.equalsIgnoreCase("back")) {
        goBack = true;
      } else {
        try {
          library.addNewCustomer(name);
          addedSuccessfully = true;
        } catch (CustomerNameTakenException error) {
          System.out.println(
              "Customer already exists. Please re-type your name, or type BACK to go back.");
        } catch (CustomerNameInvalidException error) {
          System.out.println(
              "Invalid name. Please enter your name in the following template: firstName lastName");
        }
      }
    } while (!addedSuccessfully && !goBack);
    if (addedSuccessfully) {
      System.out.println("Registration completed. Welcome!");
    }
  }

  public static void showOptions(boolean isAdmin) {
    System.out.println("Choose one of the following options (use the numbers):\n");
    if (isAdmin) { // Admin options
      System.out.println(
          "1) See books list   2) See customers' names and borrowed books   3) Add new book   4) Switch User   5) Exit");
    } else { // User options
      System.out.println(
          "1) See books list   2) Borrow book   3) Return book   4) See which books you have borrowed   5) Switch User   6) Exit");
    }
  }

  public static void addBook(Library library, Scanner scanner) {
    System.out.println("Enter the book's title:");
    String title = scanner.nextLine();
    System.out.println("Enter the book's author:");
    String author = scanner.nextLine();
    System.out.println("Enter the book's genre:");
    String genre = scanner.nextLine();
    System.out.println("Enter the book's year of release:");
    int yearOfRelease = parseIntegerFromInput(scanner);
    System.out.println("How many copies?");
    int copiesAvailable = parseIntegerFromInput(scanner);
    library.addBookToLibrary(title, genre, author, yearOfRelease, copiesAvailable);
    System.out.println("Book added successfully!\n");
  }

  public static int parseIntegerFromInput(Scanner scanner) {
    int num = -1;
    boolean numberRead = false;
    while (!numberRead) {
      try {
        num = Integer.parseInt(scanner.nextLine());
        numberRead = true;
      } catch (Exception e) {
        System.out.println("Invalid input, please enter a valid number:");
      }
    }
    return num;
  }

  public static void printBookList(Library library) {
    System.out.println("Here is the book list:");
    library.printListOfBooks();
  }

  public static void logout(Library library) {
    System.out.println("See ya!\n");
    login(library);
  }

  public static void exit(Library library) {
    library.closeLibrary();
    System.out.println("Bye!");
  }

  public static void borrowBook(Library library, Scanner scanner) {
    boolean bookBorrowedSuccessfully = false;
    boolean goBack = false;
    while (!bookBorrowedSuccessfully && !goBack) {
      System.out.println("Enter book title: ");
      String bookTitle = scanner.nextLine();
      if (bookTitle.equalsIgnoreCase("back")) {
        goBack = true;
      }
      else if (!library.isBookAvailable(bookTitle)) {
        System.out.println("Sorry, this book is unavailable at the moment.");
        System.out.println("You can try again or type \"BACK\" to go back to the main menu");
      }
      else if (library.customerHasBook(bookTitle)) {
        System.out.println("Sorry, you are only allowed one copy of each book at a time.");
        System.out.println("You can try again or type \"BACK\" to go back to the main menu");
      }
      else {
        library.borrowBook(bookTitle);
        bookBorrowedSuccessfully = true;
        System.out.println("Book borrowed successfully, enjoy your reading!");
      }
    }
  }

  public static void returnBook(Library library, Scanner scanner) {
    if (!library.getActiveCustomer().hasBooks()) {
      System.out.println("I see you haven't borrowed any books so there's nothing to return!");
      return;
    }
    boolean bookReturned = false;
    boolean goBack = false;
    while (!bookReturned && !goBack) {
      System.out.println("Enter book title: ");
      String bookTitle = scanner.nextLine();
      if (bookTitle.equalsIgnoreCase("back")) {
        goBack = true;
      } else if (!library.customerHasBook(bookTitle)) {
        System.out.println("Sorry, you cannot return a book that you don't have...");
        System.out.println("You can try again or type \"BACK\" to go back to the main menu");
      } else {
        try {
          library.returnBook(bookTitle);
          bookReturned = true;
          System.out.println("Thanks! Hope you enjoyed it.");
        } catch (BookDoesntExistException error) {
          System.out.println("Sorry, but I don't think this book is ours.");
          System.out.println("You can try again or type \"BACK\" to go back to the main menu");
        }
      }
    }
  }

  public static void printBorrowedBooks(Library library) {
    if (!library.getActiveCustomer().hasBooks()) {
      System.out.println("It looks like you haven't borrowed any books yet.");
    } else {
      System.out.println("These are the books you currently have:");
      library.printCustomersBorrowedBooks();
    }
  }
}
