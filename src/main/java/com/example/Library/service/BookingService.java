package com.example.Library.service;

import com.example.Library.entity.Book;
import com.example.Library.entity.Booking;
import com.example.Library.entity.UserEntity;
import com.example.Library.repository.BookBookingRepository;
import com.example.Library.repository.BookRepositoryDB;
import com.example.Library.repository.UserRepositoryDB;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    @Autowired
    private BookBookingRepository bookBookingRepository;

    @Transactional
    public void reserveBook(Booking booking) {
        if (booking.getBookID() == null) {
            throw new IllegalStateException("Book ID cannot be null");
        }
        bookBookingRepository.save(booking);
    }

}
