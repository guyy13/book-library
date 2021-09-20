package library;

public class Book {
  private String title;
  private String author;
  private String genre;
  private int yearOfRelease;

  public Book(String title, String author, String genre, int yearOfRelease) {
    this.title = title;
    this.author = author;
    this.genre = genre;
    this.yearOfRelease = yearOfRelease;
  }

  //Getters
  public String getTitle() {
    return this.title;
  }

  public String getAuthor() {
    return this.author;
  }

  public String getGenre() {
    return genre;
  }

  public int getYearOfRelease() {
    return this.yearOfRelease;
  }

  //Setters
  public void setAuthor(String author) {
    this.author = author;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setYearOfRelease(int yearOfRelease) {
    this.yearOfRelease = yearOfRelease;
  }
}
