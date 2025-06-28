package com.example.Library.restController;

import com.example.Library.entity.Book;
import com.example.Library.entity.ReserveRecord;
import com.example.Library.entity.UserEntity;
import com.example.Library.service.LibraryService;
import com.example.Library.service.ReserveRecordService;
import com.example.Library.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {

    private final LibraryService libraryService;

    private final UserService userService;

    private final ReserveRecordService reserveRecordService;

    public BookController(LibraryService libraryService, UserService userService, ReserveRecordService reserveRecordService) {
        this.libraryService = libraryService;
        this.userService = userService;
        this.reserveRecordService = reserveRecordService;
    }

    @GetMapping("/book")
    public String showLibraryList(Model model){
        model.addAttribute("listBooks", libraryService.showAll());
        return "library";
    }

    @GetMapping("/")
    public String showLibraryListUser(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("listBooks", libraryService.findBooksByPrefix(search));
        } else {
            model.addAttribute("listBooks", libraryService.showAll());
        }
        return "libraryUser";
    }

    @GetMapping("/book/create")
    public String showCreateForm(Model model){
        model.addAttribute("book", new Book());
        return "create";
    }

    @PostMapping("/book/create")
    public String createBook(
            @RequestParam String name,
            @RequestParam String author,
            @RequestParam int year,
            @RequestParam int countBook,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl) {

        Book book = new Book(name, author, year, countBook, description, imageUrl);
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
            ReserveRecord reserveRecord = new ReserveRecord();
            reserveRecord.setBook(bookDB.get());
            model.addAttribute("reserveRecord", reserveRecord);
            model.addAttribute("book", bookDB.get());
            return "reservationForm";
        }
        return "redirect:/";

    }

    @PostMapping("/book/reserve")
    public String processReservation(
            @RequestParam("bookID") Long bookId,  // Получаем bookID из формы
            @RequestParam("startDateBooking") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDateBooking") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Principal principal,
            RedirectAttributes redirectAttributes)
    {
        try {
            Book book = libraryService.findBookById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));

            UserEntity user = userService.findByUsername(principal.getName());

            ReserveRecord reserveRecord = new ReserveRecord();
            reserveRecord.setBook(book);
            reserveRecord.setUser(user);
            reserveRecord.setStartDateBooking(startDate);
            reserveRecord.setEndDateBooking(endDate);

            reserveRecordService.reserveBook(reserveRecord);

            book.setCountBook(book.getCountBook() - 1);
            libraryService.updateBook(book);

            return "redirect:/";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/book/reserve/" + bookId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }
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

    @GetMapping("/my-books")
    public String showUserBooks(Principal principal, Model model) {
        UserEntity user = userService.findByUsername(principal.getName());
        List<ReserveRecord> userBookings = reserveRecordService.findReserveRecordsByIdUser(user.getId());

        model.addAttribute("userBookings", userBookings);
        return "userBooks";
    }

    @GetMapping("/book/return/{id}")
    public String returnBook(@PathVariable Long id, Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName());
        reserveRecordService.returnBook(id, user.getId());
        return "redirect:/my-books";
    }

    @GetMapping("/book/details/{id}")
    public String getBookDetails(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Book> bookOptional = libraryService.findBookById(id);
        if (bookOptional.isEmpty()) {
            return "redirect:/";
        }

        Book book = bookOptional.get();
        model.addAttribute("book", book);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "bookDetails";
    }

}
