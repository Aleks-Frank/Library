package com.example.Library.service;

import com.example.Library.entity.Book;
import com.example.Library.repository.BookRepositoryDB;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibraryServiceIMPL implements LibraryService {

    private static final Logger log = LoggerFactory.getLogger(LibraryServiceIMPL.class);

    private final BookRepositoryDB bookRepositoryDB;

    public LibraryServiceIMPL(BookRepositoryDB bookRepositoryDB) {
        this.bookRepositoryDB = bookRepositoryDB;
    }

    @Override
    public void createNewBook(Book book) {
        bookRepositoryDB.save(book);
        log.info("Создана новая книга с ID: {}", book.getId());
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        log.debug("Поиск книги по ID: {}", id);
        Optional<Book> book = bookRepositoryDB.findById(id);
        book.ifPresentOrElse(
                b -> log.debug("Найдена книга: {}", b.getName()),
                () -> log.warn("Книга с ID {} не найдена", id)
        );
        return book;

    }

    @Override
    public Optional<Book> findBookByNameBook(String name) {
        log.debug("Поиск книги по названию: {}", name);
        Optional<Book> book = bookRepositoryDB.findByName(name);
        book.ifPresentOrElse(
                b -> log.debug("Найдена книга c ID: {}", b.getId()),
                () -> log.warn("Книга с названием {} не найдена", name)
        );
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        log.debug("Обновление книги с ID: {}", book.getId());
        try {
            Book updatedBook = bookRepositoryDB.save(book);
            log.info("Книга с ID: {}, успешно обновлена", book.getId());
            return updatedBook;
        } catch (Exception e) {
            log.error("Ошибка обновления книги с ID: {}", book.getId(), e);
            throw e;
        }
    }

    @Override
    public void deleteBook(Long id) {
        log.info("Удаление книги с ID: {}", id);
        try {
            bookRepositoryDB.deleteById(id);
            log.info("Книга с ID: {}, успешно удалена", id);
        } catch (Exception e) {
            log.error("Ошибка удаления книги с ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<Book> showAll() {
        log.debug("Показать все книги");
        List<Book> books = (List<Book>) bookRepositoryDB.findAll();
        log.debug("Выведено {} книг(и)", books.size());
        return books;
    }

    @Override
    public List<Book> findBooksByIds(List<Long> ids) {
        log.debug("Поиск по нескольким ID: {}", ids);
        return (List<Book>) bookRepositoryDB.findAllById(ids);
    }

    @Override
    public List<Book> findBooksByPrefix(String prefix) {
        log.debug("Поиск по названию: '{}'", prefix);
        return bookRepositoryDB.findByNameStartingWithIgnoreCase(prefix);
    }

}
