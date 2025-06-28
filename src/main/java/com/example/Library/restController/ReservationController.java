package com.example.Library.restController;

import com.example.Library.entity.ReserveRecord;
import com.example.Library.service.ReserveRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReserveRecordService reserveRecordService;

    public ReservationController(ReserveRecordService reserveRecordService) {
        this.reserveRecordService = reserveRecordService;
    }

    @GetMapping
    public String getAllReservations(Model model) {
        List<ReserveRecord> reservations = reserveRecordService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "reservations";
    }

    @PostMapping("/return/{id}")
    public String returnBook(@PathVariable Long id) {
        ReserveRecord reservation = reserveRecordService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id:" + id));

        reserveRecordService.returnBook(reservation.getBook().getId(), reservation.getUser().getId());
        return "redirect:/reservations";
    }
}
