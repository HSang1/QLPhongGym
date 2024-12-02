package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class trangchu_admin extends AppCompatActivity {

    private TextView textViewThanhVien;
    private TextView textViewPT;
    private TextView textViewTheTap;
    private TextView textViewSanPham;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.trangchu_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView imageQuanLy = findViewById(R.id.imageQuanLy);
        ImageView imageHoSo = findViewById(R.id.imageHoSo);
        ImageView imageThongBao = findViewById(R.id.imageThongBao);

        textViewThanhVien = findViewById(R.id.textViewThanhVien);
        textViewPT = findViewById(R.id.textViewPT);
        textViewTheTap = findViewById(R.id.textViewTheTap);
        textViewSanPham = findViewById(R.id.textViewSanPham);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();


        database.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long userCount = dataSnapshot.getChildrenCount();
                textViewThanhVien.setText(String.valueOf(userCount));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.child("PT").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long userCount = dataSnapshot.getChildrenCount();
                textViewPT.setText(String.valueOf(userCount));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.child("TheTap").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long userCount = dataSnapshot.getChildrenCount();
                textViewTheTap.setText(String.valueOf(userCount));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.child("SanPham").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long userCount = dataSnapshot.getChildrenCount();
                textViewSanPham.setText(String.valueOf(userCount));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageQuanLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangchu_admin.this, chucnang_admin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_2_trai_qua_phai, R.anim.slide_1_trai_qua_phai);
            }
        });

        imageHoSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangchu_admin.this, hoSo_admin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_2_trai_qua_phai, R.anim.slide_1_trai_qua_phai);
            }
        });

        imageThongBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangchu_admin.this, thongbao_admin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_2_trai_qua_phai, R.anim.slide_1_trai_qua_phai);
            }
        });
    }
}