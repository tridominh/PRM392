package com.example.prm392.Domain;

public class UserDomain {
    private String userId;
    private String userName;
    private String address;
    private String phoneNumber;
    public UserDomain() {
    }

    public UserDomain(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public UserDomain(String userId, String userName, String address, String phoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.address = address;
        this.phoneNumber = phoneNumber;
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
