package com.example.myapplicationmeituan.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.myapplicationmeituan.bean.Dish;
import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {
    private List<Dish> cartList = new ArrayList<>();

    public void addDish(Dish dish){
        boolean has = false;
        for(Dish d : cartList){
            if(d.getId() == dish.getId()){
                d.setCount(d.getCount()+1);
                has = true;
                break;
            }
        }
        if(!has){
            dish.setCount(1);
            cartList.add(dish);
        }
    }

    public void minusDish(Dish dish){
        for(Dish d : cartList){
            if(d.getId() == dish.getId()){
                d.setCount(d.getCount()-1);
                if(d.getCount() <= 0){
                    cartList.remove(d);
                }
                break;
            }
        }
    }

    public double getTotalPrice(){
        double sum = 0;
        for(Dish d : cartList){
            sum += d.getPrice() * d.getCount();
        }
        return sum;
    }

    public List<Dish> getCartList(){
        return cartList;
    }
}