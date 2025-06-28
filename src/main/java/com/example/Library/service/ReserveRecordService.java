package com.example.Library.service;

import com.example.Library.entity.Book;
import com.example.Library.entity.ReserveRecord;
import com.example.Library.repository.ReserveRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReserveRecordService implements ReserveRecordServiceIMPL{

    @Autowired
    ReserveRecordRepository reserveRecordRepository;

    @Autowired
    LibraryService libraryService;

    @Override
    public void reserveBook(ReserveRecord reserveRecord) {
        if (reserveRecord.getBook() == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        Long bookId = reserveRecord.getBook().getId();
        Long userId = reserveRecord.getUser().getId();

        // Проверяем, есть ли уже бронь этой книги у пользователя
        if (reserveRecordRepository.existsByBookIdAndUserId(bookId, userId)) {
            throw new IllegalStateException("Вы уже забронировали эту книгу");
        }

        // Проверяем доступность книги
        if (reserveRecord.getBook().getCountBook() <= 0) {
            throw new IllegalStateException("Книга недоступна для бронирования");
        }

        reserveRecordRepository.save(reserveRecord);
    }

    @Override
    public List<ReserveRecord> findReserveRecordsByIdUser(Long id) {
        return reserveRecordRepository.findByUserId(id);
    }

    @Transactional
    public void returnBook(Long bookId, Long userId) {
        List<ReserveRecord> records = reserveRecordRepository.findByBookIdAndUserId(bookId, userId);
        if (!records.isEmpty()) {
            ReserveRecord record = records.get(0);
            Book book = record.getBook();
            book.setCountBook(book.getCountBook() + 1);
            libraryService.updateBook(book);
            reserveRecordRepository.delete(record);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void returnExpiredBooks() {
        LocalDate today = LocalDate.now();
        List<ReserveRecord> expiredRecords = reserveRecordRepository.findByEndDateBookingBefore(today);

        for (ReserveRecord record : expiredRecords) {
            Book book = record.getBook();
            book.setCountBook(book.getCountBook() + 1);
            libraryService.updateBook(book);
            reserveRecordRepository.delete(record);
        }
    }

    public List<ReserveRecord> getAllReservations() {
        return (List<ReserveRecord>) reserveRecordRepository.findAll();
    }

    public Optional<ReserveRecord> getReservationById(Long id) {
        return reserveRecordRepository.findById(id);
    }

}
