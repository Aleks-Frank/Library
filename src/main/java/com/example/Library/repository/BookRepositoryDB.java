package com.example.Library.repository;

import com.example.Library.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Репозиторий для работы с книгами в базе данных.
 * Предоставляет методы для выполнения CRUD-операций и поиска книг. */
@Repository
public interface BookRepositoryDB extends CrudRepository<Book, Long> {

    /** Находит книгу по её названию.
     * @param name название книги для поиска
     * @return Optional, содержащий книгу, если она найдена, или пустой Optional */
    Optional<Book> findByName(String name);

    /** Находит все книги, названия которых начинаются с заданного префикса (без учета регистра).
     * @param prefix префикс для поиска книг
     * @return список книг, удовлетворяющих условию */
    List<Book> findByNameStartingWithIgnoreCase(String prefix);

}
