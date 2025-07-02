package com.example.Library;

import com.example.Library.entity.ROLE;
import com.example.Library.entity.UserEntity;
import com.example.Library.repository.UserRepositoryDB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.Library.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Тестовый класс для {@link UserService}.
 * Проверяет функциональность работы с пользователями системы.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    /** Мок репозитория для работы с пользователями */
    @Mock
    private UserRepositoryDB userRepositoryDB;

    /** Тестируемый сервис с внедренными зависимостями */
    @InjectMocks
    private UserService userService;

    /** Тестовый пользователь с ролью USER  */
    private final UserEntity testUser = new UserEntity("testuser", "password", Set.of(ROLE.USER));

    /** Проверяет успешную регистрацию нового пользователя */
    @Test
    @DisplayName("Успешное добавление нового пользователя")
    void addUserTest() throws Exception {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(null);
        when(userRepositoryDB.save(any(UserEntity.class))).thenReturn(testUser);
        userService.addUser("newuser", "password");
        verify(userRepositoryDB, times(1)).save(any(UserEntity.class));
    }

    /** Проверяет обработку попытки регистрации существующего пользователя */
    @Test
    @DisplayName("Попытка добавить существующего пользователя")
    void addUserAlreadyExistsTest() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(testUser);
        assertThrows(Exception.class, () ->
                userService.addUser("testuser", "password"));
    }

    /** Проверяет загрузку данных пользователя для аутентификации */
    @Test
    @DisplayName("Успешная загрузка пользователя по имени")
    void loadUserByUsernameTest() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(testUser);
        UserDetails userDetails = userService.loadUserByUsername("testuser");
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    /** Проверяет обработку попытки загрузки несуществующего пользователя */
    @Test
    @DisplayName("Загрузка несуществующего пользователя")
    void loadUserByUsernameNotFoundTest() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("unknown"));
    }

    /** Проверяет поиск пользователя по имени */
    @Test
    @DisplayName("Успешный поиск пользователя по имени")
    void findByUsernameTest() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(testUser);
        UserEntity result = userService.findByUsername("testuser");
        assertEquals("testuser", result.getUsername());
    }

    /** Проверяет обработку поиска несуществующего пользователя */
    @Test
    @DisplayName("Поиск несуществующего пользователя")
    void findByUsernameNotFoundTest() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () ->
                userService.findByUsername("unknown"));
    }

    /** Проверяет сохранение пользователя в репозитории */
    @Test
    @DisplayName("Сохранение пользователя")
    void saveUserTest() {
        when(userRepositoryDB.save(any(UserEntity.class))).thenReturn(testUser);
        userService.saveUser(testUser);
        verify(userRepositoryDB, times(1)).save(testUser);
    }

    /** Проверяет подготовку пароля пользователя */
    @Test
    @DisplayName("Подготовка пароля")
    void preparePasswordTest() {
        String result = userService.preparePasswordTest("rawpassword");
        assertEquals("rawpassword", result);
    }

}
