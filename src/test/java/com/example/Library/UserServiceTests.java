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

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepositoryDB userRepositoryDB;

    @InjectMocks
    private UserService userService;

    private final UserEntity testUser = new UserEntity("testuser", "password", Set.of(ROLE.USER));

    @Test
    @DisplayName("Успешное добавление нового пользователя")
    void addUser_Success() throws Exception {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(null);
        when(userRepositoryDB.save(any(UserEntity.class))).thenReturn(testUser);
        userService.addUser("newuser", "password");
        verify(userRepositoryDB, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Попытка добавить существующего пользователя")
    void addUser_AlreadyExists() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(testUser);
        assertThrows(Exception.class, () ->
                userService.addUser("testuser", "password"));
    }

    @Test
    @DisplayName("Успешная загрузка пользователя по имени")
    void loadUserByUsername_Success() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(testUser);
        UserDetails userDetails = userService.loadUserByUsername("testuser");
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Загрузка несуществующего пользователя")
    void loadUserByUsername_NotFound() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("unknown"));
    }

    @Test
    @DisplayName("Успешный поиск пользователя по имени")
    void findByUsername_Success() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(testUser);
        UserEntity result = userService.findByUsername("testuser");
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("Поиск несуществующего пользователя")
    void findByUsername_NotFound() {
        when(userRepositoryDB.findByUsername(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () ->
                userService.findByUsername("unknown"));
    }

    @Test
    @DisplayName("Сохранение пользователя")
    void saveUser_Success() {
        when(userRepositoryDB.save(any(UserEntity.class))).thenReturn(testUser);
        userService.saveUser(testUser);
        verify(userRepositoryDB, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Подготовка пароля")
    void preparePassword_Success() {
        String result = userService.preparePasswordTest("rawpassword");
        assertEquals("rawpassword", result);
    }

}
