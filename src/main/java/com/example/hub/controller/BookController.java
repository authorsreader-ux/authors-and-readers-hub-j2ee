package com.example.hub.controller;

import com.example.hub.entity.Author;
import com.example.hub.entity.Book;
import com.example.hub.service.AuthorService;
import com.example.hub.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private AuthorService authorService;
    
    @GetMapping
    public String listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> bookPage;
        
        if (genre != null && !genre.trim().isEmpty()) {
            bookPage = bookService.getBooksByGenre(Book.Genre.valueOf(genre), pageable);
            model.addAttribute("selectedGenre", genre);
        } else if (search != null && !search.trim().isEmpty()) {
            bookPage = bookService.searchBooksByTitle(search, pageable);
            model.addAttribute("search", search);
        } else {
            bookPage = bookService.getAllBooks(pageable);
        }
        
        model.addAttribute("books", bookPage);
        model.addAttribute("genres", Book.Genre.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalElements", bookPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "books/list";
    }
    
    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookWithAuthor(id);
        model.addAttribute("book", book);
        return "books/view";
    }
    
    @GetMapping("/new")
    public String createBookForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("genres", Book.Genre.values());
        return "books/form";
    }
    
    @PostMapping
    public String createBook(@Valid @ModelAttribute Book book, 
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", Book.Genre.values());
            return "books/form";
        }
        
        try {
            Book savedBook = bookService.createBook(book);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Book '" + savedBook.getTitle() + "' has been created successfully!");
            return "redirect:/books/" + savedBook.getId();
        } catch (IllegalArgumentException e) {
            result.rejectValue("isbn", "error.book", e.getMessage());
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", Book.Genre.values());
            return "books/form";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String editBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("genres", Book.Genre.values());
        return "books/form";
    }
    
    @PostMapping("/{id}")
    public String updateBook(@PathVariable Long id, 
                           @Valid @ModelAttribute Book book,
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            book.setId(id);
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", Book.Genre.values());
            return "books/form";
        }
        
        try {
            Book updatedBook = bookService.updateBook(id, book);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Book '" + updatedBook.getTitle() + "' has been updated successfully!");
            return "redirect:/books/" + id;
        } catch (IllegalArgumentException e) {
            result.rejectValue("isbn", "error.book", e.getMessage());
            book.setId(id);
            model.addAttribute("authors", authorService.getAllAuthors());
            model.addAttribute("genres", Book.Genre.values());
            return "books/form";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Book book = bookService.getBookById(id);
        String bookTitle = book.getTitle();
        bookService.deleteBook(id);
        redirectAttributes.addFlashAttribute("successMessage", 
                "Book '" + bookTitle + "' has been deleted successfully!");
        return "redirect:/books";
    }
    
    @GetMapping("/author/{authorId}")
    public String booksByAuthor(@PathVariable Long authorId, Model model) {
        Author author = authorService.getAuthorById(authorId);
        List<Book> books = bookService.getBooksByAuthorId(authorId);
        
        model.addAttribute("author", author);
        model.addAttribute("books", books);
        return "books/by-author";
    }
    
    @GetMapping("/search")
    @ResponseBody
    public List<Book> searchBooks(@RequestParam String keyword) {
        return bookService.searchBooks(keyword);
    }
    
    @GetMapping("/latest")
    @ResponseBody
    public List<Book> getLatestBooks() {
        return bookService.getLatestBooks();
    }
}
