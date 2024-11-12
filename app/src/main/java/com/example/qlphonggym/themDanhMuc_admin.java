package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.DanhMuc;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class themDanhMuc_admin extends AppCompatActivity {

    private EditText edtTenDanhMuc;
    private Button btnSaveDanhMuc;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themdanhmuc_admin);

        edtTenDanhMuc = findViewById(R.id.TenDanhMuc);
        btnSaveDanhMuc = findViewById(R.id.btThemDanhMuc);

        mDatabase = FirebaseDatabase.getInstance().getReference("DanhMuc");

        // Lắng nghe sự kiện khi nhấn nút lưu
        btnSaveDanhMuc.setOnClickListener(v -> {
            String tenDanhMuc = edtTenDanhMuc.getText().toString().trim();

            if (!tenDanhMuc.isEmpty()) {
                // Tạo ID mới bằng cách sử dụng Firebase push
                String newId = mDatabase.push().getKey();
                if (newId != null) {
                    // Tạo đối tượng DanhMuc
                    DanhMuc danhMuc = new DanhMuc(newId, tenDanhMuc);

                    // Lưu vào Firebase
                    mDatabase.child(newId).setValue(danhMuc)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(themDanhMuc_admin.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                                finish(); // Trở về màn hình trước
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(themDanhMuc_admin.this, "Lỗi khi thêm danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(themDanhMuc_admin.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
