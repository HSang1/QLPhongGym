package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.SanPhamAdapter;
import com.example.qlphonggym.CSDL.SanPham;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuanLySanPham_admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SanPhamAdapter sanPhamAdapter;
    private List<SanPham> sanPhamList;
    private DatabaseReference dbRefSanPham;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quanlysanpham_admin);

        // Khởi tạo Firebase Database
        dbRefSanPham = FirebaseDatabase.getInstance().getReference("SanPham");

        // Khởi tạo RecyclerView và Adapter
        recyclerView = findViewById(R.id.danhSachSanPham);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sanPhamList = new ArrayList<>();
        sanPhamAdapter = new SanPhamAdapter(this, sanPhamList);  // Chuyển context vào đây
        recyclerView.setAdapter(sanPhamAdapter);

        // Lấy danh sách sản phẩm từ Firebase
        getSanPhamFromFirebase();

        // Xử lý sự kiện Thêm Sản Phẩm
        Button btnThemSanPham = findViewById(R.id.btThemSanPham);
        btnThemSanPham.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLySanPham_admin.this, ThemSanPham_admin.class);
            startActivity(intent);
        });
    }

    // Phương thức lấy danh sách sản phẩm từ Firebase
    private void getSanPhamFromFirebase() {
        dbRefSanPham.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sanPhamList.clear();  // Xóa danh sách cũ

                // Duyệt qua tất cả các sản phẩm
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SanPham sanPham = snapshot.getValue(SanPham.class);
                    if (sanPham != null) {
                        // Dùng danhMucId như tên danh mục và thêm sản phẩm vào danh sách
                        sanPhamList.add(sanPham);
                    }
                }

                // Cập nhật RecyclerView
                sanPhamAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuanLySanPham_admin.this, "Lỗi khi lấy dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
