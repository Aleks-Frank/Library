package com.example.Library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Сущность, представляющая запись о бронировании книги пользователем.
 * <p>
 * Содержит информацию о бронировании: книгу, пользователя,
 * даты начала и окончания бронирования.
 * </p>
 *
 * @Entity указывает, что этот класс является JPA сущностью
 * @Table(name = "tbl_reserve_record_books") Задает имя таблицы в БД
 */
@Entity
@Table(name = "tbl_reserve_record_books")
public class ReserveRecord {

    /** Уникальный идентификатор записи о бронировании.
     * @Id Поле является первичным ключом
     * @GeneratedValue(strategy = GenerationType.IDENTITY) Автоинкрементное значение */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Книга, которая была забронирована.
     * @ManyToOne Определяет отношение "многие к одному" с сущностью Book
     * @JoinColumn(name = "book_id") Указывает столбец для связи с таблицей книг */
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    /** Пользователь, который забронировал книгу.
     * @ManyToOne Определяет отношение "многие к одному" с сущностью UserEntity
     * @JoinColumn(name = "user_id") Указывает столбец для связи с таблицей пользователей */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /** Дата начала бронирования */
    private LocalDate startDateBooking;

    /** Дата окончания бронирования*/
    private LocalDate endDateBooking;

    /** Конструктор по умолчанию для JPA*/
    public ReserveRecord() {
    }

    /** Создает новую запись о бронировании книги.
     *
     * @param book книга, которую бронируют
     * @param user пользователь, который бронирует
     * @param startDateBooking дата начала бронирования
     * @param endDateBooking дата окончания бронирования */
    public ReserveRecord(Book book, UserEntity user, LocalDate startDateBooking, LocalDate endDateBooking) {
        this.book = book;
        this.user = user;
        this.startDateBooking = startDateBooking;
        this.endDateBooking = endDateBooking;
    }

    /** Возвращает уникальный идентификатор записи.
     * @return идентификатор записи*/
    public Long getId() {
        return id;
    }

    /** Возвращает забронированную книгу.
     * @return сущность книги */
    public Book getBook() {
        return book;
    }

    /** Устанавливает книгу для бронирования.
     * @param book сущность книги */
    public void setBook(Book book) {
        this.book = book;
    }

    /** Возвращает пользователя, который забронировал книгу.
     * @return сущность пользователя */
    public UserEntity getUser() {
        return user;
    }

    /** Устанавливает пользователя для бронирования.
     * @param user сущность пользователя */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /** Возвращает дату начала бронирования.
     * @return дата начала бронирования */
    public LocalDate getStartDateBooking() {
        return startDateBooking;
    }

    /** Устанавливает дату начала бронирования.
     * @param startDateBooking новая дата начала */
    public void setStartDateBooking(LocalDate startDateBooking) {
        this.startDateBooking = startDateBooking;
    }

    /** Возвращает дату окончания бронирования.
     * @return дата окончания бронирования */
    public LocalDate getEndDateBooking() {
        return endDateBooking;
    }

    /** Устанавливает дату окончания бронирования.
     * @param endDateBooking новая дата окончания */
    public void setEndDateBooking(LocalDate endDateBooking) {
        this.endDateBooking = endDateBooking;
    }
}
