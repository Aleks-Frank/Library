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

/** Контроллер для управления книгами и бронированиями в веб-интерфейсе библиотеки.
 * Обрабатывает HTTP-запросы и возвращает представления (Thymeleaf templates).
 * Основные функции:
 * Управление каталогом книг (CRUD операции)
 * Бронирование и возврат книг
 * Регистрация пользователей
 * Просмотр личных бронирований
 * Используемые шаблоны:
 * library.html - отображение каталога (администратор)
 * libraryUser.html - отображение каталога (пользователь)
 * create.html - форма создания книги
 * edit.html - форма редактирования книги
 * reservationForm.html - форма бронирования
 * userBooks.html - список бронирований пользователя
 * bookDetails.html - детальная информация о книге
 * registration.html - форма регистрации */
@Controller
public class BookController {

    /** Экземпляр сервиса LibraryService */
    private final LibraryService libraryService;

    /** Экземпляр сервиса UserService */
    private final UserService userService;

    /** Экземпляр интерфейса ReserveRecordService */
    private final ReserveRecordService reserveRecordService;

    /** Конструктор с внедрением зависимостей.
     * @param libraryService сервис для работы с книгами
     * @param userService сервис для работы с пользователями
     * @param reserveRecordService сервис для работы с бронированиями */
    public BookController(LibraryService libraryService, UserService userService, ReserveRecordService reserveRecordService) {
        this.libraryService = libraryService;
        this.userService = userService;
        this.reserveRecordService = reserveRecordService;
    }

    /** Отображает полный список книг (административный интерфейс).
     * @param model модель для передачи данных в представление
     * @return имя шаблона "library" */
    @GetMapping("/book")
    public String showLibraryList(Model model){
        model.addAttribute("listBooks", libraryService.showAll());
        return "library";
    }

    /** Отображает список книг с возможностью поиска (пользовательский интерфейс).
     * @param search поисковый запрос (префикс названия книги)
     * @param model модель для передачи данных в представление
     * @return имя шаблона "libraryUser" */
    @GetMapping("/")
    public String showLibraryListUser(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("listBooks", libraryService.findBooksByPrefix(search));
        } else {
            model.addAttribute("listBooks", libraryService.showAll());
        }
        return "libraryUser";
    }

    /** Отображает форму создания новой книги.
     * @param model модель для передачи данных в представление
     * @return имя шаблона "create" */
    @GetMapping("/book/create")
    public String showCreateForm(Model model){
        model.addAttribute("book", new Book());
        return "create";
    }

    /** Обрабатывает создание новой книги.
     * @param name название книги
     * @param author автор книги
     * @param year год издания
     * @param countBook количество экземпляров
     * @param description описание книги (опционально)
     * @param imageUrl URL изображения (опционально)
     * @return редирект на страницу каталога */
    @PostMapping("/book/create")
    public String createBook(
            @RequestParam("name") String name,
            @RequestParam("author") String author,
            @RequestParam("year") int year,
            @RequestParam("countBook") int countBook,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "imageUrl", required = false) String imageUrl) {

        Book book = new Book(name, author, year, countBook, description, imageUrl);
        libraryService.createNewBook(book);
        return "redirect:/book";
    }

    /** Отображает форму редактирования книги.
     * @param id ID редактируемой книги
     * @param model модель для передачи данных в представление
     * @return имя шаблона "edit" или редирект, если книга не найдена */
    @GetMapping("/book/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model){
        Optional<Book> book = libraryService.findBookById(id);
        if(book.isPresent()){
            model.addAttribute("book", book.get());
            return "edit";
        }
        return "redirect:/book";
    }

    /** Обрабатывает обновление данных книги.
     * @param book объект книги с обновленными данными
     * @return редирект на страницу каталога */
    @PostMapping("/book/edit")
    public String editBook(@ModelAttribute Book book){
        libraryService.updateBook(book);
        return "redirect:/book";
    }

    /** Отображает форму бронирования книги.
     * @param id ID книги для бронирования
     * @param model модель для передачи данных в представление
     * @return имя шаблона "reservationForm" или редирект, если книга недоступна */
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

    /** Обрабатывает бронирование книги.
     * @param bookId ID бронируемой книги
     * @param startDate дата начала бронирования
     * @param endDate дата окончания бронирования
     * @param principal аутентифицированный пользователь
     * @param redirectAttributes атрибуты для редиректа
     * @return редирект на главную страницу или обратно на форму при ошибке
     * @throws IllegalArgumentException если книга не найдена
     * @throws IllegalStateException если книга уже забронирована */
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

    /** Удаляет книгу из каталога.
     * @param id ID удаляемой книги
     * @return редирект на страницу каталога */
    @GetMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable Long id){
        libraryService.deleteBook(id);
        return "redirect:/book";
    }

    /** Отображает форму регистрации нового пользователя.
     * @param model модель для передачи данных в представление
     * @return имя шаблона "registration" */
    @GetMapping("/register")
    public String showRegistrationFrom(Model model){
        model.addAttribute("userEntity", new UserEntity());
        return "registration";
    }

    /** Регистрация нового пользователя.
     * @param user данные нового пользователя
     * @param model модель для передачи данных в представление
     * @return редирект на страницу входа или обратно на форму при ошибке */
    @PostMapping("/register")
    public String adduser(@ModelAttribute UserEntity user, Model model)
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

    /** Отображает список бронирований текущего пользователя.
     * @param principal аутентифицированный пользователь
     * @param model модель для передачи данных в представление
     * @return имя шаблона "userBooks" */
    @GetMapping("/my-books")
    public String showUserBooks(Principal principal, Model model) {
        UserEntity user = userService.findByUsername(principal.getName());
        List<ReserveRecord> userBookings = reserveRecordService.findReserveRecordsByIdUser(user.getId());

        model.addAttribute("userBookings", userBookings);
        return "userBooks";
    }

    /** Обрабатывает возврат книги в библиотеку.
     * @param id ID бронирования
     * @param principal аутентифицированный пользователь
     * @return редирект на страницу личных бронирований */
    @GetMapping("/book/return/{id}")
    public String returnBook(@PathVariable Long id, Principal principal) {
        UserEntity user = userService.findByUsername(principal.getName());
        reserveRecordService.returnBook(id, user.getId());
        return "redirect:/my-books";
    }

    /** Отображает детальную информацию о книге.
     * @param id ID книги
     * @param model модель для передачи данных в представление
     * @param authentication данные аутентификации
     * @return имя шаблона "bookDetails" или редирект, если книга не найдена */
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
