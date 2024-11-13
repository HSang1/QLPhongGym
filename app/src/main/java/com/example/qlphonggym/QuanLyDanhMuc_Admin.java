package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.DanhMucAdapter;
import com.example.qlphonggym.CSDL.DanhMuc;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuanLyDanhMuc_Admin extends AppCompatActivity implements DanhMucAdapter.OnDanhMucChangeListener {

    private RecyclerView recyclerView;
    private DanhMucAdapter adapter;
    private List<DanhMuc> danhMucList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quanlydanhmuc_admin);

        // Khởi tạo RecyclerView và các thành phần
        recyclerView = findViewById(R.id.danhSachDanhMuc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Đảm bảo LayoutManager đã được set
        danhMucList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("DanhMuc");

        // Khởi tạo nút Thêm danh mục
        Button btnThemDanhMuc = findViewById(R.id.btThemDanhMuc);
        btnThemDanhMuc.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyDanhMuc_Admin.this, themDanhMuc_admin.class);
            startActivity(intent);
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                danhMucList.clear();

                // Kiểm tra dữ liệu có tồn tại hay không
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DanhMuc danhMuc = snapshot.getValue(DanhMuc.class);
                        if (danhMuc != null) {
                            danhMucList.add(danhMuc); // Thêm danh mục vào danh sách
                        }
                    }

                    // Nếu danh sách không trống, khởi tạo adapter
                    if (adapter == null) {
                        adapter = new DanhMucAdapter(QuanLyDanhMuc_Admin.this, danhMucList, QuanLyDanhMuc_Admin.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Nếu adapter đã tồn tại, chỉ cần cập nhật lại dữ liệu
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // Nếu không có dữ liệu, hiển thị thông báo
                    Toast.makeText(QuanLyDanhMuc_Admin.this, "Không có danh mục nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi Firebase nếu có
                Toast.makeText(QuanLyDanhMuc_Admin.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Implement callback khi danh mục thay đổi (xóa hoặc sửa)
    @Override
    public void onDanhMucChanged() {
        runOnUiThread(() -> {
            // Cập nhật lại dữ liệu trong Activity (QuanLyDanhMuc_Admin)
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
