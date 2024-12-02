package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class chucnang_admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.chucnang_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView imageHoSo = findViewById(R.id.imageHoSo);
        ImageView imageTrangChu = findViewById(R.id.imageTrangChu);
        ImageView imageThongBao = findViewById(R.id.imageThongBao);
        ImageView imageQuanLyUser = findViewById(R.id.quanly_user);
        ImageView imageQuanLyDanhMuc = findViewById(R.id.imageQuanLyDanhMuc);
        ImageView imageQuanLySanPham = findViewById(R.id.imageQuanLySanPham);
        ImageView imageQuanLyLopHoc = findViewById(R.id.imageQuanLyLopHoc);
        ImageView imageQuanLyDatLop = findViewById(R.id.imageQuanLyDatLop);
        ImageView imageQuanLyPT = findViewById(R.id.imageQuanLyPT);
        ImageView imageQuanLyDatPT = findViewById(R.id.imageQuanLyDatPT);
        ImageView imageQuanLyDonHang = findViewById(R.id.quanLyDonHang);

        imageHoSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, hoSo_admin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_2_trai_qua_phai, R.anim.slide_1_trai_qua_phai);
            }
        });

        imageTrangChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, trangchu_admin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_1_phai_qua_trai, R.anim.slide_2_phai_qua_trai);
            }
        });


        imageThongBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, thongbao_admin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_2_trai_qua_phai, R.anim.slide_1_trai_qua_phai);
            }
        });



        imageQuanLyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, QuanLyUser_Admin.class);
                startActivity(intent);
                finish();
            }
        });

        imageQuanLyDanhMuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, QuanLyDanhMuc_Admin.class);
                startActivity(intent);
                finish();
            }
        });

        imageQuanLySanPham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, QuanLySanPham_admin.class);
                startActivity(intent);
                finish();
            }
        });

        imageQuanLyLopHoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, QuanLyLopHoc_Admin.class);
                startActivity(intent);
                finish();
            }
        });

        imageQuanLyDatLop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, QuanLyDatLop_admin.class);
                startActivity(intent);
                finish();
            }
        });

        imageQuanLyPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, QuanLyPT_Admin.class);
                startActivity(intent);
                finish();
            }
        });

        imageQuanLyDatPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, QuanLyDatPT_admin.class);
                startActivity(intent);
                finish();
            }
        });
        imageQuanLyDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chucnang_admin.this, ThongKe.class);
                startActivity(intent);
                finish();
            }
        });
    }
}