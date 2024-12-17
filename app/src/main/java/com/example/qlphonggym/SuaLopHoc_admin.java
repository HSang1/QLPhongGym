package com.example.qlphonggym;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.LopHoc;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SuaLopHoc_admin extends AppCompatActivity {

    private EditText edtTenLopHoc;
    private Button btnSaveLopHoc;
    private DatabaseReference mDatabase;
    private String lopHocId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sualophoc_admin);

        edtTenLopHoc = findViewById(R.id.edtTenLopHoc);
        btnSaveLopHoc = findViewById(R.id.btnSaveLopHoc);

        mDatabase = FirebaseDatabase.getInstance().getReference("DanhMuc");

        // Lấy danh mục từ Intent
        if (getIntent() != null) {
            lopHocId = getIntent().getStringExtra("LOPHOC_ID");

            // Lấy thông tin danh mục từ Firebase
            mDatabase.child(lopHocId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        LopHoc lopHoc = dataSnapshot.getValue(LopHoc.class);
                        if (lopHoc != null) {
                            edtTenLopHoc.setText(lopHoc.getTenLopHoc());
                        }
                    } else {
                        Toast.makeText(SuaLopHoc_admin.this, "Lớp học không tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(SuaLopHoc_admin.this, "Lỗi khi tải dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Xử lý sự kiện khi nhấn nút Lưu
        btnSaveLopHoc.setOnClickListener(v -> {
            String tenLopHoc = edtTenLopHoc.getText().toString().trim();

            if (!tenLopHoc.isEmpty()) {
                // Cập nhật thông tin danh mục vào Firebase
                LopHoc lopHoc = new LopHoc(lopHocId, tenLopHoc);
                mDatabase.child(lopHocId).setValue(lopHoc)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(SuaLopHoc_admin.this, "Cập nhật lớp học thành công!", Toast.LENGTH_SHORT).show();
                            finish(); // Trở về màn hình trước
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SuaLopHoc_admin.this, "Lỗi khi cập nhật danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(SuaLopHoc_admin.this, "Vui lòng nhập tên lớp học", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
