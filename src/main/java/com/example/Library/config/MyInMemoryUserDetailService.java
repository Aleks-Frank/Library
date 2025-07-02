package com.example.Library.config;


import com.example.Library.entity.ROLE;
import com.example.Library.entity.UserEntity;
import com.example.Library.repository.UserRepositoryDB;
import com.example.Library.service.UserService;
import jakarta.annotation.PostConstruct;
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
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/** Сервис для работы с пользовательскими данными в памяти приложения.
 * Реализует интерфейс UserDetailsService для интеграции со Spring Security. */
@Component
public class MyInMemoryUserDetailService implements UserDetailsService {

    /** Логгер для записи событий и ошибок сервиса. */
    private static final Logger log = LoggerFactory.getLogger(MyInMemoryUserDetailService.class);

    /** Экземпляр интерфейса UserRepositoryDB */
    private final UserRepositoryDB userRepositoryDB;

    /** Экземпляр сервиса UserService */
    @Autowired
    private UserService userService;

    /** Конструктор для внедрения зависимостей.
     * @param userRepositoryDB репозиторий для работы с пользователем */
    public MyInMemoryUserDetailService(UserRepositoryDB userRepositoryDB) {
        this.userRepositoryDB = userRepositoryDB;
    }

    /** Инициализирует тестовых пользователей (admin/user) при старте приложения. */
    @PostConstruct
    public void init() {
        if (userRepositoryDB.findByUsername("user") == null) {
            UserEntity user = new UserEntity("user", "123", Set.of(ROLE.USER));
            userService.saveUser(user);
            log.debug("Создан пользователь с именем {} и паролем {}", user.getUsername(), user.getPassword());
        }
        if (userRepositoryDB.findByUsername("admin") == null) {
            UserEntity admin = new UserEntity("admin", "admin", Set.of(ROLE.ADMIN));
            userService.saveUser(admin);
            log.debug("Создан пользователь с именем {} и паролем {}", admin.getUsername(), admin.getPassword());
        }
    }

    /** Загружает пользователя по имени.
     * @param username имя пользователя для поиска
     * @return UserDetails с данными пользователя
     * @throws UsernameNotFoundException если пользователь не найден */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        try {
            return userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found");
        }
    }

    /** Преобразует роли пользователя в GrantedAuthority.
     * @param appUser сущность пользователя
     * @return коллекция прав доступа */
    private Collection<GrantedAuthority> mapRoles(UserEntity appUser)
    {
        return appUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" +
                        role.name())).collect(Collectors.toList());
    }
}

