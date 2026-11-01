package com.example.myapplicationmeituan.ui.shop;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.myapplicationmeituan.R;
import com.example.myapplicationmeituan.bean.Shop;
import com.example.myapplicationmeituan.ui.profile.ProfileActivity;
import java.util.ArrayList;
import java.util.List;

public class ShopListActivity extends AppCompatActivity {

    private LinearLayout llShopContainer;
    private LinearLayout llCategory;
    private TextView tvEmpty;
    private EditText etSearch;

    private final List<Shop> allShops = new ArrayList<>();
    private final List<TextView> categoryNameViews = new ArrayList<>();
    private final List<TextView> categoryIconViews = new ArrayList<>();
    private String currentCategory = "全部";
    private String currentQuery = "";

    // 分类配置：名称 + 图标(emoji) + 图标底色
    private static final String[] CATEGORY_NAMES = {"全部", "汉堡披萨", "咖啡奶茶", "麻辣烫", "中式快餐"};
    private static final String[] CATEGORY_ICONS = {"🍽️", "🍔", "🧋", "🌶️", "🍚"};
    private static final int[] CATEGORY_COLORS = {
            0xFFE3F2FD, 0xFFFFF3E0, 0xFFE8F5E9, 0xFFFFEBEE, 0xFFF3E5F5
    };
    private static final int COLOR_SELECTED = 0xFFFFF0E0;
    private static final int COLOR_TEXT_NORMAL = 0xFF333333;
    private static final int COLOR_TEXT_SELECTED = 0xFFFF6600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        llShopContainer = findViewById(R.id.ll_shop_container);
        llCategory = findViewById(R.id.ll_category);
        tvEmpty = findViewById(R.id.tv_empty);
        etSearch = findViewById(R.id.et_search);

        // 右上角"我的"入口
        ImageView ivProfile = findViewById(R.id.iv_profile);
        Glide.with(this)
                .load(R.drawable.ic_avatar)
                .circleCrop()
                .into(ivProfile);
        ivProfile.setOnClickListener(v ->
                startActivity(new Intent(ShopListActivity.this, ProfileActivity.class)));

        initShopData();
        initCategoryBar();

        // 搜索框实时过滤
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim();
                renderShopList();
            }
        });

        renderShopList();
    }

    private void initShopData() {
        allShops.add(new Shop(1, "汉堡大王", "汉堡披萨", "新店开业优惠多多", 20, 3.0, R.drawable.shop_burger, "han bao da wang"));
        allShops.add(new Shop(2, "奶茶小铺", "咖啡奶茶", "第二杯半价活动", 15, 2.5, R.drawable.shop_milktea, "nai cha xiao pu"));
        allShops.add(new Shop(3, "麻辣烫馆", "麻辣烫", "满20减5", 20, 2.0, R.drawable.shop_malatang, "ma la tang guan"));
        allShops.add(new Shop(4, "塔斯汀·中国汉堡", "汉堡披萨", "中式汉堡 现烤现做", 18, 2.5, R.drawable.shop_tastine, "ta si ting zhong guo han bao"));
        allShops.add(new Shop(5, "蜜雪冰城", "咖啡奶茶", "全场第二杯半价", 12, 1.5, R.drawable.shop_mixue, "mi xue bing cheng"));
        allShops.add(new Shop(6, "张亮麻辣烫", "麻辣烫", "称重计费 食材新鲜", 20, 2.0, R.drawable.shop_zhangliang, "zhang liang ma la tang"));
        allShops.add(new Shop(7, "沙县小吃", "中式快餐", "人均15吃饱 出餐快", 15, 2.0, R.drawable.shop_shaxian, "sha xian xiao chi"));
    }

    private void initCategoryBar() {
        for (int i = 0; i < CATEGORY_NAMES.length; i++) {
            final int index = i;
            View item = LayoutInflater.from(this).inflate(R.layout.item_category, llCategory, false);
            TextView tvIcon = item.findViewById(R.id.tv_category_icon);
            TextView tvName = item.findViewById(R.id.tv_category_name);
            tvIcon.setText(CATEGORY_ICONS[i]);
            tvName.setText(CATEGORY_NAMES[i]);

            item.setOnClickListener(v -> {
                currentCategory = CATEGORY_NAMES[index];
                refreshCategorySelection();
                renderShopList();
            });

            llCategory.addView(item);
            categoryIconViews.add(tvIcon);
            categoryNameViews.add(tvName);
        }
        refreshCategorySelection();
    }

    private void refreshCategorySelection() {
        for (int i = 0; i < categoryNameViews.size(); i++) {
            boolean selected = CATEGORY_NAMES[i].equals(currentCategory);
            TextView tvName = categoryNameViews.get(i);
            TextView tvIcon = categoryIconViews.get(i);
            tvName.setTextColor(selected ? COLOR_TEXT_SELECTED : COLOR_TEXT_NORMAL);
            tvName.setTypeface(tvName.getTypeface(), selected ? Typeface.BOLD : Typeface.NORMAL);

            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(selected ? COLOR_SELECTED : CATEGORY_COLORS[i]);
            tvIcon.setBackground(circle);
        }
    }

    private void renderShopList() {
        llShopContainer.removeAllViews();
        String query = currentQuery.toLowerCase().trim();
        List<Shop> result = new ArrayList<>();
        for (Shop shop : allShops) {
            boolean categoryOk = currentCategory.equals("全部") || currentCategory.equals(shop.getCategory());
            boolean queryOk = query.isEmpty()
                    || shop.getName().contains(currentQuery)
                    || shop.getNotice().contains(currentQuery)
                    || shop.getPinyin().contains(query)                 // 拼音全拼，如 "mlt"->"ma la tang guan"
                    || pinyinInitials(shop.getPinyin()).contains(query); // 拼音首字母，如 "mlt"
            if (categoryOk && queryOk) {
                result.add(shop);
            }
        }
        for (Shop shop : result) {
            addShopItem(shop);
        }
        tvEmpty.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /** 取拼音各段首字母，如 "ma la tang guan" -> "mltg" */
    private String pinyinInitials(String pinyin) {
        StringBuilder sb = new StringBuilder();
        for (String part : pinyin.split(" ")) {
            if (!part.isEmpty()) {
                sb.append(part.charAt(0));
            }
        }
        return sb.toString();
    }

    private void addShopItem(Shop shop) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_shop, llShopContainer, false);
        ImageView ivShop = itemView.findViewById(R.id.iv_shop);
        TextView tvShopName = itemView.findViewById(R.id.tv_shop_name);
        TextView tvNotice = itemView.findViewById(R.id.tv_notice);
        TextView tvDelivery = itemView.findViewById(R.id.tv_delivery);

        Glide.with(this)
                .load(shop.getImageRes())
                .centerCrop()
                .into(ivShop);
        tvShopName.setText(shop.getName());
        tvNotice.setText(shop.getNotice());
        tvDelivery.setText("配送费：¥" + shop.getDeliveryFee());

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ShopListActivity.this, ShopActivity.class);
            intent.putExtra("shopId", shop.getId());
            intent.putExtra("shopName", shop.getName());
            intent.putExtra("shopNotice", shop.getNotice());
            intent.putExtra("sendPrice", shop.getSendPrice());
            intent.putExtra("deliveryFee", shop.getDeliveryFee());
            intent.putExtra("shopImageRes", shop.getImageRes());
            startActivity(intent);
        });
        llShopContainer.addView(itemView);
    }
}
