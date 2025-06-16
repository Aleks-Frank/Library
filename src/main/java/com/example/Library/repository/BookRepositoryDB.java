package com.example.Library.repository;

import com.example.Library.entity.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepositoryDB extends CrudRepository<Book, Long> {

    Optional<Book> findByName(String name);

}
