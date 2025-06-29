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

@ExtendWith(MockitoExtension.class)
public class LibraryServiceIMPLTests {

    @Mock
    private BookRepositoryDB bookRepositoryDB;

    @InjectMocks
    private LibraryServiceIMPL libraryService;

    private final Book testBook = new Book("Test Book", "Frank", 2005, 5);

    @Test
    @DisplayName("Успешное создание книги")
    void createNewBook_Success() throws Exception {
        when(bookRepositoryDB.save(any(Book.class))).thenReturn(testBook);
        CompletableFuture<Void> future = libraryService.createNewBook(testBook);
        future.get();
        verify(bookRepositoryDB, times(1)).save(testBook);
    }

    @Test
    @DisplayName("Ошибка при создании книги")
    void createNewBook_Failure() {
        when(bookRepositoryDB.save(any(Book.class))).thenThrow(new RuntimeException("DB Error"));
        CompletableFuture<Void> future = libraryService.createNewBook(testBook);
        assertThrows(ExecutionException.class, future::get);
    }

    @Test
    @DisplayName("Поиск существующей книги по ID")
    void findBookById_Exists() {
        when(bookRepositoryDB.findById(testBook.getId())).thenReturn(Optional.of(testBook));
        Optional<Book> result = libraryService.findBookById(testBook.getId());
        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getName());
    }

    @Test
    @DisplayName("Поиск несуществующей книги по ID")
    void findBookById_NotExists() {
        when(bookRepositoryDB.findById(2L)).thenReturn(Optional.empty());
        Optional<Book> result = libraryService.findBookById(2L);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Успешное обновление книги")
    void updateBook_Success() {
        Book updatedBook = new Book("Updated Book", "Frank", 2004, 3);
        when(bookRepositoryDB.save(updatedBook)).thenReturn(updatedBook);
        Book result = libraryService.updateBook(updatedBook);
        assertEquals("Updated Book", result.getName());
        verify(bookRepositoryDB, times(1)).save(updatedBook);
    }

    @Test
    @DisplayName("Ошибка при обновлении книги")
    void updateBook_Failure() {
        when(bookRepositoryDB.save(any(Book.class))).thenThrow(new RuntimeException("Update error"));
        assertThrows(RuntimeException.class, () -> libraryService.updateBook(testBook));
    }

    @Test
    @DisplayName("Успешное удаление книги")
    void deleteBook_Success() {
        doNothing().when(bookRepositoryDB).deleteById(testBook.getId());
        assertDoesNotThrow(() -> libraryService.deleteBook(testBook.getId()));
        verify(bookRepositoryDB, times(1)).deleteById(testBook.getId());
    }

    @Test
    @DisplayName("Ошибка при удалении книги")
    void deleteBook_Failure() {
        doThrow(new RuntimeException("Delete error")).when(bookRepositoryDB).deleteById(testBook.getId());
        assertThrows(RuntimeException.class, () -> libraryService.deleteBook(testBook.getId()));
    }

    @Test
    @DisplayName("Поиск книг по префиксу")
    void findBooksByPrefix_Success() {
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
