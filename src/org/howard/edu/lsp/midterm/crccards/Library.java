package org.howard.edu.lsp.midterm.crccards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Maintains books by ISBN and coordinates lending with a three-book limit.
 * Books in this library must be borrowed and returned through this class.
 * A TreeMap keeps ISBNs in natural String order; returned lists are independent
 * containers holding the original Book instances.
 *
 * @author Brian Too
 */
public class Library {
    private static final int BORROWING_LIMIT = 3;
    private final Map<String, Book> books = new TreeMap<>();

    /** Creates an empty library. */
    public Library() {
    }

    /**
     * Adds a book without replacing an existing ISBN.
     * @param book the book to add
     * @throws NullPointerException if book is null
     * @throws IllegalArgumentException if its ISBN is already present
     * @throws IllegalStateException if adding a borrowed book would exceed the limit
     */
    public void addBook(Book book) {
        Objects.requireNonNull(book, "book");
        if (books.containsKey(book.getIsbn())) {
            throw new IllegalArgumentException("Duplicate ISBN: " + book.getIsbn());
        }
        if (!book.isAvailable() && getBorrowedBookCount() >= BORROWING_LIMIT) {
            throw new IllegalStateException("Three books are already borrowed");
        }
        books.put(book.getIsbn(), book);
    }

    /**
     * Finds the original book object associated with an ISBN.
     * @param isbn the identifier to find; null matches no book
     * @return the matching book, or null if absent
     */
    public Book findBook(String isbn) {
        return isbn == null ? null : books.get(isbn);
    }

    /**
     * Locates a book, checks the library limit, and asks the book to borrow itself.
     * @param isbn the identifier of the book to borrow
     * @throws IllegalArgumentException if the ISBN is absent
     * @throws IllegalStateException if already borrowed or the limit is reached
     */
    public void borrowBook(String isbn) {
        Book book = requireBook(isbn);
        if (getBorrowedBookCount() >= BORROWING_LIMIT) {
            throw new IllegalStateException("Three books are already borrowed");
        }
        book.borrowBook();
    }

    /**
     * Locates a book and delegates its return to the book.
     * @param isbn the identifier of the book to return
     * @throws IllegalArgumentException if the ISBN is absent
     * @throws IllegalStateException if the book is already available
     */
    public void returnBook(String isbn) {
        requireBook(isbn).returnBook();
    }

    /**
     * Retrieves all books currently available for borrowing.
     * @return a new list of available books, empty if none are available
     */
    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    /**
     * Retrieves available and borrowed books by exact, case-sensitive author name.
     * @param author the name to match; null matches no book
     * @return a new list of matching books, empty if none match
     */
    public List<Book> getBooksByAuthor(String author) {
        List<Book> matches = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getAuthor().equals(author)) {
                matches.add(book);
            }
        }
        return matches;
    }

    /**
     * Retrieves every book in ascending lexicographical ISBN order.
     * This operation does not change library contents or book states.
     * @return a new sorted list, empty if this library is empty
     */
    public List<Book> getBooksSortedByIsbn() {
        return new ArrayList<>(books.values());
    }

    /**
     * Counts borrowed books from their current states instead of duplicating state.
     * @return the number of books currently borrowed
     */
    public int getBorrowedBookCount() {
        int count = 0;
        for (Book book : books.values()) {
            if (!book.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    private Book requireBook(String isbn) {
        Book book = findBook(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Unknown ISBN: " + isbn);
        }
        return book;
    }
}
