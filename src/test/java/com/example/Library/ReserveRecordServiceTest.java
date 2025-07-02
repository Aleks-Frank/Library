package com.example.Library;

import com.example.Library.entity.Book;
import com.example.Library.entity.ROLE;
import com.example.Library.entity.ReserveRecord;
import com.example.Library.entity.UserEntity;
import com.example.Library.repository.ReserveRecordRepository;
import com.example.Library.service.LibraryService;
import com.example.Library.service.ReserveRecordService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

/** Тестовый класс для ReserveRecordService.
 * Проверяет функциональность работы с бронированиями книг */
@ExtendWith(MockitoExtension.class)
public class ReserveRecordServiceTest {

    /** Мок репозитория для работы с бронированиями */
    @Mock
    private ReserveRecordRepository reserveRecordRepository;

    /** Мок сервиса работы с книгами */
    @Mock
    private LibraryService libraryService;

    /** Тестируемый сервис с внедренными моками */
    @InjectMocks
    private ReserveRecordService reserveRecordService;

    /** Тестовая книга для создания записей бронирования */
    private final Book testBook = new Book("Test Book", "Frank", 2000, 5);

    /** Тестовый пользователь с ролью ADMIN */
    private final UserEntity testUser = new UserEntity("UserName", "123", Set.of(ROLE.ADMIN));

    /** Тестовая запись бронирования. */
    private final ReserveRecord testRecord = new ReserveRecord(testBook, testUser, LocalDate.now(), LocalDate.now().plusDays(7));

    /** Проверяет успешный возврат книги */
    @Test
    @DisplayName("Успешный возврат книги")
    @Transactional
    void returnBookTest() {
        List<ReserveRecord> records = List.of(testRecord);
        when(reserveRecordRepository.findByBookIdAndUserId(anyLong(), anyLong())).thenReturn(records);
        when(libraryService.updateBook(any(Book.class))).thenReturn(testBook);
        reserveRecordService.returnBook(1L, 1L);
        verify(reserveRecordRepository, times(1)).delete(testRecord);
        assertEquals(6, testBook.getCountBook());
    }

    /** Проверяет автоматический возврат просроченных бронирований */
    @Test
    @DisplayName("Автоматический возврат просроченных книг")
    void returnExpiredBooksTest() {
        ReserveRecord expiredRecord = new ReserveRecord(testBook, testUser,
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(3));
        when(reserveRecordRepository.findByEndDateBookingBefore(any(LocalDate.class)))
                .thenReturn(List.of(expiredRecord));
        reserveRecordService.returnExpiredBooks();
        verify(reserveRecordRepository, times(1)).delete(expiredRecord);
        verify(libraryService, times(1)).updateBook(testBook);
    }

    /** Проверяет поиск бронирований по идентификатору пользователя */
    @Test
    @DisplayName("Поиск бронирований пользователя")
    void findReserveRecordsByIdUserTest() {
        when(reserveRecordRepository.findByUserId(anyLong())).thenReturn(List.of(testRecord));
        List<ReserveRecord> result = reserveRecordService.findReserveRecordsByIdUser(1L);
        assertEquals(1, result.size());
        assertEquals(testRecord, result.get(0));
    }

    /** Проверяет получение всех бронирований в системе */
    @Test
    @DisplayName("Получение всех бронирований")
    void getAllReservationsTest() {
        when(reserveRecordRepository.findAll()).thenReturn(List.of(testRecord));
        List<ReserveRecord> result = reserveRecordService.getAllReservations();
        assertEquals(1, result.size());
        assertEquals(testRecord, result.get(0));
    }

}
