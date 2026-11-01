package com.example.myapplicationmeituan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplicationmeituan.ui.shop.ShopListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGoShop = findViewById(R.id.btn_go_shop);
        btnGoShop.setOnClickListener(v -> {
            // 这里先跳【店铺列表页面 ShopListActivity】
            Intent intent = new Intent(MainActivity.this, ShopListActivity.class);
            startActivity(intent);
        });
    }
}
