package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.DatPTAdapter;
import com.example.qlphonggym.CSDL.DatPT;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuanLyDatPT_admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatPTAdapter datPTAdapter;
    private List<DatPT> datPTList;
    private DatabaseReference dbRefDatPT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quanlydatpt_admin);

        // Khởi tạo Firebase Database
        dbRefDatPT = FirebaseDatabase.getInstance().getReference("DatPT");

        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.danhSachDatPT);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        datPTList = new ArrayList<>();
        datPTAdapter = new DatPTAdapter(this, datPTList);  // Chuyển context vào đây
        recyclerView.setAdapter(datPTAdapter);

        // Lấy danh sách sản phẩm từ Firebase
        getDatPTFromFirebase();

        // Xử lý sự kiện Thêm Sản Phẩm
        Button btnThemDatPT = findViewById(R.id.btThemDatPT);
        btnThemDatPT.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyDatPT_admin.this, ThemDatPT_admin.class);
            startActivity(intent);
        });
    }

    // Phương thức lấy danh sách sản phẩm từ Firebase
    private void getDatPTFromFirebase() {
        dbRefDatPT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datPTList.clear();  // Xóa danh sách cũ

                // Duyệt qua tất cả các sản phẩm
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatPT datPT = snapshot.getValue(DatPT.class);
                    if (datPT != null) {
                        // Dùng danhMucId như tên danh mục và thêm sản phẩm vào danh sách
                        datPTList.add(datPT);
                    }
                }

                // Cập nhật RecyclerView
                datPTAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuanLyDatPT_admin.this, "Lỗi khi lấy dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}