package com.example.thekidself.activity.forgot_password.models;


public class ServerRequest {

    private String operation;
    private User user;

    public void setOperation(String operation) {
        this.operation = operation;
    } //resPassReq

    public void setUser(User user) {
        this.user = user;
    } //resPass

    public User getUser() {
        return user;
    }

    public String getOperation() {
        return operation;
    }
}