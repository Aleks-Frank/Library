package com.example.Library.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    @ElementCollection
    private List<Book> myBookList;

    private String username;

    private String password;

    @ElementCollection(targetClass = ROLE.class, fetch = FetchType.EAGER)
    private Set<ROLE> role = new HashSet<>();

    public Set<ROLE> getRoles() {
        return role;
    }

    public void setRoles(Set<ROLE> role) {
        this.role = role;
    }

    public UserEntity(String login, String password, Set<ROLE> role) {
        this.username = login;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public List<Book> getMyBookList() {
        return myBookList;
    }

    public void setMyBookList(List<Book> myBookList) {
        this.myBookList = myBookList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserEntity(String firstName){
        this.firstName = firstName;
    }

    public UserEntity(){
    }

}
