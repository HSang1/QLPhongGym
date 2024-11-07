package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class hoSo_admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.hoso_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView txtQuayLai = findViewById(R.id.txtQuayLaiTrangChu);
        txtQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(hoSo_admin.this, chucnang_admin.class); // Chuyển sang màn hình Trang Chủ
                startActivity(intent);
                finish();
            }
        });


        ImageView imageQuanLy = findViewById(R.id.imageQuanLy);
        ImageView imageTrangChu = findViewById(R.id.imageTrangChu);
        ImageView imageThongBao = findViewById(R.id.imageThongBao);

        imageQuanLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(hoSo_admin.this, chucnang_admin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_1_phai_qua_trai, R.anim.slide_2_phai_qua_trai);

            }
        });

        imageTrangChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(hoSo_admin.this, trangchu_admin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_1_phai_qua_trai, R.anim.slide_2_phai_qua_trai);

            }
        });

        imageThongBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(hoSo_admin.this, thongbao_admin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_1_phai_qua_trai, R.anim.slide_2_phai_qua_trai);

            }
        });

        Button btDangXuatAdmin = findViewById(R.id.btDangXuatAdmin);
        btDangXuatAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(hoSo_admin.this, trangChu.class);
                startActivity(intent);
            }
        });

    }


}