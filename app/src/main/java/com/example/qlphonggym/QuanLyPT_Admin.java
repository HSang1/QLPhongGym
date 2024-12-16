package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.PTAdapter;
import com.example.qlphonggym.CSDL.PT;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuanLyPT_Admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PTAdapter adapter;
    private List<PT> pTList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quanlypt_admin);

        // Khởi tạo RecyclerView và các thành phần
        recyclerView = findViewById(R.id.danhSachPT);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Đảm bảo LayoutManager đã được set
        pTList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("PT");

        // Khởi tạo nút Thêm PT
        Button btnThemPT = findViewById(R.id.btThemPT);
        btnThemPT.setOnClickListener(v -> {
            Intent intent = new Intent(QuanLyPT_Admin.this, themPT_admin.class);
            startActivity(intent);
        });

        // Lấy dữ liệu từ Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                pTList.clear();

                // Kiểm tra dữ liệu có tồn tại hay không
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PT pt = snapshot.getValue(PT.class);
                        if (pt != null) {
                            pTList.add(pt); // Thêm PT vào danh sách
                        }
                    }

                    // Nếu danh sách không trống, khởi tạo adapter
                    if (adapter == null) {
                        adapter = new PTAdapter(QuanLyPT_Admin.this, pTList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Nếu adapter đã tồn tại, chỉ cần cập nhật lại dữ liệu
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // Nếu không có dữ liệu, hiển thị thông báo
                    Toast.makeText(QuanLyPT_Admin.this, "Không có PT nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi Firebase nếu có
                Toast.makeText(QuanLyPT_Admin.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
