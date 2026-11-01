package com.example.myapplicationmeituan.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationmeituan.R;
import com.example.myapplicationmeituan.bean.Address;
import com.example.myapplicationmeituan.storage.ProfileStorage;

import java.util.List;

/**
 * 新增 / 编辑收货地址
 */
public class   AddressEditActivity extends AppCompatActivity {

    private static final String[] TAGS = {"家", "公司", "学校"};

    private ImageView ivBack;
    private TextView tvTitle;
    private EditText etName, etPhone, etDetail;
    private LinearLayout llDefault;
    private TextView tvDefaultCheck;
    private Button btnSave;

    private ProfileStorage storage;
    private Address editingAddress;      // null 表示新增
    private String selectedTag = "家";
    private boolean isDefault = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        storage = new ProfileStorage(this);
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etDetail = findViewById(R.id.et_detail);
        llDefault = findViewById(R.id.ll_default);
        tvDefaultCheck = findViewById(R.id.tv_default_check);
        btnSave = findViewById(R.id.btn_save);

        ivBack.setOnClickListener(v -> finish());

        // 编辑模式：接收已存在的地址
        Object obj = getIntent().getSerializableExtra("address");
        if (obj instanceof Address) {
            editingAddress = (Address) obj;
            tvTitle.setText("编辑收货地址");
            etName.setText(editingAddress.getName());
            etPhone.setText(editingAddress.getPhone());
            etDetail.setText(editingAddress.getDetail());
            selectedTag = editingAddress.getTag();
            isDefault = editingAddress.isDefault();
        }

        // 标签选择
        final TextView[] tagViews = {
                findViewById(R.id.tv_tag_home),
                findViewById(R.id.tv_tag_company),
                findViewById(R.id.tv_tag_school)
        };
        for (int i = 0; i < TAGS.length; i++) {
            final int index = i;
            tagViews[i].setOnClickListener(v -> {
                selectedTag = TAGS[index];
                for (int j = 0; j < tagViews.length; j++) {
                    tagViews[j].setBackgroundResource(
                            j == index ? R.drawable.bg_tag_selected : R.drawable.bg_tag);
                }
            });
        }
        for (int i = 0; i < TAGS.length; i++) {
            if (TAGS[i].equals(selectedTag)) {
                tagViews[i].setBackgroundResource(R.drawable.bg_tag_selected);
            }
        }

        // 设为默认开关
        llDefault.setOnClickListener(v -> {
            isDefault = !isDefault;
            tvDefaultCheck.setText(isDefault ? "✔" : "○");
            tvDefaultCheck.setTextColor(isDefault ? 0xFFFF6600 : 0xFFCCCCCC);
        });
        tvDefaultCheck.setText(isDefault ? "✔" : "○");
        tvDefaultCheck.setTextColor(isDefault ? 0xFFFF6600 : 0xFFCCCCCC);

        btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "请输入收货人姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.matches("1\\d{10}")) {
            Toast.makeText(this, "请输入正确的11位手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (detail.isEmpty()) {
            Toast.makeText(this, "请输入详细地址", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Address> list = storage.loadAddresses();

        if (editingAddress == null) {
            // 新增
            Address address = new Address(storage.nextAddressId(), name, phone, detail, selectedTag, isDefault);
            list.add(address);
        } else {
            // 编辑
            for (Address a : list) {
                if (a.getId() == editingAddress.getId()) {
                    a.setName(name);
                    a.setPhone(phone);
                    a.setDetail(detail);
                    a.setTag(selectedTag);
                    a.setDefault(isDefault);
                }
            }
        }

        // 同一时间只有一个默认地址
        if (isDefault) {
            for (Address a : list) {
                if (editingAddress == null || a.getId() != editingAddress.getId()) {
                    a.setDefault(false);
                }
            }
        }

        storage.saveAddresses(list);
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
