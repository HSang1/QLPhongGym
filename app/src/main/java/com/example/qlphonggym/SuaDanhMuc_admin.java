package com.example.qlphonggym;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.DanhMuc;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SuaDanhMuc_admin extends AppCompatActivity {

    private EditText edtTenDanhMuc;
    private Button btnSaveDanhMuc;
    private DatabaseReference mDatabase;
    private String danhMucId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suadanhmuc_admin);

        edtTenDanhMuc = findViewById(R.id.edtTenDanhMuc);
        btnSaveDanhMuc = findViewById(R.id.btnSaveDanhMuc);

        mDatabase = FirebaseDatabase.getInstance().getReference("DanhMuc");

        // Lấy danh mục từ Intent
        if (getIntent() != null) {
            danhMucId = getIntent().getStringExtra("DANHMUC_ID");

            // Lấy thông tin danh mục từ Firebase
            mDatabase.child(danhMucId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DanhMuc danhMuc = dataSnapshot.getValue(DanhMuc.class);
                        if (danhMuc != null) {
                            edtTenDanhMuc.setText(danhMuc.getTenDanhMuc());
                        }
                    } else {
                        Toast.makeText(SuaDanhMuc_admin.this, "Danh mục không tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(SuaDanhMuc_admin.this, "Lỗi khi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Xử lý sự kiện khi nhấn nút Lưu
        btnSaveDanhMuc.setOnClickListener(v -> {
            String tenDanhMuc = edtTenDanhMuc.getText().toString().trim();

            if (!tenDanhMuc.isEmpty()) {
                // Cập nhật thông tin danh mục vào Firebase
                DanhMuc danhMuc = new DanhMuc(danhMucId, tenDanhMuc);
                mDatabase.child(danhMucId).setValue(danhMuc)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SuaDanhMuc_admin.this, "Cập nhật danh mục thành công!", Toast.LENGTH_SHORT).show();
                            finish(); // Trở về màn hình trước
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SuaDanhMuc_admin.this, "Lỗi khi cập nhật danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(SuaDanhMuc_admin.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
