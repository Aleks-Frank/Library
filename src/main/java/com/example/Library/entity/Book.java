package com.example.Library.entity;


import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

/**
 * Класс, представляющий сущность книги в системе.
 * <p>
 * Содержит информацию о книге, включая название, автора, год издания,
 * количество доступных экземпляров, описание и ссылку на изображение обложки.
 * </p>
 *
 * @Entity указывает, что этот класс является JPA сущностью
 * @Table(name="tbl_book") задаёт имя таблицы в базе данных
 */
@Entity
@Table(name="tbl_book")
public class Book {

    /**
     * Уникальный идентификатор книги.
     * @Id указывает на первичный ключ
     * @GeneratedValue(strategy = GenerationType.IDENTITY) - автоинкрементное поле
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Название книги */
    private String name;

    /** Автор книги */
    private String author;

    /** Год выпуска книги*/
    private int year;

    /** Количество доступных книг*/
    private int countBook;

    /** Подробное описание книги.
     * @Column(length = 2000) - максимальная длина описания 2000 символов */
    @Column(length = 2000)
    private String description;

    /** Ссылка на картинку */
    private String imageUrl;

    /** Конструктор по умолчанию для JPA*/
    public Book(){
    }

    /** Создает новый экземпляр книги с основными параметрами.
     *
     * @param name название книги
     * @param author автор книги
     * @param year год издания
     * @param countBook количество доступных экземпляров */
    public Book(String name, String author, int year, int countBook){
        this.name = name;
        this.author = author;
        this.year = year;
        this.countBook = countBook;
    }

    /** Создает новый экземпляр книги со всеми параметрами.
     *
     * @param name название книги
     * @param author автор книги
     * @param year год издания
     * @param countBook количество доступных экземпляров
     * @param description подробное описание
     * @param imageUrl URL изображения обложки */
    public Book(String name, String author, int year, int countBook, String description, String imageUrl) {
        this.name = name;
        this.author = author;
        this.year = year;
        this.countBook = countBook;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    /** Возвращает уникальный идентификатор книги.
     * @return идентификатор книги*/
    public Long getId() {
        return id;
    }

    /** Устанавливает уникальный идентификатор книги.
     * @param id новый идентификатор */
    public void setId(Long id) {
        this.id = id;
    }

    /** Возвращает название книги.
     * @return название книги*/
    public String getName() {
        return name;
    }

    /** Устанавливает название книги.
     * @param name новое название */
    public void setName(String name) {
        this.name = name;
    }

    /** Возвращает имя автора.
     * @return имя автора*/
    public String getAuthor() {
        return author;
    }

    /** Устанавливает имя автора.
     * @param author новое название */
    public void setAuthor(String author) {
        this.author = author;
    }

    /** Возвращает год выпуска.
     * @return год выпуска*/
    public int getYear() {
        return year;
    }

    /** Устанавливает год выпуска.
     * @param year новое название */
    public void setYear(int year) {
        this.year = year;
    }

    /** Возвращает количество доступных книг.
     * @return количество доступных книг*/
    public int getCountBook() {
        return countBook;
    }

    /** Устанавливает количество доступных книг.
     * @param countBook новое название */
    public void setCountBook(int countBook) {
        this.countBook = countBook;
    }

    /** Возвращает описание книги.
     * @return описание книги*/
    public String getDescription() {
        return description;
    }

    /** Устанавливает описание книги.
     * @param description новое название */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Возвращает ссылку на картинку книги.
     * @return ссылку на картинку книги*/
    public String getImageUrl() {
        return imageUrl;
    }

    /** Устанавливает ссылку на картинку.
     * @param imageUrl новое название */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
