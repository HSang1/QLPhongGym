package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.LopHocAdapter;
import com.example.qlphonggym.CSDL.LopHoc;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuanLyLopHoc_Admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LopHocAdapter adapter;
    private List<LopHoc> lopHocList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quanlylophoc_admin);

        // Khởi tạo RecyclerView và các thành phần
        recyclerView = findViewById(R.id.danhSachLopHoc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Đảm bảo LayoutManager đã được set
        lopHocList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("LopHoc");

        // Khởi tạo nút Thêm danh mục
        Button btnThemDanhMuc = findViewById(R.id.btThemLopHoc);
        btnThemDanhMuc.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyLopHoc_Admin.this, themLopHoc_admin.class);
            startActivity(intent);
        });

        // Lấy dữ liệu từ Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                lopHocList.clear();

                // Kiểm tra dữ liệu có tồn tại hay không
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LopHoc lopHoc = snapshot.getValue(LopHoc.class);
                        if (lopHoc != null) {
                            lopHocList.add(lopHoc); // Thêm danh mục vào danh sách
                        }
                    }

                    // Nếu danh sách không trống, khởi tạo adapter
                    if (adapter == null) {
                        adapter = new LopHocAdapter(QuanLyLopHoc_Admin.this, lopHocList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Nếu adapter đã tồn tại, chỉ cần cập nhật lại dữ liệu
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // Nếu không có dữ liệu, hiển thị thông báo
                    Toast.makeText(QuanLyLopHoc_Admin.this, "Không có danh mục nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi Firebase nếu có
                Toast.makeText(QuanLyLopHoc_Admin.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}