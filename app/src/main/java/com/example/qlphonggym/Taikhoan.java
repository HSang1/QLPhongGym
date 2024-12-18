package com.example.qlphonggym;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Taikhoan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.taikhoan);

        // Tự động điều chỉnh padding cho hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập các nút đăng nhập và đăng ký
        setupAuthButtons();

        // Sự kiện quay lại màn hình chính
        TextView txtQuayLai = findViewById(R.id.txtQuayLaiTrangChu);
        txtQuayLai.setOnClickListener(v -> {
            startActivity(new Intent(Taikhoan.this, trangChu.class));
            finish(); // Đóng màn hình hiện tại
        });

        // Khởi tạo thanh điều hướng bottomNavigation
        setupBottomNavigation();
    }

    private void setupAuthButtons() {
        // Nút Đăng Nhập
        Button btnDangNhap = findViewById(R.id.btDangNhapTuTrangChu);
        btnDangNhap.setOnClickListener(v -> {
            startActivity(new Intent(Taikhoan.this, dangNhap.class));
        });

        // Nút Đăng Ký
        Button btnDangKy = findViewById(R.id.btDangKyTuTrangChu);
        btnDangKy.setOnClickListener(v -> {
            startActivity(new Intent(Taikhoan.this, DangkysdtActivity.class));
        });
    }

    private void setupBottomNavigation() {
        // Nút "Trang Chủ"
        findViewById(R.id.homeButton).setOnClickListener(v -> {
            startActivity(new Intent(Taikhoan.this, trangChu.class));
            finish();
        });

        // Nút "Cửa Hàng"
        findViewById(R.id.storeButton).setOnClickListener(v -> {
            startActivity(new Intent(Taikhoan.this, CuaHang.class));
            finish();
        });

        // Nút "Nhận Xét"
        findViewById(R.id.notificationButton).setOnClickListener(v -> {
            startActivity(new Intent(Taikhoan.this, NhanXet.class));
            finish();
        });

        // Nút "Tài Khoản"
        findViewById(R.id.TaiKhoan).setOnClickListener(v -> {
            Toast.makeText(Taikhoan.this, "Bạn đang ở mục Tài khoản", Toast.LENGTH_SHORT).show();
        });
    }
}
