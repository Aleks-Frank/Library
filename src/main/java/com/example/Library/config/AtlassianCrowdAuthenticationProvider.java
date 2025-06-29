package com.example.Library.config;

import com.example.Library.service.LibraryServiceIMPL;
import com.example.Library.service.UserService;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Component
public class AtlassianCrowdAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(AtlassianCrowdAuthenticationProvider.class);

    @Autowired
    private UserService userService;

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


    @Override
    public boolean supports(Class<?> authentication)
    {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

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
