package com.example.Library.restController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Контроллер для обработки аутентификации пользователей.
 * Предоставляет endpoint для отображения страницы входа в систему. */
@Controller
public class LoginController {

    /** Отображает страницу входа в систему.
     * @return имя шаблона "login" */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}