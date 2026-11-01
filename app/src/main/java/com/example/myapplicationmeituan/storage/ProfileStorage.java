package com.example.myapplicationmeituan.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapplicationmeituan.bean.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人中心数据存储（SharedPreferences 本地持久化）
 * 保存：昵称、手机号、性别、收货地址列表
 */
public class   ProfileStorage {

    private static final String PREFS = "user_profile";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_ADDRESSES = "addresses";
    private static final String KEY_NEXT_ID = "next_id";

    private final SharedPreferences sp;

    public ProfileStorage(Context context) {
        sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // ================= 个人信息 =================

    public void saveNickname(String nickname) {
        sp.edit().putString(KEY_NICKNAME, nickname).apply();
    }

    public String getNickname() {
        return sp.getString(KEY_NICKNAME, "");
    }

    public void savePhone(String phone) {
        sp.edit().putString(KEY_PHONE, phone).apply();
    }

    public String getPhone() {
        return sp.getString(KEY_PHONE, "");
    }

    public void saveGender(String gender) {
        sp.edit().putString(KEY_GENDER, gender).apply();
    }

    public String getGender() {
        return sp.getString(KEY_GENDER, "保密");
    }

    // ================= 收货地址 =================

    public List<Address> loadAddresses() {
        List<Address> list = new ArrayList<>();
        String json = sp.getString(KEY_ADDRESSES, "");
        if (json.isEmpty()) {
            return list;
        }
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                Address a = new Address();
                a.setId(o.optInt("id", i + 1));
                a.setName(o.optString("name"));
                a.setPhone(o.optString("phone"));
                a.setDetail(o.optString("detail"));
                a.setTag(o.optString("tag", "家"));
                a.setDefault(o.optBoolean("isDefault"));
                list.add(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void saveAddresses(List<Address> list) {
        JSONArray arr = new JSONArray();
        for (Address a : list) {
            JSONObject o = new JSONObject();
            try {
                o.put("id", a.getId());
                o.put("name", a.getName());
                o.put("phone", a.getPhone());
                o.put("detail", a.getDetail());
                o.put("tag", a.getTag());
                o.put("isDefault", a.isDefault());
                arr.put(o);
            } catch (JSONException ignored) {
            }
        }
        sp.edit().putString(KEY_ADDRESSES, arr.toString()).apply();
    }

    /** 生成自增地址 id */
    public int nextAddressId() {
        int id = sp.getInt(KEY_NEXT_ID, 1);
        sp.edit().putInt(KEY_NEXT_ID, id + 1).apply();
        return id;
    }

    /** 取默认地址；没有默认则取第一个；没有地址返回 null */
    public Address getDefaultAddress() {
        List<Address> list = loadAddresses();
        if (list.isEmpty()) {
            return null;
        }
        for (Address a : list) {
            if (a.isDefault()) {
                return a;
            }
        }
        return list.get(0);
    }

    /** 清空全部个人数据 */
    public void clearAll() {
        sp.edit().clear().apply();
    }
}
