package com.example.Library.service;

import com.example.Library.entity.Book;
import com.example.Library.repository.BookRepositoryDB;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryServiceIMPL implements LibraryService {

    private final BookRepositoryDB bookRepositoryDB;

    public LibraryServiceIMPL(BookRepositoryDB bookRepositoryDB) {
        this.bookRepositoryDB = bookRepositoryDB;
    }

    @Override
    public void createNewBook(Book book) {
        bookRepositoryDB.save(book);
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        return bookRepositoryDB.findById(id);
    }

    @Override
    public Optional<Book> findBookByNameBook(String name) {
        return bookRepositoryDB.findByName(name);
    }

    @Override
    public Book updateBook(Book book) {
        return bookRepositoryDB.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepositoryDB.deleteById(id);
    }

    @Override
    public List<Book> showAll() {
        return (List<Book>) bookRepositoryDB.findAll();
    }

    @Override
    public List<Book> findBooksByIds(List<Long> ids) {
        return (List<Book>) bookRepositoryDB.findAllById(ids);
    }

    @Override
    public List<Book> findBooksByPrefix(String prefix) {
        return bookRepositoryDB.findByNameStartingWithIgnoreCase(prefix);
    }

}
