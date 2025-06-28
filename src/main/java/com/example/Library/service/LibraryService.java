package com.example.Library.service;

import com.example.Library.entity.Book;

import java.util.List;
import java.util.Optional;

public interface LibraryService {

    void createNewBook(Book book);

    Optional<Book> findBookById(Long id);

    Optional<Book> findBookByNameBook(String name);

    Book updateBook(Book book);

    void deleteBook(Long id);

    List<Book> showAll();

    List<Book> findBooksByIds(List<Long> bookIds);

    List<Book> findBooksByPrefix(String prefix);
}
