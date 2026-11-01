package com.example.myapplicationmeituan.bean;

import java.io.Serializable;

/**
 * 收货地址
 */
public class Address implements Serializable {

    private int id;
    private String name;      // 收货人
    private String phone;     // 手机号
    private String detail;    // 详细地址
    private String tag;       // 标签：家/公司/学校
    private boolean isDefault; // 是否默认地址

    public Address() {
    }

    public Address(int id, String name, String phone, String detail, String tag, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.detail = detail;
        this.tag = tag;
        this.isDefault = isDefault;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
