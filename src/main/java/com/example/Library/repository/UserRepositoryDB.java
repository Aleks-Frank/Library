package com.example.Library.repository;

import com.example.Library.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepositoryDB extends CrudRepository<UserEntity, Long> {
    UserEntity findByUsername(String username);
}
