package com.example.hub.service;

import com.example.hub.entity.Author;
import com.example.hub.exception.ResourceNotFoundException;
import com.example.hub.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuthorService {
    
    @Autowired
    private AuthorRepository authorRepository;
    
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<Author> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public Author getAuthorWithBooks(Long id) {
        return authorRepository.findByIdWithBooks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public Optional<Author> findByEmail(String email) {
        return authorRepository.findByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public List<Author> searchAuthors(String keyword) {
        return authorRepository.searchByKeyword(keyword);
    }
    
    @Transactional(readOnly = true)
    public Page<Author> searchAuthorsByName(String name, Pageable pageable) {
        return authorRepository.findByNameContainingIgnoreCase(name, pageable);
    }
    
    public Author createAuthor(Author author) {
        validateAuthorEmail(author.getEmail(), null);
        return authorRepository.save(author);
    }
    
    public Author updateAuthor(Long id, Author authorDetails) {
        Author existingAuthor = getAuthorById(id);
        
        validateAuthorEmail(authorDetails.getEmail(), id);
        
        existingAuthor.setName(authorDetails.getName());
        existingAuthor.setEmail(authorDetails.getEmail());
        existingAuthor.setBio(authorDetails.getBio());
        
        return authorRepository.save(existingAuthor);
    }
    
    public void deleteAuthor(Long id) {
        Author author = getAuthorById(id);
        
        // Check if author has books
        if (!author.getBooks().isEmpty()) {
            throw new IllegalStateException("Cannot delete author with existing books. Please remove all books first.");
        }
        
        authorRepository.delete(author);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return authorRepository.existsByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public long getTotalAuthorsCount() {
        return authorRepository.countAllAuthors();
    }
    
    @Transactional(readOnly = true)
    public List<Author> getAuthorsWithMoreThanBooks(int bookCount) {
        return authorRepository.findAuthorsWithMoreThanBooks(bookCount);
    }
    
    private void validateAuthorEmail(String email, Long currentAuthorId) {
        if (email != null && !email.trim().isEmpty()) {
            Optional<Author> existingAuthor = authorRepository.findByEmail(email);
            if (existingAuthor.isPresent() && 
                (currentAuthorId == null || !existingAuthor.get().getId().equals(currentAuthorId))) {
                throw new IllegalArgumentException("An author with this email already exists");
            }
        }
    }
}
