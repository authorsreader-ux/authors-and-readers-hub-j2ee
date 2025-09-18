package com.example.hub;

import com.example.hub.entity.Author;
import com.example.hub.entity.Book;
import com.example.hub.service.AuthorService;
import com.example.hub.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class AuthorsAndReadersHubApplicationTests {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
        assertNotNull(authorService);
        assertNotNull(bookService);
    }

    @Test
    void testAuthorCreation() {
        Author author = new Author("Test Author", "test@example.com", "A test author bio");
        Author savedAuthor = authorService.createAuthor(author);
        
        assertNotNull(savedAuthor.getId());
        assertEquals("Test Author", savedAuthor.getName());
        assertEquals("test@example.com", savedAuthor.getEmail());
    }

    @Test
    void testBookCreation() {
        // First create an author
        Author author = new Author("Book Author", "bookauthor@example.com", "An author for books");
        Author savedAuthor = authorService.createAuthor(author);
        
        // Then create a book
        Book book = new Book("Test Book", "978-1234567890", "A test book description", savedAuthor);
        book.setGenre(Book.Genre.FICTION);
        Book savedBook = bookService.createBook(book);
        
        assertNotNull(savedBook.getId());
        assertEquals("Test Book", savedBook.getTitle());
        assertEquals("978-1234567890", savedBook.getIsbn());
        assertEquals(savedAuthor.getId(), savedBook.getAuthor().getId());
    }

    @Test
    void testAuthorBookRelationship() {
        // Create an author
        Author author = new Author("Relationship Author", "relation@example.com", "Testing relationships");
        Author savedAuthor = authorService.createAuthor(author);
        
        // Create books for the author
        Book book1 = new Book("Book One", "111-1111111111", "First book", savedAuthor);
        Book book2 = new Book("Book Two", "222-2222222222", "Second book", savedAuthor);
        
        bookService.createBook(book1);
        bookService.createBook(book2);
        
        // Verify the relationship
        Author authorWithBooks = authorService.getAuthorWithBooks(savedAuthor.getId());
        assertEquals(2, authorWithBooks.getBooks().size());
    }
}
