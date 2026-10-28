package org.howard.edu.lsp.midterm.crccards;

import java.util.Objects;

/**
 * Stores a book's identity and controls its borrowing state.
 * A new book is available, and availability changes only through its methods.
 *
 * @author Brian Too
 */
public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private boolean available;

    /**
     * Creates an available book.
     *
     * @param isbn the book's unique identifier
     * @param title the book's title
     * @param author the author's name
     * @throws NullPointerException if any argument is null
     */
    public Book(String isbn, String title, String author) {
        this.isbn = Objects.requireNonNull(isbn, "isbn");
        this.title = Objects.requireNonNull(title, "title");
        this.author = Objects.requireNonNull(author, "author");
        this.available = true;
    }

    /**
     * Returns the book's identifier.
     * @return the ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Returns the book's title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the author's name.
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Reports whether the book can be borrowed.
     * @return true when the book is available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Marks an available book as borrowed.
     * @throws IllegalStateException if the book is already borrowed
     */
    public void borrowBook() {
        if (!available) {
            throw new IllegalStateException("Book is already borrowed: " + isbn);
        }
        available = false;
    }

    /**
     * Makes a borrowed book available again.
     * @throws IllegalStateException if the book is already available
     */
    public void returnBook() {
        if (available) {
            throw new IllegalStateException("Book is already available: " + isbn);
        }
        available = true;
    }

    /**
     * Describes the book and its current availability.
     * @return ISBN, title, author, and AVAILABLE or BORROWED status
     */
    @Override
    public String toString() {
        return isbn + " " + title + " by " + author
                + (available ? " [AVAILABLE]" : " [BORROWED]");
    }
}
