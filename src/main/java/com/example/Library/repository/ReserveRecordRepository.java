package com.example.Library.repository;

import com.example.Library.entity.ReserveRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/** Репозиторий для работы с бронированием книг в базе данных.
 * Предоставляет методы для выполнения CRUD-операций записей. */
@Repository
public interface ReserveRecordRepository extends CrudRepository<ReserveRecord, Long> {

    /** Находит все записи о забронированных книгах для указанного пользователя.
     * @param userId идентификатор пользователя
     * @return список записей резерваций */
    List<ReserveRecord> findByUserId(Long userId);

    /** Находит записи для указанной забронированной книги и пользователе.
     * @param bookId идентификатор книги
     * @param userId идентификатор пользователя
     * @return список записей резерваций*/
    List<ReserveRecord> findByBookIdAndUserId(Long bookId, Long userId);

    /** Находит записи забронированных книг, у которых дата окончания бронирования раньше указанной даты.
     * @param date дата для сравнения
     * @return список записей резерваций */
    List<ReserveRecord> findByEndDateBookingBefore(LocalDate date);

    /** Проверяет существование записи забронированных книг для указанной книги и пользователя.
     * @param bookId идентификатор книги
     * @param userId идентификатор пользователя
     * @return true если запись существует, false в противном случае */
    boolean existsByBookIdAndUserId(Long bookId, Long userId);

}
