package com.example.Library.restController;

import com.example.Library.entity.Book;
import com.example.Library.entity.Booking;
import com.example.Library.entity.UserEntity;
import com.example.Library.service.BookingService;
import com.example.Library.service.LibraryService;
import com.example.Library.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
public class BookController {

    private final LibraryService libraryService;

    private final UserService userService;

    private final BookingService bookingService;

    public BookController(LibraryService libraryService, UserService userService, BookingService bookingService) {
        this.libraryService = libraryService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping("/book")
    public String showLibraryList(Model model){
        model.addAttribute("listBooks", libraryService.showAll());
        return "library";
    }

    @GetMapping("/")
    public String showLibraryListUser(Model model){
        model.addAttribute("listBooks", libraryService.showAll());
        return "libraryUser";
    }

    @GetMapping("/book/create")
    public String showCreateForm(Model model){
        model.addAttribute("book", new Book());
        return "create";
    }

    @PostMapping("/book/create")
    public String createBook(@ModelAttribute Book book){
        libraryService.createNewBook(book);
        return "redirect:/book";
    }

    @GetMapping("/book/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        Optional<Book> book = libraryService.findBookById(id);
        if(book.isPresent()){
            model.addAttribute("book", book.get());
            return "edit";
        }
        return "redirect:/book";
    }

    @PostMapping("/book/edit")
    public String editBook(@ModelAttribute Book book){
        libraryService.updateBook(book);
        return "redirect:/book";
    }

    @GetMapping("/book/reserve/{id}")
    public String showFormBooking(@PathVariable Long id, Model model){
        Optional<Book> bookDB = libraryService.findBookById(id);
        if(bookDB.isPresent() && bookDB.get().getCountBook() > 0){
            Booking booking = new Booking();
            booking.setBookID(id);
            model.addAttribute("booking", booking);
            model.addAttribute("book", bookDB.get());
            return "reservationForm";
        }
        return "redirect:/";

    }

    @PostMapping("/book/reserve")
    public String processReservation(@ModelAttribute Booking booking, @RequestParam("startDateBooking") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                     @RequestParam("endDateBooking") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, Principal principal) {
        Book book = libraryService.findBookById(booking.getBookID())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        UserEntity user = userService.findByUsername(principal.getName());
        booking.setBookID(book.getId()); // Устанавливаем ID книги
        booking.setStartDateBooking(startDate);
        booking.setEndDateBooking(endDate);
        booking.setUser(user);
        booking.setActive(true);

        // Сохраняем бронирование
        bookingService.reserveBook(booking);

        // Уменьшаем количество книг
        book.setCountBook(book.getCountBook() - 1);
        libraryService.updateBook(book);

        return "redirect:/";
    }

    @GetMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable Long id){
        libraryService.deleteBook(id);
        return "redirect:/book";
    }

    @GetMapping("/register")
    public String showRegistrationFrom(Model model){
        model.addAttribute("userEntity", new UserEntity());
        return "registration";
    }

    @PostMapping("/register")
    public String adduser(UserEntity user, Model model)
    {
        try
        {
            userService.addUser(user.getUsername(), user.getPassword());
            return "redirect:/login";
        }
        catch (Exception ex)
        {
            model.addAttribute("message", "User exists");
            return "registration";
        }
    }
}
