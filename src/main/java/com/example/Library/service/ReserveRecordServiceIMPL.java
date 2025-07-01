package com.example.Library.service;

import com.example.Library.entity.ReserveRecord;

import java.util.List;

/** Интерфейс сервис для работы с бронированием книг.
 * Предоставляет методы для управления записями о бронировании. */
public interface ReserveRecordServiceIMPL {

    /** Создает запись о бронировании книги.
     * @param reserveRecord данные о бронировании*/
    void reserveBook(ReserveRecord reserveRecord);

    /** Находит все записи о забронированных книгах для указанного пользователя.
     * @param id идентификатор пользователя
     * @return список записей о бронировании*/
    List<ReserveRecord> findReserveRecordsByIdUser(Long id);

}
