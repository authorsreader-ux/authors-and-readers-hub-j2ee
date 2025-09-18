package com.example.hub.controller;

import com.example.hub.entity.Author;
import com.example.hub.service.AuthorService;
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
@RequestMapping("/authors")
public class AuthorController {
    
    @Autowired
    private AuthorService authorService;
    
    @GetMapping
    public String listAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            Model model) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Author> authorPage;
        
        if (search != null && !search.trim().isEmpty()) {
            authorPage = authorService.searchAuthorsByName(search, pageable);
            model.addAttribute("search", search);
        } else {
            authorPage = authorService.getAllAuthors(pageable);
        }
        
        model.addAttribute("authors", authorPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", authorPage.getTotalPages());
        model.addAttribute("totalElements", authorPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        return "authors/list";
    }
    
    @GetMapping("/{id}")
    public String viewAuthor(@PathVariable Long id, Model model) {
        Author author = authorService.getAuthorWithBooks(id);
        model.addAttribute("author", author);
        return "authors/view";
    }
    
    @GetMapping("/new")
    public String createAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "authors/form";
    }
    
    @PostMapping
    public String createAuthor(@Valid @ModelAttribute Author author, 
                             BindingResult result, 
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "authors/form";
        }
        
        try {
            Author savedAuthor = authorService.createAuthor(author);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Author '" + savedAuthor.getName() + "' has been created successfully!");
            return "redirect:/authors/" + savedAuthor.getId();
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.author", e.getMessage());
            return "authors/form";
        }
    }
    
    @GetMapping("/{id}/edit")
    public String editAuthorForm(@PathVariable Long id, Model model) {
        Author author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        return "authors/form";
    }
    
    @PostMapping("/{id}")
    public String updateAuthor(@PathVariable Long id, 
                             @Valid @ModelAttribute Author author,
                             BindingResult result, 
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            author.setId(id);
            return "authors/form";
        }
        
        try {
            Author updatedAuthor = authorService.updateAuthor(id, author);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Author '" + updatedAuthor.getName() + "' has been updated successfully!");
            return "redirect:/authors/" + id;
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.author", e.getMessage());
            author.setId(id);
            return "authors/form";
        }
    }
    
    @PostMapping("/{id}/delete")
    public String deleteAuthor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Author author = authorService.getAuthorById(id);
            String authorName = author.getName();
            authorService.deleteAuthor(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Author '" + authorName + "' has been deleted successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/authors";
    }
    
    @GetMapping("/search")
    @ResponseBody
    public List<Author> searchAuthors(@RequestParam String keyword) {
        return authorService.searchAuthors(keyword);
    }
}
