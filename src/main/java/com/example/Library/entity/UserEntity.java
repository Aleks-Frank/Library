package com.example.Library.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Класс-сущность, представляющий пользователя библиотечной системы.
 * <p>
 * Содержит информацию о пользователе, включая учетные данные, список книг и роли.
 * Используется для аутентификации, авторизации и управления пользователями.
 * </p>
 *
 * @Entity Указывает, что класс является JPA сущностью
 * @Table(name = "tbl_user") Задает имя таблицы в базе данных */
@Entity
@Table(name = "tbl_user")
public class UserEntity {

    /** Уникальный идентификатор пользователя.
     * @Id Поле является первичным ключом
     * @GeneratedValue(strategy = GenerationType.IDENTITY) Автоинкрементное значение */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Имя пользователя */
    private String firstName;

    /** Список книг, забронированных пользователем.
     * @ElementCollection Определяет коллекцию простых или встраиваемых объектов */
    @ElementCollection
    private List<Book> myBookList;

    /** Уникальное имя пользователя, для входа в аккаунт */
    private String username;

    /** Пароль для входа в систему*/
    private String password;

    /** Роли пользователя в системе.
     * @ElementCollection(targetClass = ROLE.class, fetch = FetchType.EAGER)
     * Хранит перечень ролей пользователя, загружается сразу при загрузке пользователя */
    @ElementCollection(targetClass = ROLE.class, fetch = FetchType.EAGER)
    private Set<ROLE> role = new HashSet<>();

    /** Возвращает роль пользователя.
     * @return набор ролей пользователя */
    public Set<ROLE> getRoles() {
        return role;
    }

    /** Устанавливает роли пользователя.
     * @param role новый набор ролей */
    public void setRoles(Set<ROLE> role) {
        this.role = role;
    }

    /** Создает нового пользователя с указанными учетными данными и ролями.
     * @param login имя пользователя (логин)
     * @param password пароль пользователя
     * @param role набор ролей пользователя */
    public UserEntity(String login, String password, Set<ROLE> role) {
        this.username = login;
        this.password = password;
        this.role = role;
    }

    /** Возвращает уникальный идентификатор пользователя.
     * @return идентификатор пользователя*/
    public Long getId() {
        return id;
    }

    /** Устанавливает уникальный идентификатор пользователя.
     * @param id новый идентификатор */
    public void setId(Long id) {
        this.id = id;
    }

    /** Возвращает имя пользователя.
     * @return имя пользователя */
    public String getFirstName() {
        return firstName;
    }

    /** Устанавливает имя пользователя.
     * @param firstName новое имя пользователя */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** Возвращает список книг пользователя.
     * @return список забронированных книг */
    public List<Book> getMyBookList() {
        return myBookList;
    }

    /** Устанавливает список книг пользователя.
     * @param myBookList новый список книг */
    public void setMyBookList(List<Book> myBookList) {
        this.myBookList = myBookList;
    }

    /** Возвращает имя пользователя (логин).
     * @return имя пользователя для входа в систему */
    public String getUsername() {
        return username;
    }

    /** Устанавливает имя пользователя (логин).
     * @param username новое имя пользователя */
    public void setUsername(String username) {
        this.username = username;
    }

    /** Возвращает пароль пользователя.
     * @return текущий пароль (обычно в зашифрованном виде) */
    public String getPassword() {
        return password;
    }

    /** Устанавливает пароль пользователя.
     * * @param password новый пароль */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Создает нового пользователя с указанным именем.
     * @param firstName имя пользователя */
    public UserEntity(String firstName){
        this.firstName = firstName;
    }

    /** Конструктор по умолчанию для JPA*/
    public UserEntity(){
    }

}
