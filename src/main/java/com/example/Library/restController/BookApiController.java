package com.example.Library.restController;

import com.example.Library.entity.Book;
import com.example.Library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping
    public List<Book> getAllBooks() {
        return libraryService.showAll();
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String prefix) {
        return libraryService.findBooksByPrefix(prefix);
    }

}
