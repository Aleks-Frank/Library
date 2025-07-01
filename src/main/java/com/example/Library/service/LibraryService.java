package com.example.Library.service;

import com.example.Library.entity.Book;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/** Интерфейс сервис для работы с библиотекой книг.
 * Предоставляет методы для управления книгами и их поиска. */
public interface LibraryService {

    /** Асинхронно создает новую книгу в библиотеке.
     * @param book книга для создания
     * @return CompletableFuture, который завершается когда книга создана*/
    CompletableFuture<Void> createNewBook(Book book);

    /** Находит книгу по её идентификатору.
     * @param id идентификатор книги
     * @return Optional, содержащий книгу если найдена, иначе пустой Optional*/
    Optional<Book> findBookById(Long id);

    /** Находит книгу по её названию.
     * @param name название книги для поиска
     * @return Optional, содержащий книгу если найдена, иначе пустой Optional */
    Optional<Book> findBookByNameBook(String name);

    /** Обновляет информацию о книге.
     * @param book книга с обновленными данными
     * @return обновленная книга */
    Book updateBook(Book book);

    /** Удаляет книгу из библиотеки по её идентификатору.
     * @param id идентификатор книги для удаления */
    void deleteBook(Long id);

    /** Возвращает список всех книг в библиотеке.
     * @return список всех книг*/
    List<Book> showAll();

    /** Находит книги по списку идентификаторов.
     * @param bookIds список идентификаторов книг
     * @return список найденных книг*/
    List<Book> findBooksByIds(List<Long> bookIds);

    /** Находит книги, названия которых начинаются с заданного префикса.
     * @param prefix префикс для поиска
     * @return список книг, удовлетворяющих условию*/
    List<Book> findBooksByPrefix(String prefix);
}
