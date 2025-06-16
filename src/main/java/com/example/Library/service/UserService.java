package com.example.Library.service;

import com.example.Library.entity.ROLE;
import com.example.Library.entity.UserEntity;
import com.example.Library.repository.UserRepositoryDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepositoryDB userRepositoryDB;

    @Autowired
    public UserService(UserRepositoryDB userRepositoryDB) {
        this.userRepositoryDB = userRepositoryDB;
    }

    public void addUser(String username, String password) throws Exception {
        UserEntity entity = userRepositoryDB.findByUsername(username);
        if (entity != null){
            throw new Exception("User already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(preparePassword(password));
        user.setRoles(Set.of(ROLE.USER));

        userRepositoryDB.save(user);
    }

    public void saveUser(UserEntity user){
        userRepositoryDB.save(user);
    }

    private String preparePassword(String password) {
        return password;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepositoryDB.findByUsername(username);
        if (entity == null){
            throw new UsernameNotFoundException("User not found");
        }
        return new User(entity.getUsername(), entity.getPassword(), extractRoles(entity));
    }

    private Collection<? extends GrantedAuthority> extractRoles(UserEntity entity) {
        return entity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    public UserEntity findByUsername(String username){
        return userRepositoryDB.findByUsername(username);
    }
}
