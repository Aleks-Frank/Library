package com.example.Library.repository;

import com.example.Library.entity.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookBookingRepository extends CrudRepository<Booking, Long> {

    List<Booking> findByEndDateBookingBeforeAndIsActiveTrue(LocalDate date);

}
