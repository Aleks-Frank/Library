package com.example.Library.config;

import com.example.Library.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/** Кастомный провайдер аутентификации, интегрирующийся с внешней системой аутентификации.
 * Реализует интерфейс AuthenticationProvider для обработки учетных данных пользователей.
 * @see AuthenticationProvider
 * @see UserService */
@Component
public class AtlassianCrowdAuthenticationProvider implements AuthenticationProvider {

    /** Логгер для записи событий и ошибок сервиса. */
    private static final Logger log = LoggerFactory.getLogger(AtlassianCrowdAuthenticationProvider.class);

    /** Экземпляр сервиса UserService */
    @Autowired
    private UserService userService;

    /** Проверяет учетные данные пользователя.
     * @param authentication объект аутентификации с учетными данными
     * @return аутентификационный токен с правами пользователя
     * @throws AuthenticationException если аутентификация не удалась */
    @Override
    public Authentication authenticate(Authentication authentication) throws
            AuthenticationException
    {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        User user = callMyAuthRestService(username, password);
        if (user == null)
        {
            throw new AuthenticationServiceException("user not found");
        }
        log.debug("Пользователь {} авторизирован", user.getUsername());
        return new UsernamePasswordAuthenticationToken(user.getUsername(),
                user.getPassword(), user.getAuthorities());
    }

    /** Определяет поддерживаемый тип аутентификации.
     * @param authentication класс аутентификации для проверки
     * @return true если поддерживается UsernamePasswordAuthenticationToken */
    @Override
    public boolean supports(Class<?> authentication)
    {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /** Выполняет проверку учетных данных через REST-сервис.
     * @param username имя пользователя
     * @param password пароль
     * @return объект User с правами или null если аутентификация не удалась */
    private User callMyAuthRestService(String username, String password)
    {
        try {
            UserDetails userDetails = userService.loadUserByUsername(username);
            if (userDetails != null && userDetails.getPassword().equals(password)) {
                return new User(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            } else {
                return null;
            }
        } catch (UsernameNotFoundException e) {
            return null;
        }
    }
}
