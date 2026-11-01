package com.example.myapplicationmeituan.bean;

public class CartItem {
    private Dish dish;
    private int count;

    public CartItem(Dish dish, int count) {
        this.dish = dish;
        this.count = count;
    }

    public Dish getDish() {
        return dish;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}