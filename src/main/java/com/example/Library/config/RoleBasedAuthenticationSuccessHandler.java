package com.example.Library.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Обработчик успешной аутентификации с перенаправлением по ролям.
 * Реализует различное поведение после входа для ADMIN и USER ролей.
 * @see AuthenticationSuccessHandler */
@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /** Перенаправляет пользователя после успешного входа в зависимости от его роли.
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param authentication объект аутентификации
     * @throws IOException при ошибках перенаправления */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            redirectStrategy.sendRedirect(request, response, "/book");
        } else {
            redirectStrategy.sendRedirect(request, response, "/");
        }
    }
}
