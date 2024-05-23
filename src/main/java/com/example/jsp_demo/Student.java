package com.example.jsp_demo;

import java.sql.Date;

public class Student {
    private int id;
    private String username;
    private String univ;
    private Date birth;
    private String email;
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getUniv() {
        return univ;
    }

    public Date getBirth() {
        return birth;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUniv(String univ) {
        this.univ = univ;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
