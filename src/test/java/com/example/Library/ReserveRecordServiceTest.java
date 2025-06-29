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

@ExtendWith(MockitoExtension.class)
public class ReserveRecordServiceTest {

    @Mock
    private ReserveRecordRepository reserveRecordRepository;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private ReserveRecordService reserveRecordService;

    private final Book testBook = new Book("Test Book", "Frank", 2000, 5);
    private final UserEntity testUser = new UserEntity("UserName", "123", Set.of(ROLE.ADMIN));
    private final ReserveRecord testRecord = new ReserveRecord(testBook, testUser, LocalDate.now(), LocalDate.now().plusDays(7));

    @Test
    @DisplayName("Успешное бронирование книги")
    void reserveBook_Success() {
        when(reserveRecordRepository.existsByBookIdAndUserId(anyLong(), anyLong())).thenReturn(false);
        when(reserveRecordRepository.save(any(ReserveRecord.class))).thenReturn(testRecord);
        testBook.setCountBook(1);

        reserveRecordService.reserveBook(testRecord);

        verify(reserveRecordRepository, times(1)).save(testRecord);
    }

    @Test
    @DisplayName("Успешный возврат книги")
    @Transactional
    void returnBook_Success() {
        List<ReserveRecord> records = List.of(testRecord);
        when(reserveRecordRepository.findByBookIdAndUserId(anyLong(), anyLong())).thenReturn(records);
        when(libraryService.updateBook(any(Book.class))).thenReturn(testBook);
        reserveRecordService.returnBook(1L, 1L);
        verify(reserveRecordRepository, times(1)).delete(testRecord);
        assertEquals(6, testBook.getCountBook());
    }

    @Test
    @DisplayName("Автоматический возврат просроченных книг")
    void returnExpiredBooks_Success() {
        ReserveRecord expiredRecord = new ReserveRecord(testBook, testUser,
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(3));
        when(reserveRecordRepository.findByEndDateBookingBefore(any(LocalDate.class)))
                .thenReturn(List.of(expiredRecord));
        reserveRecordService.returnExpiredBooks();
        verify(reserveRecordRepository, times(1)).delete(expiredRecord);
        verify(libraryService, times(1)).updateBook(testBook);
    }

    @Test
    @DisplayName("Поиск бронирований пользователя")
    void findReserveRecordsByIdUser_Success() {
        when(reserveRecordRepository.findByUserId(anyLong())).thenReturn(List.of(testRecord));
        List<ReserveRecord> result = reserveRecordService.findReserveRecordsByIdUser(1L);
        assertEquals(1, result.size());
        assertEquals(testRecord, result.get(0));
    }

    @Test
    @DisplayName("Получение всех бронирований")
    void getAllReservations_Success() {
        when(reserveRecordRepository.findAll()).thenReturn(List.of(testRecord));
        List<ReserveRecord> result = reserveRecordService.getAllReservations();
        assertEquals(1, result.size());
        assertEquals(testRecord, result.get(0));
    }

}
