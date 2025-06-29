package com.example.Library.config;


import com.example.Library.entity.ROLE;
import com.example.Library.entity.UserEntity;
import com.example.Library.repository.UserRepositoryDB;
import com.example.Library.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MyInMemoryUserDetailService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(MyInMemoryUserDetailService.class);

    private final UserRepositoryDB userRepositoryDB;

    @Autowired
    private UserService userService;

    public MyInMemoryUserDetailService(UserRepositoryDB userRepositoryDB) {
        this.userRepositoryDB = userRepositoryDB;
    }

    @PostConstruct
    public void init() {
        if (userRepositoryDB.findByUsername("user") == null) {
            UserEntity user = new UserEntity("user", "123", Set.of(ROLE.USER));
            userService.saveUser(user);
            log.debug("Создан пользователь с именем {} и паролем {}", user.getUsername(), user.getPassword());
        }
        if (userRepositoryDB.findByUsername("admin") == null) {
            UserEntity admin = new UserEntity("admin", "admin", Set.of(ROLE.ADMIN));
            userService.saveUser(admin);
            log.debug("Создан пользователь с именем {} и паролем {}", admin.getUsername(), admin.getPassword());
        }
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        try {
            return userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found");
        }
    }


    private Collection<GrantedAuthority> mapRoles(UserEntity appUser)
    {
        return appUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" +
                        role.name())).collect(Collectors.toList());
    }
}

