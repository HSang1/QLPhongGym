package com.example.qlphonggym;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.LopHoc;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class themLopHoc_admin extends AppCompatActivity {

    private EditText edtTenLopHoc;
    private Button btnSaveLopHoc;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themlophoc_admin);

        edtTenLopHoc = findViewById(R.id.TenLopHoc);
        btnSaveLopHoc = findViewById(R.id.btThemLopHoc);

        mDatabase = FirebaseDatabase.getInstance().getReference("LopHoc");

        // Lắng nghe sự kiện khi nhấn nút lưu
        btnSaveLopHoc.setOnClickListener(v -> {
            String tenLopHoc = edtTenLopHoc.getText().toString().trim();

            if (!tenLopHoc.isEmpty()) {
                // Tạo ID mới bằng cách sử dụng Firebase push
                String newId = mDatabase.push().getKey();
                if (newId != null) {
                    // Tạo đối tượng DanhMuc
                    LopHoc lopHoc = new LopHoc(newId, tenLopHoc);

                    // Lưu vào Firebase
                    mDatabase.child(newId).setValue(lopHoc)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(themLopHoc_admin.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                                finish(); // Trở về màn hình trước
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(themLopHoc_admin.this, "Lỗi khi thêm danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(themLopHoc_admin.this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }
}