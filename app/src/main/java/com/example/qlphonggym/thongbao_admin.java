package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.NhanXetAdapter;
import com.example.qlphonggym.CSDL.CSDL_NhanXet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class thongbao_admin extends AppCompatActivity {

    private RecyclerView recyclerViewNhanXet;
    private NhanXetAdapter nhanXetAdapter;
    private List<CSDL_NhanXet> nhanXetList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongbao_admin);

        recyclerViewNhanXet = findViewById(R.id.recyclerViewNhanXet);
        recyclerViewNhanXet.setLayoutManager(new LinearLayoutManager(this));

        nhanXetList = new ArrayList<>();
        nhanXetAdapter = new NhanXetAdapter(nhanXetList);
        recyclerViewNhanXet.setAdapter(nhanXetAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("NhanXet");


        loadNhanXetData();


        ImageView imageQuanLy = findViewById(R.id.imageQuanLy);
        ImageView imageTrangChu = findViewById(R.id.imageTrangChu);
        ImageView imageHoSo = findViewById(R.id.imageHoSo);

        imageQuanLy.setOnClickListener(v -> {
            Intent intent = new Intent(thongbao_admin.this, chucnang_admin.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_1_phai_qua_trai, R.anim.slide_2_phai_qua_trai);
        });

        imageTrangChu.setOnClickListener(v -> {
            Intent intent = new Intent(thongbao_admin.this, trangchu_admin.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_1_phai_qua_trai, R.anim.slide_2_phai_qua_trai);
        });

        imageHoSo.setOnClickListener(v -> {
            Intent intent = new Intent(thongbao_admin.this, hoSo_admin.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_2_trai_qua_phai, R.anim.slide_1_trai_qua_phai);
        });
    }

    private void loadNhanXetData() {
        mDatabase.orderByChild("ngayGioGopY").limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        nhanXetList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CSDL_NhanXet nhanXet = snapshot.getValue(CSDL_NhanXet.class);
                            nhanXetList.add(nhanXet);
                        }
                        // Đảo ngược danh sách để nhận xét mới nhất xuất hiện ở đầu
                        Collections.reverse(nhanXetList);
                        nhanXetAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(thongbao_admin.this, "Lỗi khi tải nhận xét", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
