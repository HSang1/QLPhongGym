package com.example.qlphonggym;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//khai báo thêm dòng na
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Taikhoan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.taikhoan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // tạo Button Đăng Nhap và thiết lập sự kiện onClick
        Button btnDangNhap = findViewById(R.id.btDangNhapTuTrangChu);
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Taikhoan.this, dangNhap.class); // Chuyển sang màn hình Đăng Nhập
                startActivity(intent);
            }
        });

        //  tạo Button "Đăng ký" và thiết lập sự kiện onClick
        Button btnDangKy = findViewById(R.id.btDangKyTuTrangChu);
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Taikhoan.this, DangkysdtActivity.class); // Chuyển sang màn hình Đăng ký sdt
                startActivity(intent);
            }
        });

        // Khởi tạo TextView "Quay lại" và thiết lập sự kiện onClick
        TextView txtQuayLai = findViewById(R.id.txtQuayLaiTrangChu);
        txtQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Taikhoan.this, trangChu.class); // Chuyển sang màn hình Trang Chủ
                startActivity(intent);
                finish(); // Đóng Activity hiện tại để tránh quay lại màn hình này khi bấm nút Back
            }
        });
    }
}