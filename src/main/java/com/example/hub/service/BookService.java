package com.example.hub.service;

import com.example.hub.entity.Book;
import com.example.hub.exception.ResourceNotFoundException;
import com.example.hub.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private AuthorService authorService;
    
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public Book getBookWithAuthor(Long id) {
        return bookRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthorId(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }
    
    @Transactional(readOnly = true)
    public List<Book> getBooksByGenre(Book.Genre genre) {
        return bookRepository.findByGenre(genre);
    }
    
    @Transactional(readOnly = true)
    public Page<Book> getBooksByGenre(Book.Genre genre, Pageable pageable) {
        return bookRepository.findByGenre(genre, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchByKeyword(keyword);
    }
    
    @Transactional(readOnly = true)
    public Page<Book> searchBooksByTitle(String title, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Book> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return bookRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthorName(String authorName) {
        return bookRepository.findByAuthorNameContaining(authorName);
    }
    
    @Transactional(readOnly = true)
    public List<Book> getLatestBooks() {
        return bookRepository.findTop10ByOrderByCreatedAtDesc();
    }
    
    public Book createBook(Book book) {
        validateBookIsbn(book.getIsbn(), null);
        
        // Ensure the author exists
        if (book.getAuthor() != null && book.getAuthor().getId() != null) {
            book.setAuthor(authorService.getAuthorById(book.getAuthor().getId()));
        }
        
        return bookRepository.save(book);
    }
    
    public Book updateBook(Long id, Book bookDetails) {
        Book existingBook = getBookById(id);
        
        validateBookIsbn(bookDetails.getIsbn(), id);
        
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setIsbn(bookDetails.getIsbn());
        existingBook.setDescription(bookDetails.getDescription());
        existingBook.setPublicationDate(bookDetails.getPublicationDate());
        existingBook.setPrice(bookDetails.getPrice());
        existingBook.setPageCount(bookDetails.getPageCount());
        existingBook.setGenre(bookDetails.getGenre());
        
        // Update author if provided
        if (bookDetails.getAuthor() != null && bookDetails.getAuthor().getId() != null) {
            existingBook.setAuthor(authorService.getAuthorById(bookDetails.getAuthor().getId()));
        }
        
        return bookRepository.save(existingBook);
    }
    
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }
    
    @Transactional(readOnly = true)
    public long getBookCountByAuthor(Long authorId) {
        return bookRepository.countBooksByAuthor(authorId);
    }
    
    private void validateBookIsbn(String isbn, Long currentBookId) {
        if (isbn != null && !isbn.trim().isEmpty()) {
            Optional<Book> existingBook = bookRepository.findByIsbn(isbn);
            if (existingBook.isPresent() && 
                (currentBookId == null || !existingBook.get().getId().equals(currentBookId))) {
                throw new IllegalArgumentException("A book with this ISBN already exists");
            }
        }
    }
}
