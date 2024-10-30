package com.example.qlphonggym;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//khai bao them dong nay
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class trangChu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.trangchu);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo TextView và thiết lập sự kiện onClick
        TextView txtTaiKhoan = findViewById(R.id.txtTaiKhoan);
        txtTaiKhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangChu.this, Taikhoan.class); // Chuyển sang màn hình Tài khoản
                startActivity(intent);
            }
        });

        //  tạo Button "Đặt lớp" và thiết lập sự kiện onClick
        Button btnDatLop = findViewById(R.id.btDatLop);
        btnDatLop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangChu.this, datLop.class); // Chuyển sang màn hình Đăng ký sdt
                startActivity(intent);
            }
        });
    }


}