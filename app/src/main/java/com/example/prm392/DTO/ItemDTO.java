package com.example.prm392.DTO;

import java.util.ArrayList;

public class ItemDTO{
    public String title;
    public String description;
    public int quantity;
    public String picUrl;
    public double price;

    public ItemDTO(String title, String description,int quantity, double price, String picUrl ) {
        this.title = title;
        this.description = description;
        this.quantity = quantity;
        this.picUrl = picUrl;
        this.price = price;
    }
}