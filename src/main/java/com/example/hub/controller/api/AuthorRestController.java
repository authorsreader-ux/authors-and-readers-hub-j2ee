package com.example.hub.controller.api;

import com.example.hub.entity.Author;
import com.example.hub.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "*")
public class AuthorRestController {
    
    @Autowired
    private AuthorService authorService;
    
    @GetMapping
    public ResponseEntity<Page<Author>> getAllAuthors(Pageable pageable) {
        Page<Author> authors = authorService.getAllAuthors(pageable);
        return ResponseEntity.ok(authors);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        Author author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }
    
    @GetMapping("/{id}/with-books")
    public ResponseEntity<Author> getAuthorWithBooks(@PathVariable Long id) {
        Author author = authorService.getAuthorWithBooks(id);
        return ResponseEntity.ok(author);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Author>> searchAuthors(@RequestParam String keyword) {
        List<Author> authors = authorService.searchAuthors(keyword);
        return ResponseEntity.ok(authors);
    }
    
    @PostMapping
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody Author author) {
        Author createdAuthor = authorService.createAuthor(author);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, 
                                             @Valid @RequestBody Author author) {
        Author updatedAuthor = authorService.updateAuthor(id, author);
        return ResponseEntity.ok(updatedAuthor);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalAuthorsCount() {
        long count = authorService.getTotalAuthorsCount();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/prolific")
    public ResponseEntity<List<Author>> getProlificAuthors(
            @RequestParam(defaultValue = "1") int minBooks) {
        List<Author> authors = authorService.getAuthorsWithMoreThanBooks(minBooks);
        return ResponseEntity.ok(authors);
    }
}
