package com.example.Library.service;

import com.example.Library.entity.Book;
import com.example.Library.entity.ReserveRecord;
import com.example.Library.repository.ReserveRecordRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReserveRecordService implements ReserveRecordServiceIMPL{

    @Autowired
    ReserveRecordRepository reserveRecordRepository;

    @Autowired
    LibraryService libraryService;

    @Override
    public void reserveBook(ReserveRecord reserveRecord) {
        log.info("Пользователь с ID: {}, хочет забронировать книгу с ID: {}", reserveRecord.getUser().getId(), reserveRecord.getBook().getId());
        if (reserveRecord.getBook() == null) {
            log.error("Не удалось зарезервировать книгу");
            throw new IllegalArgumentException("Book cannot be null");
        }

        Long bookId = reserveRecord.getBook().getId();
        Long userId = reserveRecord.getUser().getId();

        if (reserveRecordRepository.existsByBookIdAndUserId(bookId, userId)) {
            log.error("Не удалось зарезервировать книгу, так как она уже забронирована пользователем");
            throw new IllegalStateException("Вы уже забронировали эту книгу");
        }

        if (reserveRecord.getBook().getCountBook() <= 0) {
            log.error("Не удалось зарезервировать книгу, так как ее нет в наличии");
            throw new IllegalStateException("Книга недоступна для бронирования");
        }

        reserveRecordRepository.save(reserveRecord);
    }

    @Override
    public List<ReserveRecord> findReserveRecordsByIdUser(Long id) {
        log.debug("Поиск книг забронированных пользователем с ID: {}", id);
        List<ReserveRecord> records = reserveRecordRepository.findByUserId(id);
        log.debug("Найдено {} книг(и) забронированных(ые) пользователем с ID: {}", records.size(), id);
        return records;
    }

    @Transactional
    public void returnBook(Long bookId, Long userId) {
        log.info("Возвращение книги в библиотеку");
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
        log.info("Автоматическое возвращение книги в библиотеку");
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
        log.info("Получение всех забронированных книг");
        List<ReserveRecord> reservations = (List<ReserveRecord>) reserveRecordRepository.findAll();
        log.debug("Всего {} записей", reservations.size());
        return reservations;
    }

    public Optional<ReserveRecord> getReservationById(Long id) {
        log.info("Поиск записи по ID: {}", id);
        return reserveRecordRepository.findById(id);
    }

}
