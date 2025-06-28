package com.example.Library.service;

import com.example.Library.entity.ReserveRecord;

import java.util.List;

public interface ReserveRecordServiceIMPL {

    void reserveBook(ReserveRecord reserveRecord);

    List<ReserveRecord> findReserveRecordsByIdUser(Long id);

}
