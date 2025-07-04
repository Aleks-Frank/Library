package com.example.Library.config;

import com.example.Library.entity.ROLE;
import net.bull.javamelody.MonitoringFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/** Основная конфигурация безопасности приложения.
 * @see EnableWebSecurity
 * @see SecurityFilterChain */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig{

    /** Экземпляр класса RoleBasedAuthenticationSuccessHandler */
    @Autowired
    private RoleBasedAuthenticationSuccessHandler successHandler;

    /** Настраивает кодировщик паролей.
     * @return экземпляр PasswordEncoder */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /** Конфигурирует цепочку фильтров безопасности.
     * @param security строитель конфигурации безопасности
     * @return настроенный SecurityFilterChain
     * @throws Exception при ошибках конфигурации */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception{
        return security
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/book/reserve/{id}").permitAll()
                        .requestMatchers("/book/reserve").permitAll()
                        .requestMatchers("/book").hasRole(ROLE.ADMIN.name())
                        .requestMatchers("/book/create").hasRole(ROLE.ADMIN.name())
                        .requestMatchers("/book/edit/{id}").hasRole(ROLE.ADMIN.name())
                        .requestMatchers("/book/edit").hasRole(ROLE.ADMIN.name())
                        .requestMatchers("/book/delete/{id}").hasRole(ROLE.ADMIN.name())
                        .requestMatchers("/reservations").hasRole(ROLE.ADMIN.name())
                        .requestMatchers("/", "/login", "/register", "/logout", "/monitoring",
                                "/monitoring/**",
                                "/javamelody/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home")
                        .successHandler(successHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .build();
    }

}
