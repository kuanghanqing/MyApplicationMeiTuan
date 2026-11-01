package com.example.myapplicationmeituan.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationmeituan.R;
import com.example.myapplicationmeituan.bean.Address;
import com.example.myapplicationmeituan.storage.ProfileStorage;

import java.util.List;

/**
 * 收货地址管理：列表 / 新增 / 编辑 / 删除 / 设默认
 */
public class AddressListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private LinearLayout llAddressContainer;
    private TextView tvEmpty;
    private ProfileStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        storage = new ProfileStorage(this);
        ivBack = findViewById(R.id.iv_back);
        llAddressContainer = findViewById(R.id.ll_address_container);
        tvEmpty = findViewById(R.id.tv_empty);

        ivBack.setOnClickListener(v -> finish());

        findViewById(R.id.btn_add).setOnClickListener(v ->
                startActivity(new Intent(this, AddressEditActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        llAddressContainer.removeAllViews();
        final List<Address> list = storage.loadAddresses();
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);

        for (Address address : list) {
            View item = LayoutInflater.from(this).inflate(R.layout.item_address, llAddressContainer, false);
            TextView tvName = item.findViewById(R.id.tv_address_name);
            TextView tvPhone = item.findViewById(R.id.tv_address_phone);
            TextView tvTag = item.findViewById(R.id.tv_address_tag);
            TextView tvDetail = item.findViewById(R.id.tv_address_detail);
            TextView tvDefault = item.findViewById(R.id.tv_address_default);
            TextView tvEdit = item.findViewById(R.id.tv_address_edit);
            TextView tvDelete = item.findViewById(R.id.tv_address_delete);

            tvName.setText(address.getName());
            tvPhone.setText(maskPhone(address.getPhone()));
            tvTag.setText(address.getTag());
            tvDetail.setText(address.getDetail());
            tvDefault.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

            // 点击卡片主体：设为默认
            item.setOnClickListener(v -> {
                if (!address.isDefault()) {
                    for (Address a : list) {
                        a.setDefault(a.getId() == address.getId());
                    }
                    storage.saveAddresses(list);
                    Toast.makeText(this, "已设为默认地址", Toast.LENGTH_SHORT).show();
                    refreshList();
                }
            });

            // 编辑
            tvEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddressEditActivity.class);
                intent.putExtra("address", address);
                startActivity(intent);
            });

            // 删除
            tvDelete.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("删除地址")
                            .setMessage("确定删除该收货地址吗？")
                            .setPositiveButton("删除", (DialogInterface dialog, int which) -> {
                                list.remove(address);
                                storage.saveAddresses(list);
                                Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
                                refreshList();
                            })
                            .setNegativeButton("取消", null)
                            .show());

            llAddressContainer.addView(item);
        }
    }

    /** 手机号脱敏：138****8888 */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone == null ? "" : phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
