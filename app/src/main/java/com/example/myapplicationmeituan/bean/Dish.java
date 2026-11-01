package com.example.myapplicationmeituan.bean;

import java.io.Serializable;

public class Dish implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double price;
    private int sale;
    private int count;
    private int imageRes;

    public Dish(int id, String name, double price, int sale) {
        this(id, name, price, sale, 0);
    }

    public Dish(int id, String name, double price, int sale, int imageRes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sale = sale;
        this.count = 0;
        this.imageRes = imageRes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }
}
