package com.example.hub.controller.api;

import com.example.hub.entity.Book;
import com.example.hub.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookRestController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(Pageable pageable) {
        Page<Book> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    
    @GetMapping("/{id}/with-author")
    public ResponseEntity<Book> getBookWithAuthor(@PathVariable Long id) {
        Book book = bookService.getBookWithAuthor(id);
        return ResponseEntity.ok(book);
    }
    
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long authorId) {
        List<Book> books = bookService.getBooksByAuthorId(authorId);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<Book>> getBooksByGenre(
            @PathVariable Book.Genre genre, Pageable pageable) {
        Page<Book> books = bookService.getBooksByGenre(genre, pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String keyword) {
        List<Book> books = bookService.searchBooks(keyword);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/price-range")
    public ResponseEntity<List<Book>> getBooksByPriceRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        List<Book> books = bookService.getBooksByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/latest")
    public ResponseEntity<List<Book>> getLatestBooks() {
        List<Book> books = bookService.getLatestBooks();
        return ResponseEntity.ok(books);
    }
    
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, 
                                         @Valid @RequestBody Book book) {
        Book updatedBook = bookService.updateBook(id, book);
        return ResponseEntity.ok(updatedBook);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
