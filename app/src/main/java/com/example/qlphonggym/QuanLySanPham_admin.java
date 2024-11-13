package com.example.qlphonggym;

import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;



import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuanLySanPham_admin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.quanlysanpham_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button btnThemSanPham = findViewById(R.id.btThemSanPham);

        btnThemSanPham.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLySanPham_admin.this, ThemSanPham_admin.class);
            startActivity(intent);
        });
    }
}