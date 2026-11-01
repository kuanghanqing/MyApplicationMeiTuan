package com.example.myapplicationmeituan.ui.shop;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplicationmeituan.R;
import com.example.myapplicationmeituan.bean.Address;
import com.example.myapplicationmeituan.bean.Dish;
import com.example.myapplicationmeituan.storage.ProfileStorage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckoutActivity extends AppCompatActivity {

    private static final String[] PAY_NAMES = {"支付宝", "微信支付", "余额支付"};
    private static final String[] PAY_ICONS = {"💙", "💚", "💰"};

    private TextView tvCheckoutShopName, tvGoodsAmount, tvDeliveryFee, tvTotalAmount, tvPayAmount;
    private TextView tvSuccessAmount, tvSuccessOrder, tvSuccessMethod;
    private LinearLayout llGoodsList, llPayMethods, llSuccess, llPayBar;
    private ScrollView svCheckout;
    private Button btnPay, btnDone;
    private ImageView ivBack;

    private final List<View> payRows = new ArrayList<>();
    private int selectedPay = 0;

    private String shopName = "";
    private double deliveryFee = 0.0;
    private ArrayList<Dish> cartList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        ivBack = findViewById(R.id.iv_back);
        tvCheckoutShopName = findViewById(R.id.tv_checkout_shop_name);
        llGoodsList = findViewById(R.id.ll_goods_list);
        tvGoodsAmount = findViewById(R.id.tv_goods_amount);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvPayAmount = findViewById(R.id.tv_pay_amount);
        llPayMethods = findViewById(R.id.ll_pay_methods);
        llSuccess = findViewById(R.id.ll_success);
        llPayBar = findViewById(R.id.ll_pay_bar);
        svCheckout = findViewById(R.id.sv_checkout);
        btnPay = findViewById(R.id.btn_pay);
        btnDone = findViewById(R.id.btn_done);
        tvSuccessAmount = findViewById(R.id.tv_success_amount);
        tvSuccessOrder = findViewById(R.id.tv_success_order);
        tvSuccessMethod = findViewById(R.id.tv_success_method);

        // 接收结算数据
        shopName = getIntent().getStringExtra("shopName");
        if (shopName == null) shopName = "";
        deliveryFee = getIntent().getDoubleExtra("deliveryFee", 0.0);
        Object data = getIntent().getSerializableExtra("cartList");
        if (data instanceof ArrayList) {
            cartList = (ArrayList<Dish>) data;
        }

        tvCheckoutShopName.setText(shopName);

        // 配送地址：优先显示个人中心设置的默认地址
        loadAddress();

        // 商品清单
        double goodsAmount = 0;
        for (Dish d : cartList) {
            goodsAmount += d.getPrice() * d.getCount();
            addGoodsRow(d);
        }
        double total = goodsAmount + deliveryFee;
        tvGoodsAmount.setText("¥" + fmt(goodsAmount));
        tvDeliveryFee.setText("¥" + fmt(deliveryFee));
        tvTotalAmount.setText("¥" + fmt(total));
        tvPayAmount.setText("¥" + fmt(total));

        initPayMethods();

        ivBack.setOnClickListener(v -> finish());
        btnDone.setOnClickListener(v -> finish());
        btnPay.setOnClickListener(v -> simulatePay(total));
    }

    /** 读取个人中心设置的默认收货地址填充地址卡；未设置时使用默认文案 */
    private void loadAddress() {
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvAddressContact = findViewById(R.id.tv_address_contact);
        Address address = new ProfileStorage(this).getDefaultAddress();
        if (address != null) {
            String contact = address.getName() + " " + maskPhone(address.getPhone());
            tvAddress.setText(address.getDetail());
            tvAddressContact.setText(contact);
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone == null ? "" : phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    private void addGoodsRow(Dish dish) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        row.setPadding(0, 8, 0, 8);
        row.setLayoutParams(rowLp);

        TextView tvName = new TextView(this);
        tvName.setText(dish.getName());
        tvName.setTextSize(14);
        tvName.setTextColor(0xFF333333);
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvCount = new TextView(this);
        tvCount.setText("×" + dish.getCount());
        tvCount.setTextSize(14);
        tvCount.setTextColor(0xFF999999);
        LinearLayout.LayoutParams countLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        countLp.setMarginStart(10);
        tvCount.setLayoutParams(countLp);

        TextView tvSub = new TextView(this);
        tvSub.setText("¥" + fmt(dish.getPrice() * dish.getCount()));
        tvSub.setTextSize(14);
        tvSub.setTextColor(0xFF333333);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.setMarginStart(10);
        tvSub.setLayoutParams(subLp);

        row.addView(tvName);
        row.addView(tvCount);
        row.addView(tvSub);
        llGoodsList.addView(row);
    }

    private void initPayMethods() {
        for (int i = 0; i < PAY_NAMES.length; i++) {
            final int index = i;
            View row = LayoutInflater.from(this).inflate(R.layout.item_pay_method, llPayMethods, false);
            TextView tvIcon = row.findViewById(R.id.tv_pay_icon);
            TextView tvName = row.findViewById(R.id.tv_pay_name);
            tvIcon.setText(PAY_ICONS[i]);
            tvName.setText(PAY_NAMES[i]);
            row.setOnClickListener(v -> {
                selectedPay = index;
                refreshPaySelection();
            });
            llPayMethods.addView(row);
            payRows.add(row);
        }
        refreshPaySelection();
    }

    private void refreshPaySelection() {
        for (int i = 0; i < payRows.size(); i++) {
            View row = payRows.get(i);
            TextView tvCheck = row.findViewById(R.id.tv_pay_check);
            boolean selected = (i == selectedPay);
            row.setBackgroundColor(selected ? 0xFFFFF7EE : Color.WHITE);
            tvCheck.setText(selected ? "✅" : "○");
            tvCheck.setTextColor(selected ? 0xFFFF6600 : 0xFFCCCCCC);
        }
    }

    private void simulatePay(final double total) {
        btnPay.setEnabled(false);
        btnPay.setText("支付中…");
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String ts = String.valueOf(System.currentTimeMillis());
            String orderNo = "MT" + ts.substring(ts.length() - 10)
                    + (100 + new Random().nextInt(900));
            tvSuccessAmount.setText("¥" + fmt(total));
            tvSuccessOrder.setText("订单号：" + orderNo);
            tvSuccessMethod.setText("支付方式：" + PAY_NAMES[selectedPay]);
            llSuccess.setVisibility(View.VISIBLE);
            svCheckout.setVisibility(View.GONE);
            llPayBar.setVisibility(View.GONE);
            Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
        }, 1200);
    }

    private String fmt(double v) {
        return new DecimalFormat("0.##").format(v);
    }
}
