package com.example.hub.repository;

import com.example.hub.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    Optional<Author> findByEmail(String email);
    
    List<Author> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT a FROM Author a WHERE a.name LIKE %:keyword% OR a.email LIKE %:keyword%")
    List<Author> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(@Param("id") Long id);
    
    Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Author a")
    long countAllAuthors();
    
    @Query("SELECT a FROM Author a WHERE SIZE(a.books) > :bookCount")
    List<Author> findAuthorsWithMoreThanBooks(@Param("bookCount") int bookCount);
    
    boolean existsByEmail(String email);
}
