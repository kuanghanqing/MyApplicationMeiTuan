package com.example.myapplicationmeituan.bean;

public class Shop {
    private int id;
    private String name;
    private String category; //商铺分类
    private String notice; //公告信息
    private int sendPrice; //起送价
    private double deliveryFee; //配送费
    private int imageRes; //店铺图资源
    private String pinyin; //店铺名称拼音（小写，空格分隔），用于拼音搜索

    public Shop(int id, String name, String category, String notice, int sendPrice, double deliveryFee, int imageRes) {
        this(id, name, category, notice, sendPrice, deliveryFee, imageRes, "");
    }

    public Shop(int id, String name, String category, String notice, int sendPrice, double deliveryFee, int imageRes, String pinyin) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.notice = notice;
        this.sendPrice = sendPrice;
        this.deliveryFee = deliveryFee;
        this.imageRes = imageRes;
        this.pinyin = pinyin;
    }

    //getter
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getNotice() { return notice; }
    public int getSendPrice() { return sendPrice; }
    public double getDeliveryFee() { return deliveryFee; }
    public int getImageRes() { return imageRes; }
    public String getPinyin() { return pinyin; }
}
