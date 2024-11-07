package com.example.qlphonggym;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//Khai báo thêm dòng sau
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class giaoDienUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giaodienuser);

        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvCity = findViewById(R.id.tvCity);
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        // Lấy thông tin người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "Không có");
        String phone = sharedPreferences.getString("PHONE_NUMBER", "Không có");
        String email = sharedPreferences.getString("EMAIL", "Không có");
        String address = sharedPreferences.getString("ADDRESS", "Không có");
        String city = sharedPreferences.getString("CITY", "Không có");

        // Hiển thị thông tin người dùng
        tvUsername.setText("Tên đăng nhập: " + username);
        tvPhone.setText("Số điện thoại: " + phone);
        tvEmail.setText("Email: " + email);
        tvAddress.setText("Địa chỉ: " + address);
        tvCity.setText("Thành phố: " + city);

        // Xử lý sự kiện "Đăng Xuất"
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đánh dấu là chưa đăng nhập mà không xóa dữ liệu người dùng
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IS_REGISTERED", false); // Đánh dấu là chưa đăng nhập
                editor.putString("USERNAME", null); // Xóa tên người dùng
                editor.apply();

                // Chuyển về màn hình đăng nhập
                Intent intent = new Intent(giaoDienUser.this, trangChu.class);
                startActivity(intent);
                finish();
            }
        });

        // Xử lý sự kiện "Xóa Tài Khoản"
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa toàn bộ thông tin người dùng
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Xóa tất cả dữ liệu trong SharedPreferences
                editor.apply();

                // Thông báo cho người dùng
                Toast.makeText(giaoDienUser.this, "Tài khoản đã được xóa.", Toast.LENGTH_SHORT).show();

                // Chuyển về màn hình đăng ký hoặc trang chủ
                Intent intent = new Intent(giaoDienUser.this, trangChu.class);
                startActivity(intent);
                finish();
            }
        });
    }
}