package com.example.Library.restController;

import com.example.Library.entity.Book;
import com.example.Library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST контроллер для поиска книг в библиотечной системе. */
@RestController
@RequestMapping("/api/books")
public class BookApiController {

    /** Экземпляр сервиса LibraryService */
    private final LibraryService libraryService;

    /** Конструктор с внедрением зависимости сервиса книг.
     * @param libraryService сервис для работы с книгами */
    @Autowired
    public BookApiController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /** Возвращает полный список всех книг в библиотеке.
     * @return список всех книг (может быть пустым) */
    @GetMapping
    public List<Book> getAllBooks() {
        return libraryService.showAll();
    }

    /** Осуществляет поиск книг, названия которых начинаются с указанного префикса.
     * @param prefix префикс для поиска (без учета регистра)
     * @return список найденных книг (может быть пустым) */
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam("prefix") String prefix) {
        return libraryService.findBooksByPrefix(prefix);
    }

}
