package com.example.Library.service;

import com.example.Library.entity.ROLE;
import com.example.Library.entity.UserEntity;
import com.example.Library.repository.UserRepositoryDB;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepositoryDB userRepositoryDB;

    @Autowired
    public UserService(UserRepositoryDB userRepositoryDB) {
        this.userRepositoryDB = userRepositoryDB;
    }

    public void addUser(String username, String password) throws Exception {
        log.info("Добавление нового пользователя с именем {}", username);
        UserEntity entity = userRepositoryDB.findByUsername(username);
        if (entity != null){
            log.error("Пользователь с именем {} уже существует", entity.getUsername());
            throw new Exception("User already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(preparePassword(password));
        user.setRoles(Set.of(ROLE.USER));

        saveUser(user);
    }

    public void saveUser(UserEntity user){
        userRepositoryDB.save(user);
        log.debug("Пользователь с именем {} создан и добавлен в БД", user.getUsername());
    }

    private String preparePassword(String password) {
        log.info("Выдан пароль");
        return password;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepositoryDB.findByUsername(username);
        if (entity == null){
            log.error("Пользователь с именем {} не найден", username);
            throw new UsernameNotFoundException("User not found");
        }
        log.debug("Пользователь найден, ID: {}", entity.getId());
        return new User(entity.getUsername(), entity.getPassword(), extractRoles(entity));
    }

    private Collection<? extends GrantedAuthority> extractRoles(UserEntity entity) {
        return entity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    public UserEntity findByUsername(String username){
        UserEntity user = userRepositoryDB.findByUsername(username);
        if (user == null){
            log.error("Пользователь с именем {} не найден", username);
            throw new UsernameNotFoundException("User not found");
        }
        log.debug("Пользователь найден, ID: {}", user.getId());
        return userRepositoryDB.findByUsername(username);
    }

}
