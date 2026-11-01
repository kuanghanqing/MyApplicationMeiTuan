package com.example.myapplicationmeituan.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplicationmeituan.R;
import com.example.myapplicationmeituan.storage.ProfileStorage;

/**
 * 编辑个人信息：昵称 / 手机号 / 性别
 */
public class     EditProfileActivity extends AppCompatActivity {

    private ImageView ivBack, ivAvatar;
    private EditText etNickname, etPhone;
    private RadioGroup rgGender;
    private Button btnSave;
    private ProfileStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        storage = new ProfileStorage(this);
        ivBack = findViewById(R.id.iv_back);
        ivAvatar = findViewById(R.id.iv_avatar);
        etNickname = findViewById(R.id.et_nickname);
        etPhone = findViewById(R.id.et_phone);
        rgGender = findViewById(R.id.rg_gender);
        btnSave = findViewById(R.id.btn_save);

        Glide.with(this)
                .load(R.drawable.ic_avatar)
                .circleCrop()
                .into(ivAvatar);

        ivBack.setOnClickListener(v -> finish());

        // 预填已有数据
        etNickname.setText(storage.getNickname());
        etPhone.setText(storage.getPhone());
        String gender = storage.getGender();
        switch (gender) {
            case "男": rgGender.check(R.id.rb_male); break;
            case "女": rgGender.check(R.id.rb_female); break;
            default: rgGender.check(R.id.rb_secret); break;
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        String nickname = etNickname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (nickname.isEmpty()) {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!phone.isEmpty() && !phone.matches("1\\d{10}")) {
            Toast.makeText(this, "请输入正确的11位手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        storage.saveNickname(nickname);
        storage.savePhone(phone);
        int checked = rgGender.getCheckedRadioButtonId();
        String gender;
        if (checked == R.id.rb_male) {
            gender = "男";
        } else if (checked == R.id.rb_female) {
            gender = "女";
        } else {
            gender = "保密";
        }
        storage.saveGender(gender);

        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
