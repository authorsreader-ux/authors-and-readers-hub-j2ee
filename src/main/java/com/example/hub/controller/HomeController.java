package com.example.hub.controller;

import com.example.hub.service.AuthorService;
import com.example.hub.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private AuthorService authorService;
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("totalAuthors", authorService.getTotalAuthorsCount());
        model.addAttribute("latestBooks", bookService.getLatestBooks());
        model.addAttribute("prolificAuthors", authorService.getAuthorsWithMoreThanBooks(0));
        return "home";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalAuthors", authorService.getTotalAuthorsCount());
        model.addAttribute("latestBooks", bookService.getLatestBooks());
        return "dashboard";
    }
}
