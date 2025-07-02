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

/** Контроллер для управления бронированиями книг.
 * Обрабатывает запросы, связанные с просмотром и возвратом книг.
 * @see ReserveRecordService */
@Controller
@RequestMapping("/reservations")
public class ReservationController {

    /** Экземпляр интерфейса ReserveRecordService */
    private final ReserveRecordService reserveRecordService;

    /** Конструктор с внедрением зависимости сервиса бронирований.
     * @param reserveRecordService сервис для работы с бронированиями */
    public ReservationController(ReserveRecordService reserveRecordService) {
        this.reserveRecordService = reserveRecordService;
    }

    /** Отображает список всех текущих бронирований.
     * @param model модель для передачи данных в представление
     * @return имя шаблона "reservations" с атрибутом "reservations", содержащим список бронирований */
    @GetMapping
    public String getAllReservations(Model model) {
        List<ReserveRecord> reservations = reserveRecordService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "reservations";
    }

    /** Обрабатывает возврат книги по ID бронирования.
     * @param id ID бронирования
     * @return редирект на страницу списка бронирований
     * @throws IllegalArgumentException если бронирование с указанным ID не найдено */
    @PostMapping("/return/{id}")
    public String returnBook(@PathVariable Long id) {
        ReserveRecord reservation = reserveRecordService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation Id:" + id));

        reserveRecordService.returnBook(reservation.getBook().getId(), reservation.getUser().getId());
        return "redirect:/reservations";
    }
}
