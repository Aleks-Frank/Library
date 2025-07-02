package com.example.Library;

import com.example.Library.entity.Book;
import com.example.Library.repository.BookRepositoryDB;
import com.example.Library.service.LibraryServiceIMPL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/** Тестовый класс для LibraryServiceIMPL.
 * Проверяет функциональность работы с книгами:
 * Создание, поиск, обновление и удаление книг
 * Поиск по префиксу названия
 * Обработка ошибок при операциях с книгами */
@ExtendWith(MockitoExtension.class)
public class LibraryServiceIMPLTests {

    /** Мок репозитория для работы с книгами */
    @Mock
    private BookRepositoryDB bookRepositoryDB;

    /** Тестируемый сервис с внедренными моками */
    @InjectMocks
    private LibraryServiceIMPL libraryService;

    /** Тестовая книга для использования в тестах */
    private final Book testBook = new Book("Test Book", "Frank", 2005, 5);


    /** Проверяет успешное создание книги */
    @Test
    @DisplayName("Успешное создание книги")
    void createNewBookTest() throws Exception {
        when(bookRepositoryDB.save(any(Book.class))).thenReturn(testBook);
        CompletableFuture<Void> future = libraryService.createNewBook(testBook);
        future.get();
        verify(bookRepositoryDB, times(1)).save(testBook);
    }

    /** Проверяет обработку ошибки при создании книги */
    @Test
    @DisplayName("Ошибка при создании книги")
    void createNewBookFailTest() {
        when(bookRepositoryDB.save(any(Book.class))).thenThrow(new RuntimeException("DB Error"));
        CompletableFuture<Void> future = libraryService.createNewBook(testBook);
        assertThrows(ExecutionException.class, future::get);
    }

    /** Проверяет поиск книги по ID */
    @Test
    @DisplayName("Поиск существующей книги по ID")
    void findBookByIdExistsTest() {
        when(bookRepositoryDB.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        Optional<Book> result = libraryService.findBookById(testBook.getId());
        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getName());
    }

    /** Проверяет обработку ошибки при поиске книги по ID */
    @Test
    @DisplayName("Поиск несуществующей книги по ID")
    void findBookByIdNotExistsTest() {
        when(bookRepositoryDB.findById(2L)).thenReturn(Optional.empty());
        Optional<Book> result = libraryService.findBookById(2L);
        assertFalse(result.isPresent());
    }

    /** Проверяет обновление данных о книге в БД */
    @Test
    @DisplayName("Успешное обновление книги")
    void updateBookTest() {
        Book updatedBook = new Book("Updated Book", "Frank", 2004, 3);
        when(bookRepositoryDB.save(updatedBook)).thenReturn(updatedBook);
        Book result = libraryService.updateBook(updatedBook);
        assertEquals("Updated Book", result.getName());
        verify(bookRepositoryDB, times(1)).save(updatedBook);
    }

    /** Проверяет обработку ошибки обновления данных о книге */
    @Test
    @DisplayName("Ошибка при обновлении книги")
    void updateBookFailTest() {
        when(bookRepositoryDB.save(any(Book.class))).thenThrow(new RuntimeException("Update error"));
        assertThrows(RuntimeException.class, () -> libraryService.updateBook(testBook));
    }

    /** Проверяет удаление книги */
    @Test
    @DisplayName("Успешное удаление книги")
    void deleteBookTest() {
        doNothing().when(bookRepositoryDB).deleteById(testBook.getId());
        assertDoesNotThrow(() -> libraryService.deleteBook(testBook.getId()));
        verify(bookRepositoryDB, times(1)).deleteById(testBook.getId());
    }

    /** Проверяет обработку ошибки при удалении книги */
    @Test
    @DisplayName("Ошибка при удалении книги")
    void deleteBookFailTest() {
        doThrow(new RuntimeException("Delete error")).when(bookRepositoryDB).deleteById(testBook.getId());
        assertThrows(RuntimeException.class, () -> libraryService.deleteBook(testBook.getId()));
    }

    /** Проверяет поиск по началу названия книги */
    @Test
    @DisplayName("Поиск книг по префиксу")
    void findBooksByPrefixTest() {
        List<Book> books = List.of(
                new Book("Test Book 1", "Frank", 2003, 2),
                new Book("Test Book 2", "Frank", 2005, 3)
        );
        when(bookRepositoryDB.findByNameStartingWithIgnoreCase("Test")).thenReturn(books);
        List<Book> result = libraryService.findBooksByPrefix("Test");
        assertEquals(2, result.size());
        verify(bookRepositoryDB, times(1)).findByNameStartingWithIgnoreCase("Test");
    }
}
