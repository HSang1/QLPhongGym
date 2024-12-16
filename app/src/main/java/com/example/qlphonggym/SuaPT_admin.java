package com.example.qlphonggym;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.PT;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SuaPT_admin extends AppCompatActivity {

    private EditText edtTenPT;
    private Button btnSave;
    private DatabaseReference ptRef;
    private String ptId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suapt_admin);

        // Khởi tạo các view
        edtTenPT = findViewById(R.id.edtTenPT);
        btnSave = findViewById(R.id.btnSave);

        // Khởi tạo Firebase Database reference
        ptRef = FirebaseDatabase.getInstance().getReference("PT");

        // Lấy ID của PT từ Intent
        ptId = getIntent().getStringExtra("PT_ID");

        // Kiểm tra xem có nhận được ID PT không
        if (ptId == null) {
            Toast.makeText(this, "Không tìm thấy PT", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tải thông tin PT từ Firebase (nếu có)
        ptRef.child(ptId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                PT pt = task.getResult().getValue(PT.class);
                if (pt != null) {
                    // Hiển thị tên PT hiện tại trong EditText
                    edtTenPT.setText(pt.getTenPT());
                } else {
                    Toast.makeText(SuaPT_admin.this, "Không tìm thấy PT", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SuaPT_admin.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện click nút Lưu
        btnSave.setOnClickListener(v -> {
            String newTenPT = edtTenPT.getText().toString().trim();
            if (newTenPT.isEmpty()) {
                edtTenPT.setError("Tên PT không được để trống");
                edtTenPT.requestFocus();
            } else {
                // Cập nhật tên PT mới vào Firebase
                PT updatedPT = new PT(ptId, newTenPT);
                ptRef.child(ptId).setValue(updatedPT).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SuaPT_admin.this, "Cập nhật PT thành công", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình trước
                    } else {
                        Toast.makeText(SuaPT_admin.this, "Lỗi cập nhật PT", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
