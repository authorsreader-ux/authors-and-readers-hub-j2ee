package com.example.hub.config;

import com.example.hub.entity.Author;
import com.example.hub.entity.Book;
import com.example.hub.repository.AuthorRepository;
import com.example.hub.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("dev") // Only runs in dev profile
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (authorRepository.count() == 0) {
            loadSampleData();
        }
    }
    
    private void loadSampleData() {
        // Create sample authors
        Author author1 = new Author("J.K. Rowling", "jk.rowling@email.com", 
                "British author, best known for the Harry Potter series.");
        
        Author author2 = new Author("Stephen King", "stephen.king@email.com", 
                "American author of horror, supernatural fiction, and fantasy novels.");
        
        Author author3 = new Author("Agatha Christie", "agatha.christie@email.com", 
                "English writer known for her detective novels featuring Hercule Poirot and Miss Marple.");
        
        authorRepository.save(author1);
        authorRepository.save(author2);
        authorRepository.save(author3);
        
        // Create sample books
        Book book1 = new Book("Harry Potter and the Philosopher's Stone", "978-0747532699", 
                "The first novel in the Harry Potter series.", author1);
        book1.setGenre(Book.Genre.FANTASY);
        book1.setPrice(new BigDecimal("12.99"));
        book1.setPageCount(223);
        book1.setPublicationDate(LocalDate.of(1997, 6, 26));
        
        Book book2 = new Book("The Shining", "978-0307743657", 
                "A horror novel about a family that becomes winter caretakers of an isolated hotel.", author2);
        book2.setGenre(Book.Genre.FICTION);
        book2.setPrice(new BigDecimal("14.99"));
        book2.setPageCount(447);
        book2.setPublicationDate(LocalDate.of(1977, 1, 28));
        
        Book book3 = new Book("Murder on the Orient Express", "978-0007119318", 
                "A detective novel featuring Hercule Poirot.", author3);
        book3.setGenre(Book.Genre.MYSTERY);
        book3.setPrice(new BigDecimal("10.99"));
        book3.setPageCount(256);
        book3.setPublicationDate(LocalDate.of(1934, 1, 1));
        
        Book book4 = new Book("Harry Potter and the Chamber of Secrets", "978-0747538493", 
                "The second novel in the Harry Potter series.", author1);
        book4.setGenre(Book.Genre.FANTASY);
        book4.setPrice(new BigDecimal("13.99"));
        book4.setPageCount(251);
        book4.setPublicationDate(LocalDate.of(1998, 7, 2));
        
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        
        System.out.println("Sample data loaded successfully!");
    }
}
