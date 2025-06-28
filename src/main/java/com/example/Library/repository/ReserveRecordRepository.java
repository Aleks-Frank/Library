package com.example.Library.repository;

import com.example.Library.entity.ReserveRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReserveRecordRepository extends CrudRepository<ReserveRecord, Long> {

    List<ReserveRecord> findByUserId(Long userId);
    List<ReserveRecord> findByBookIdAndUserId(Long bookId, Long userId);
    List<ReserveRecord> findByEndDateBookingBefore(LocalDate date);
    boolean existsByBookIdAndUserId(Long bookId, Long userId);

}
