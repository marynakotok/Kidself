package com.example.thekidself.activity.forgot_password.models;

public class User {

    private String name;
    private String email;
    private String password;
    private String code;


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCode(String code) {
        this.code = code;
    }

}