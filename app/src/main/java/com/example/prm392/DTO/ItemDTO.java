package com.example.prm392.DTO;

import java.util.ArrayList;

public class ItemDTO{
    public String title;
    public String description;
    public String picUrl;
    public double price;

    public ItemDTO(String title, String description, String picUrl, double price) {
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.price = price;
    }
}