package com.example.Library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_reserve_record_books")
public class ReserveRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private LocalDate startDateBooking;

    private LocalDate endDateBooking;

    public ReserveRecord() {
    }

    public ReserveRecord(Book book, UserEntity user, LocalDate startDateBooking, LocalDate endDateBooking) {
        this.book = book;
        this.user = user;
        this.startDateBooking = startDateBooking;
        this.endDateBooking = endDateBooking;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDate getStartDateBooking() {
        return startDateBooking;
    }

    public void setStartDateBooking(LocalDate startDateBooking) {
        this.startDateBooking = startDateBooking;
    }

    public LocalDate getEndDateBooking() {
        return endDateBooking;
    }

    public void setEndDateBooking(LocalDate endDateBooking) {
        this.endDateBooking = endDateBooking;
    }
}
