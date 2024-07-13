package com.example.prm392.Domain;

public class UserDomain {
    private String userId;
    private String userName;
    private String address;
    private String phoneNumber;
    public UserDomain() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserDomain(String userId, String userName, String address, String phoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
