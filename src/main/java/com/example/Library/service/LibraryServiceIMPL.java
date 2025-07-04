package com.example.Library.service;

import com.example.Library.entity.Book;
import com.example.Library.repository.BookRepositoryDB;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/** Реализация сервиса для работы с библиотекой книг.
 * Предоставляет методы для управления книгами, включая создание, поиск, обновление и удаление.
 * <p>Логирует все операции для отслеживания работы сервиса.</p>
 * @see LibraryService
 * @see BookRepositoryDB */
@Service
public class LibraryServiceIMPL implements LibraryService {

    /** Логгер для записи событий и ошибок сервиса. */
    private static final Logger log = LoggerFactory.getLogger(LibraryServiceIMPL.class);

    /** Экземпляр интерфейса BookRepositoryDB */
    private final BookRepositoryDB bookRepositoryDB;

    /** Конструктор с внедрением зависимости репозитория книг.
     * @param bookRepositoryDB репозиторий для работы с книгами в БД */
    public LibraryServiceIMPL(BookRepositoryDB bookRepositoryDB) {
        this.bookRepositoryDB = bookRepositoryDB;
    }

    /** {@inheritDoc}
     * <p>Асинхронно сохраняет новую книгу в базу данных.</p>
     * @throws RuntimeException если возникла ошибка при сохранении*/
    @Override
    @Async
    public CompletableFuture<Void> createNewBook(Book book) {
        try {
            bookRepositoryDB.save(book);
            log.info("Создана новая книга с ID: {}", book.getId());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Ошибка при создании книги", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /** {@inheritDoc}
     * <p>Логирует результат поиска.</p> */
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

    /** {@inheritDoc}
     * <p>Логирует результат поиска.</p> */
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

    /** {@inheritDoc}
     * @throws RuntimeException если возникла ошибка при обновлении */
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

    /** {@inheritDoc}
     * @throws RuntimeException если возникла ошибка при удалении */
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

    /** {@inheritDoc}
     * <p>Логирует количество найденных книг.</p> */
    @Override
    public List<Book> showAll() {
        log.debug("Показать все книги");
        List<Book> books = (List<Book>) bookRepositoryDB.findAll();
        log.debug("Выведено {} книг(и)", books.size());
        return books;
    }

    /** {@inheritDoc}
     * <p>Возвращает список книг по переданным идентификаторам.</p> */
    @Override
    public List<Book> findBooksByIds(List<Long> ids) {
        log.debug("Поиск по нескольким ID: {}", ids);
        return (List<Book>) bookRepositoryDB.findAllById(ids);
    }

    /** {@inheritDoc}
     * <p>Поиск без учета регистра.</p> */
    @Override
    public List<Book> findBooksByPrefix(String prefix) {
        log.debug("Поиск по названию: '{}'", prefix);
        return bookRepositoryDB.findByNameStartingWithIgnoreCase(prefix);
    }

}
