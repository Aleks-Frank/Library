package com.example.Library.repository;

import com.example.Library.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

/** Репозиторий для работы с пользователями в базе данных.
 * Предоставляет методы для выполнения CRUD-операций и поиска пользователей. */
public interface UserRepositoryDB extends CrudRepository<UserEntity, Long> {

    /** Находит пользователя по его имени пользователя.
     * @param username имя пользователя для поиска
     * @return найденный пользователь или null, если пользователь не найден */
    UserEntity findByUsername(String username);
}
