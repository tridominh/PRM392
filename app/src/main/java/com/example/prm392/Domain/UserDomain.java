package com.example.prm392.Domain;

public class UserDomain {
    private String userId;
    private String userName;

    public UserDomain() {
    }

    public UserDomain(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
