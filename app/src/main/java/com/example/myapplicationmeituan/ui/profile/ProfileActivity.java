package com.example.myapplicationmeituan.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplicationmeituan.R;
import com.example.myapplicationmeituan.storage.ProfileStorage;

/**
 * 个人中心
 */
public class ProfileActivity extends AppCompatActivity {

    private ImageView ivAvatar, ivBack;
    private TextView tvNickname, tvPhone, tvLogout;
    private ProfileStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storage = new ProfileStorage(this);
        ivBack = findViewById(R.id.iv_back);
        ivAvatar = findViewById(R.id.iv_avatar);
        tvNickname = findViewById(R.id.tv_nickname);
        tvPhone = findViewById(R.id.tv_phone);
        tvLogout = findViewById(R.id.tv_logout);

        Glide.with(this)
                .load(R.drawable.ic_avatar)
                .circleCrop()
                .into(ivAvatar);

        ivBack.setOnClickListener(v -> finish());

        // 点击用户卡 -> 编辑资料
        findViewById(R.id.ll_user_card).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        // 收货地址
        findViewById(R.id.ll_address).setOnClickListener(v ->
                startActivity(new Intent(this, AddressListActivity.class)));

        // 我的订单
        findViewById(R.id.ll_order).setOnClickListener(v ->
                Toast.makeText(this, "订单功能开发中，敬请期待", Toast.LENGTH_SHORT).show());

        // 意见反馈
        findViewById(R.id.ll_feedback).setOnClickListener(v ->
                Toast.makeText(this, "感谢反馈，我们会持续优化", Toast.LENGTH_SHORT).show());

        // 关于我们
        findViewById(R.id.ll_about).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("关于我们")
                        .setMessage("仿美团外卖 Demo\n版本 1.0\n仅供学习使用")
                        .setPositiveButton("知道了", null)
                        .show());

        // 退出登录
        tvLogout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定要退出登录吗？将清空本地个人信息与地址。")
                        .setPositiveButton("退出", (DialogInterface dialog, int which) -> {
                            storage.clearAll();
                            Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
                            refreshUserInfo();
                        })
                        .setNegativeButton("取消", null)
                        .show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserInfo();
    }

    /** 刷新用户信息显示 */
    private void refreshUserInfo() {
        String nickname = storage.getNickname();
        String phone = storage.getPhone();
        tvNickname.setText(nickname.isEmpty() ? "未设置昵称" : nickname);
        tvPhone.setText(phone.isEmpty() ? "未绑定手机号" : "手机号：" + phone);
    }
}
