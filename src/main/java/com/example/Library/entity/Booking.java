package com.example.Library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="tbl_booking_book")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDateBooking;

    private LocalDate endDateBooking;

    private Long bookID;

    @ManyToOne
    private UserEntity user;

    private boolean isActive;

    public Booking() {
    }

    public Booking(LocalDate startDateBooking, LocalDate endDateBooking, Long book, UserEntity user) {
        this.startDateBooking = startDateBooking;
        this.endDateBooking = endDateBooking;
        this.bookID = book;
        this.user = user;
    }

    public Long getId() {
        return id;
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

    public Long getBookID() {
        return bookID;
    }

    public void setBookID(Long book) {
        this.bookID = book;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
