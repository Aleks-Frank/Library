package com.example.Library.service;

import com.example.Library.entity.Book;
import com.example.Library.entity.ReserveRecord;
import com.example.Library.repository.ReserveRecordRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления процессом бронирования книг в библиотечной системе.
 * Обеспечивает полный цикл работы с бронированиями: создание, поиск, возврат и автоматическую обработку.
 * @see ReserveRecordServiceIMPL
 * @see ReserveRecord
 */
@Service
public class ReserveRecordService implements ReserveRecordServiceIMPL{

    /** Экземпляр интерфейса ReserveRecordRepository */
    private final ReserveRecordRepository reserveRecordRepository;

    /** Экземпляр сервиса LibraryService */
    private final LibraryService libraryService;

    /** Логгер для записи событий и ошибок сервиса. */
    private static final Logger log = LoggerFactory.getLogger(ReserveRecordService.class);

    /** Конструктор для внедрения зависимостей.
     * @param reserveRecordRepository репозиторий для работы с записями бронирований
     * @param libraryService сервис для управления книгами*/
    @Autowired
    public ReserveRecordService(ReserveRecordRepository reserveRecordRepository, LibraryService libraryService) {
        this.reserveRecordRepository = reserveRecordRepository;
        this.libraryService = libraryService;
    }

    /** Создает новую запись о бронировании книги.
     * @param reserveRecord данные бронирования (книга и пользователь)
     * @throws IllegalArgumentException если книга не указана (null)
     * @throws IllegalStateException если: книга уже забронирована этим пользователем
     * или книга отсутствует в наличии*/
    @Override
    public void reserveBook(ReserveRecord reserveRecord) {
        log.info("Пользователь с ID: {}, хочет забронировать книгу с ID: {}", reserveRecord.getUser().getId(), reserveRecord.getBook().getId());
        if (reserveRecord.getBook() == null) {
            log.error("Не удалось зарезервировать книгу");
            throw new IllegalArgumentException("Не удалось зарезервировать книгу");
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

    /** Возвращает список всех бронирований указанного пользователя.
     * @param id идентификатор пользователя
     * @return список активных бронирований */
    @Override
    public List<ReserveRecord> findReserveRecordsByIdUser(Long id) {
        log.debug("Поиск книг забронированных пользователем с ID: {}", id);
        List<ReserveRecord> records = reserveRecordRepository.findByUserId(id);
        log.debug("Найдено {} книг(и) забронированных(ые) пользователем с ID: {}", records.size(), id);
        return records;
    }

    /** Обрабатывает возврат книги в библиотеку.
     * Увеличивает счетчик доступных экземпляров и удаляет запись о бронировании.
     * @param bookId идентификатор возвращаемой книги
     * @param userId идентификатор пользователя */
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

    /** Автоматически возвращает все просроченные бронирования.
     * Запускается ежедневно в 00:00 по системному времени.
     * Алгоритм работы:
     * Находит все бронирования с истекшей датой возврата
     * Для каждого бронирования:
     * 1) Увеличивает счетчик доступных книг
     * 2) Удаляет запись о бронировании */
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

    /** Возвращает список всех текущих бронирований в системе.
     * @return полный список записей о бронировании */
    public List<ReserveRecord> getAllReservations() {
        log.info("Получение всех забронированных книг");
        List<ReserveRecord> reservations = (List<ReserveRecord>) reserveRecordRepository.findAll();
        log.debug("Всего {} записей", reservations.size());
        return reservations;
    }

    /** Находит запись о бронировании по её уникальному идентификатору.
     * @param id идентификатор записи бронирования
     * @return Optional с найденной записью */
    public Optional<ReserveRecord> getReservationById(Long id) {
        log.info("Поиск записи по ID: {}", id);
        return reserveRecordRepository.findById(id);
    }

}
