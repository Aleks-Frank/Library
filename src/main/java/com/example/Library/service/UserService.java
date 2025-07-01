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

/**
 * Сервис для работы с пользователями системы.
 * @see UserDetailsService
 * @see UserRepositoryDB
 * @see UserEntity
 */
@Service
public class UserService implements UserDetailsService {

    /**
     * Логгер для записи событий аутентификации и управления пользователями.
     */
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /** Экземпляр интерфейса UserRepositoryDB */
    private final UserRepositoryDB userRepositoryDB;

    /** Конструктор с внедрением зависимости репозитория пользователей.
     * @param userRepositoryDB репозиторий для работы с пользователями в БД */
    @Autowired
    public UserService(UserRepositoryDB userRepositoryDB) {
        this.userRepositoryDB = userRepositoryDB;
    }

    /** Регистрирует нового пользователя в системе.
     * @param username имя пользователя
     * @param password пароль (будет сохранен как есть, без хеширования)
     * @throws Exception если пользователь с таким именем уже существует */
    public void addUser(String username, String password) throws Exception {
        log.info("Добавление нового пользователя с именем {}", username);
        UserEntity entity = userRepositoryDB.findByUsername(username);
        if (entity != null){
            log.error("Пользователь с именем {} уже существует", entity.getUsername());
            throw new Exception("Пользователь не найден");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(preparePassword(password));
        user.setRoles(Set.of(ROLE.USER));

        saveUser(user);
    }

    /** Сохраняет пользователя в базу данных.
     * @param user сущность пользователя для сохранения*/
    public void saveUser(UserEntity user){
        userRepositoryDB.save(user);
        log.debug("Пользователь с именем {} создан и добавлен в БД", user.getUsername());
    }

    /** Вспомогательный метод для подготовки пароля.
     * @param password исходный пароль
     * @return тот же пароль без изменений */
    private String preparePassword(String password) {
        log.info("Выдан пароль");
        return password;
    }

    /** Тестовый метод для проверки подготовки пароля.
     * @param password исходный пароль
     * @return результат preparePassword */
    public String preparePasswordTest(String password) {
        return preparePassword(password);
    }


    /** {@inheritDoc}
     * @throws UsernameNotFoundException если пользователь не найден*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepositoryDB.findByUsername(username);
        if (entity == null){
            log.error("Пользователь с именем {} не найден", username);
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        log.debug("Пользователь найден, ID: {}", entity.getId());
        return new User(entity.getUsername(), entity.getPassword(), extractRoles(entity));
    }

    /** Преобразует роли пользователя в коллекцию GrantedAuthority для Spring Security.
     * @param entity сущность пользователя
     * @return коллекция прав доступа */
    private Collection<? extends GrantedAuthority> extractRoles(UserEntity entity) {
        return entity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    /** Тестовый метод для проверки извлечения ролей.
     * @param entity сущность пользователя
     * @return результат метода extractRoles */
    public Collection<? extends GrantedAuthority> extractRolesTest(UserEntity entity){
        return extractRoles(entity);
    }

    /** Находит пользователя по имени.
     * @param username имя пользователя для поиска
     * @return сущность пользователя
     * @throws UsernameNotFoundException если пользователь не найден */
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
