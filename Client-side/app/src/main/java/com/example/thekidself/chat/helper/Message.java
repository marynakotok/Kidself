package com.example.thekidself.chat.helper;

public class Message {
    private String code_user;
    private String message;
    private String created_on;
    private String username;
    private String code_family;

    public String getCode_user() {
        return code_user;
    }

    public void setCode_user(String code_user) {
        this.code_user = code_user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode_family() {
        return code_family;
    }

    public void setCode_family(String code_family) {
        this.code_family = code_family;
    }

    public Message(String code_user, String message, String created_on, String username, String code_family) {
        this.code_user = code_user;
        this.message = message;
        this.created_on = created_on;
        this.username = username;
        this.code_family = code_family;
    }


}