package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.DatLopAdapter;
import com.example.qlphonggym.CSDL.DatLop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuanLyDatLop_admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatLopAdapter datLopAdapter;
    private List<DatLop> datLopList;
    private DatabaseReference dbRefDatLop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quanlydatlop_admin);

        // Khởi tạo Firebase Database
        dbRefDatLop = FirebaseDatabase.getInstance().getReference("DatLop");

        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.danhSachDatLop);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        datLopList = new ArrayList<>();
       datLopAdapter = new DatLopAdapter(this, datLopList);  // Chuyển context vào đây
        recyclerView.setAdapter(datLopAdapter);

        // Lấy danh sách sản phẩm từ Firebase
        getDatLopFromFirebase();

        // Xử lý sự kiện Thêm Sản Phẩm
        Button btnThemDatLop = findViewById(R.id.btThemDatLop);
        btnThemDatLop.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyDatLop_admin.this, ThemDatLop_admin.class);
            startActivity(intent);
        });
    }

    // Phương thức lấy danh sách sản phẩm từ Firebase
    private void getDatLopFromFirebase() {
        dbRefDatLop.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datLopList.clear();  // Xóa danh sách cũ

                // Duyệt qua tất cả các sản phẩm
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatLop datLop = snapshot.getValue(DatLop.class);
                    if (datLop != null) {
                        // Dùng danhMucId như tên danh mục và thêm sản phẩm vào danh sách
                        datLopList.add(datLop);
                    }
                }

                // Cập nhật RecyclerView
                datLopAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuanLyDatLop_admin.this, "Lỗi khi lấy dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
