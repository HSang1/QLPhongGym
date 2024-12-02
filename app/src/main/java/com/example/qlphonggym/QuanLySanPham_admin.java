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
import com.google.firebase.database.ChildEventListener;

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
        dbRefSanPham.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                SanPham sanPham = dataSnapshot.getValue(SanPham.class);
                if (sanPham != null) {
                    sanPhamList.add(sanPham);  // Thêm sản phẩm mới vào danh sách
                    sanPhamAdapter.notifyItemInserted(sanPhamList.size() - 1);  // Cập nhật RecyclerView
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                SanPham updatedSanPham = dataSnapshot.getValue(SanPham.class);
                if (updatedSanPham != null) {
                    for (int i = 0; i < sanPhamList.size(); i++) {
                        if (sanPhamList.get(i).getIdSanPham().equals(updatedSanPham.getIdSanPham())) {
                            sanPhamList.set(i, updatedSanPham);  // Cập nhật sản phẩm trong danh sách
                            sanPhamAdapter.notifyItemChanged(i);  // Cập nhật RecyclerView
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                SanPham removedSanPham = dataSnapshot.getValue(SanPham.class);
                if (removedSanPham != null) {
                    for (int i = 0; i < sanPhamList.size(); i++) {
                        if (sanPhamList.get(i).getIdSanPham().equals(removedSanPham.getIdSanPham())) {
                            sanPhamList.remove(i);  // Xóa sản phẩm khỏi danh sách
                            sanPhamAdapter.notifyItemRemoved(i);  // Cập nhật RecyclerView
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                // Không cần xử lý khi có sự thay đổi vị trí của các phần tử
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(QuanLySanPham_admin.this, "Lỗi khi lấy dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
