package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class dangNhap extends AppCompatActivity {

    private EditText txtHoTen, txtMatKhau;
    private ImageView imgAnHienMatKhau;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangnhap);

        txtHoTen = findViewById(R.id.txtHoTen);
        txtMatKhau = findViewById(R.id.txtMK);
        imgAnHienMatKhau = findViewById(R.id.imgAnHienMatKhau);
        MaterialButton btnDangNhap = findViewById(R.id.btnDangNhap);

        // Thiết lập sự kiện nhấn cho ImageView để ẩn/hiện mật khẩu
        imgAnHienMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    txtMatKhau.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgAnHienMatKhau.setImageResource(R.drawable.baseline_remove_red_eye_24);
                } else {
                    txtMatKhau.setInputType(InputType.TYPE_CLASS_TEXT);
                    imgAnHienMatKhau.setImageResource(R.drawable.baseline_visibility_off_24);
                }
                isPasswordVisible = !isPasswordVisible;
                txtMatKhau.setSelection(txtMatKhau.getText().length());
            }
        });

        // Xử lý sự kiện nhấn nút "Đăng nhập"
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUsername = txtHoTen.getText().toString();
                String inputPassword = txtMatKhau.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String storedUsername = sharedPreferences.getString("USERNAME", null);
                String storedPassword = sharedPreferences.getString("PASSWORD", null); // Đảm bảo mật khẩu đã lưu ở `dangKy`

                if (inputUsername.equals(storedUsername) && inputPassword.equals(storedPassword)) {
                    // Đăng nhập thành công
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("IS_REGISTERED", true);
                    editor.apply();

                    // Chuyển đến trang chủ
                    Intent intent = new Intent(dangNhap.this, trangChu.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Thông báo lỗi đăng nhập
                    Toast.makeText(dangNhap.this, "Tên tài khoản hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thiết lập sự kiện cho TextView "Quay lại"
        TextView txtQuayLai = findViewById(R.id.txtReturn);
        txtQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dangNhap.this, Taikhoan.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
