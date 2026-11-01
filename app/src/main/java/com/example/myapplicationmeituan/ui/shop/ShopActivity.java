package com.example.myapplicationmeituan.ui.shop;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplicationmeituan.R;
import com.example.myapplicationmeituan.adapter.DishAdapter;
import com.example.myapplicationmeituan.bean.Dish;
import com.example.myapplicationmeituan.viewmodel.CartViewModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {

    private static final int COLOR_ORANGE = 0xFFFF6600;
    private static final int COLOR_GRAY = 0xFFCCCCCC;

    private RecyclerView rvDish;
    private TextView tvTotal;
    private TextView tvCartBadge;
    private ImageView ivCart;
    private Button btnSettle;
    private DishAdapter dishAdapter;
    private List<Dish> dishList = new ArrayList<>();
    private CartViewModel cartViewModel;

    private String shopName = "";
    private int sendPrice = 20;
    private double deliveryFee = 3.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        rvDish = findViewById(R.id.rv_dish);
        tvTotal = findViewById(R.id.tv_total);
        btnSettle = findViewById(R.id.btn_settle);
        ivCart = findViewById(R.id.iv_cart);
        tvCartBadge = findViewById(R.id.tv_cart_badge);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // 返回按钮：回到商铺列表
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        // 显示商铺 logo 与名称
        ImageView ivShopLogo = findViewById(R.id.iv_shop_logo);
        TextView tvShopName = findViewById(R.id.tv_shop_name);
        TextView tvNotice = findViewById(R.id.tv_notice);
        TextView tvPriceInfo = findViewById(R.id.tv_price_info);
        int imageRes = getIntent().getIntExtra("shopImageRes", 0);
        shopName = getIntent().getStringExtra("shopName");
        if (shopName == null) shopName = "";
        String shopNotice = getIntent().getStringExtra("shopNotice");
        sendPrice = getIntent().getIntExtra("sendPrice", 20);
        deliveryFee = getIntent().getDoubleExtra("deliveryFee", 3.0);
        tvShopName.setText(shopName);
        if (shopNotice != null) {
            tvNotice.setText(shopNotice);
        }
        tvPriceInfo.setText("起送¥" + sendPrice + "｜配送费¥" + fmt(deliveryFee));
        if (imageRes != 0) {
            ivShopLogo.setImageResource(imageRes);
        }

        // 根据商铺 ID 加载对应菜单
        int shopId = getIntent().getIntExtra("shopId", 1);
        initDishData(shopId);

        dishAdapter = new DishAdapter(dishList);
        rvDish.setLayoutManager(new LinearLayoutManager(this));
        rvDish.setAdapter(dishAdapter);

        dishAdapter.setListener(new DishAdapter.OnDishClickListener() {
            @Override
            public void onAdd(Dish dish) {
                cartViewModel.addDish(dish);
                dishAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }

            @Override
            public void onMinus(Dish dish) {
                cartViewModel.minusDish(dish);
                dishAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }
        });

        // 点击购物车图标：弹出购物车面板
        ivCart.setOnClickListener(v -> showCartDialog());

        // 去结算：跳转支付结算页
        btnSettle.setOnClickListener(v -> onSettleClick());

        updateTotalPrice();
    }

    private void onSettleClick() {
        List<Dish> cart = cartViewModel.getCartList();
        if (cart.isEmpty()) {
            Toast.makeText(this, "购物车是空的", Toast.LENGTH_SHORT).show();
            return;
        }
        double total = cartViewModel.getTotalPrice();
        if (total < sendPrice) {
            Toast.makeText(this, "还差¥" + fmt(sendPrice - total) + "起送", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(ShopActivity.this, CheckoutActivity.class);
        intent.putExtra("shopName", shopName);
        intent.putExtra("deliveryFee", deliveryFee);
        intent.putExtra("cartList", new ArrayList<>(cart));
        startActivity(intent);
    }

    // ---------- 购物车面板 ----------

    private void showCartDialog() {
        if (cartViewModel.getCartList().isEmpty()) {
            Toast.makeText(this, "购物车是空的", Toast.LENGTH_SHORT).show();
            return;
        }
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cart_panel);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
        dialog.setCanceledOnTouchOutside(true);

        LinearLayout llCartList = dialog.findViewById(R.id.ll_cart_list);
        TextView tvCartTotal = dialog.findViewById(R.id.tv_cart_total);
        TextView tvClearCart = dialog.findViewById(R.id.tv_clear_cart);
        Button btnCartSettle = dialog.findViewById(R.id.btn_cart_settle);

        renderCartRows(dialog, llCartList, tvCartTotal);
        tvClearCart.setOnClickListener(v -> {
            cartViewModel.getCartList().clear();
            dishAdapter.notifyDataSetChanged();
            updateTotalPrice();
            renderCartRows(dialog, llCartList, tvCartTotal);
            if (cartViewModel.getCartList().isEmpty()) {
                dialog.dismiss();
            }
        });
        btnCartSettle.setOnClickListener(v -> {
            dialog.dismiss();
            onSettleClick();
        });
        dialog.show();
    }

    private void renderCartRows(Dialog dialog, LinearLayout llCartList, TextView tvCartTotal) {
        llCartList.removeAllViews();
        List<Dish> cart = cartViewModel.getCartList();
        for (Dish dish : cart) {
            llCartList.addView(buildCartRow(dialog, llCartList, tvCartTotal, dish));
        }
        tvCartTotal.setText("¥" + fmt(cartViewModel.getTotalPrice()));
    }

    private LinearLayout buildCartRow(Dialog dialog, LinearLayout llCartList, TextView tvCartTotal, Dish dish) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 10, 0, 10);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 名称 + 单价
        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        left.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvName = new TextView(this);
        tvName.setText(dish.getName());
        tvName.setTextSize(15);
        tvName.setTextColor(0xFF333333);
        left.addView(tvName);

        TextView tvUnit = new TextView(this);
        tvUnit.setText("¥" + fmt(dish.getPrice()) + " / 份");
        tvUnit.setTextSize(12);
        tvUnit.setTextColor(0xFF999999);
        left.addView(tvUnit);

        // 数量控制
        TextView tvMinus = buildOpButton("−", 0xFFF0F0F0, 0xFF666666);
        tvMinus.setOnClickListener(v -> {
            cartViewModel.minusDish(dish);
            dishAdapter.notifyDataSetChanged();
            updateTotalPrice();
            renderCartRows(dialog, llCartList, tvCartTotal);
            if (cartViewModel.getCartList().isEmpty()) {
                dialog.dismiss();
            }
        });

        TextView tvCount = new TextView(this);
        tvCount.setText(String.valueOf(dish.getCount()));
        tvCount.setTextSize(15);
        tvCount.setTextColor(0xFF333333);
        tvCount.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams countLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        countLp.setMarginStart(6);
        countLp.setMarginEnd(6);
        tvCount.setLayoutParams(countLp);

        TextView tvPlus = buildOpButton("+", COLOR_ORANGE, Color.WHITE);
        tvPlus.setOnClickListener(v -> {
            cartViewModel.addDish(dish);
            dishAdapter.notifyDataSetChanged();
            updateTotalPrice();
            renderCartRows(dialog, llCartList, tvCartTotal);
        });

        // 小计
        TextView tvSub = new TextView(this);
        tvSub.setText("¥" + fmt(dish.getPrice() * dish.getCount()));
        tvSub.setTextSize(15);
        tvSub.setTypeface(tvSub.getTypeface(), Typeface.BOLD);
        tvSub.setTextColor(0xFF333333);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.setMarginStart(14);
        tvSub.setLayoutParams(subLp);

        row.addView(left);
        row.addView(tvMinus);
        row.addView(tvCount);
        row.addView(tvPlus);
        row.addView(tvSub);
        return row;
    }

    private TextView buildOpButton(String text, int bgColor, int textColor) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(textColor);
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(bgColor);
        tv.setBackground(gd);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(30, 30);
        lp.setMargins(3, 0, 3, 0);
        tv.setLayoutParams(lp);
        return tv;
    }

    // ---------- 菜单数据 ----------

    private void initDishData(int shopId){
        if (shopId == 2) { // 奶茶小铺
            dishList.add(new Dish(1,"珍珠奶茶",9.0,320, R.drawable.dish_tea_pearl));
            dishList.add(new Dish(2,"手打柠檬茶",8.0,210, R.drawable.dish_tea_lemon));
            dishList.add(new Dish(3,"杨枝甘露",15.0,180, R.drawable.dish_tea_mango));
            dishList.add(new Dish(4,"红豆布丁奶茶",10.0,260, R.drawable.dish_tea_redbean));
            dishList.add(new Dish(5,"草莓波波冰",13.0,150, R.drawable.dish_tea_strawberry));
        } else if (shopId == 3) { // 麻辣烫馆
            dishList.add(new Dish(1,"麻辣牛肉串",12.0,200, R.drawable.dish_ma_beef));
            dishList.add(new Dish(2,"爆汁鱼丸",8.0,260, R.drawable.dish_ma_fishball));
            dishList.add(new Dish(3,"秘制豆腐",6.0,180, R.drawable.dish_ma_tofu));
            dishList.add(new Dish(4,"红薯宽粉",9.0,140, R.drawable.dish_ma_noodle));
            dishList.add(new Dish(5,"时蔬拼盘",7.0,120, R.drawable.dish_ma_veg));
        } else if (shopId == 4) { // 塔斯汀·中国汉堡
            dishList.add(new Dish(1,"北京烤鸭汉堡",15.0,190, R.drawable.dish_tst_duck));
            dishList.add(new Dish(2,"藤椒鸡腿堡",13.5,260, R.drawable.dish_tst_chicken));
            dishList.add(new Dish(3,"板烧凤梨堡",14.5,170, R.drawable.dish_tst_pineapple));
            dishList.add(new Dish(4,"脆香粗薯条",7.0,220, R.drawable.dish_tst_fries));
            dishList.add(new Dish(5,"冰柠可乐",5.0,300, R.drawable.dish_tst_cola));
        } else if (shopId == 5) { // 蜜雪冰城
            dishList.add(new Dish(1,"冰鲜柠檬水",4.0,600, R.drawable.dish_mx_lemon));
            dishList.add(new Dish(2,"满杯百香果",6.0,480, R.drawable.dish_mx_passion));
            dishList.add(new Dish(3,"芋泥啵啵奶茶",7.0,380, R.drawable.dish_mx_taro));
            dishList.add(new Dish(4,"摇摇奶昔",8.0,290, R.drawable.dish_mx_shake));
            dishList.add(new Dish(5,"原味甜筒冰淇淋",3.0,520, R.drawable.dish_mx_ice));
        } else if (shopId == 6) { // 张亮麻辣烫
            dishList.add(new Dish(1,"招牌麻辣烫单人餐",18.0,260, R.drawable.dish_zl_combo));
            dishList.add(new Dish(2,"精品肥牛卷",12.0,210, R.drawable.dish_zl_beef));
            dishList.add(new Dish(3,"午餐肉",6.0,240, R.drawable.dish_zl_luncheon));
            dishList.add(new Dish(4,"鹌鹑蛋",5.0,190, R.drawable.dish_zl_quail));
            dishList.add(new Dish(5,"华丰面",4.0,260, R.drawable.dish_zl_noodle));
        } else if (shopId == 7) { // 沙县小吃
            dishList.add(new Dish(1,"花生酱拌面",7.0,420, R.drawable.dish_sx_noodle));
            dishList.add(new Dish(2,"鲜肉蒸饺",8.0,350, R.drawable.dish_sx_dumpling));
            dishList.add(new Dish(3,"大骨汤馄饨",9.0,260, R.drawable.dish_sx_wonton));
            dishList.add(new Dish(4,"卤味鸡腿",10.0,190, R.drawable.dish_sx_chicken));
            dishList.add(new Dish(5,"秘制茶叶蛋",2.5,480, R.drawable.dish_sx_egg));
        } else { // 汉堡大王
            dishList.add(new Dish(1,"香辣鸡腿堡",12.5,230, R.drawable.dish_burger));
            dishList.add(new Dish(2,"薯条",8.0,156, R.drawable.dish_fries));
            dishList.add(new Dish(3,"可乐",4.0,320, R.drawable.dish_cola));
            dishList.add(new Dish(4,"奥尔良烤翅",9.9,188, R.drawable.dish_wing));
            dishList.add(new Dish(5,"鸡肉卷",11.0,96, R.drawable.dish_wrap));
        }
    }

    // ---------- 底部栏状态 ----------

    private void updateTotalPrice(){
        double total = cartViewModel.getTotalPrice();
        tvTotal.setText("¥" + fmt(total));

        int count = 0;
        for (Dish d : cartViewModel.getCartList()) {
            count += d.getCount();
        }
        tvCartBadge.setText(String.valueOf(count));
        tvCartBadge.setVisibility(count > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
        ivCart.setColorFilter(count > 0 ? COLOR_ORANGE : 0xFF999999);

        if (count == 0) {
            btnSettle.setText("去结算");
            btnSettle.setBackgroundTintList(ColorStateList.valueOf(COLOR_GRAY));
        } else if (total < sendPrice) {
            btnSettle.setText("还差¥" + fmt(sendPrice - total) + "起送");
            btnSettle.setBackgroundTintList(ColorStateList.valueOf(COLOR_GRAY));
        } else {
            btnSettle.setText("去结算");
            btnSettle.setBackgroundTintList(ColorStateList.valueOf(COLOR_ORANGE));
        }
    }

    private String fmt(double v) {
        return new DecimalFormat("0.##").format(v);
    }
}
