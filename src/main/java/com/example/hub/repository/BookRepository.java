package com.example.hub.repository;

import com.example.hub.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    Optional<Book> findByIsbn(String isbn);
    
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    List<Book> findByAuthorId(Long authorId);
    
    List<Book> findByGenre(Book.Genre genre);
    
    @Query("SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice")
    List<Book> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE b.id = :id")
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.description LIKE %:keyword%")
    List<Book> searchByKeyword(@Param("keyword") String keyword);
    
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    Page<Book> findByGenre(Book.Genre genre, Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.author.name LIKE %:authorName%")
    List<Book> findByAuthorNameContaining(@Param("authorName") String authorName);
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.author.id = :authorId")
    long countBooksByAuthor(@Param("authorId") Long authorId);
    
    List<Book> findTop10ByOrderByCreatedAtDesc();
    
    boolean existsByIsbn(String isbn);
}
