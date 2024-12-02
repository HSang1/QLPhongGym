package com.example.qlphonggym;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.PT;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class themPT_admin extends AppCompatActivity {

    private EditText edtTenPT;
    private Button btnSavePT;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thempt_admin);

        edtTenPT = findViewById(R.id.TenPT);
        btnSavePT = findViewById(R.id.btThemPT);

        mDatabase = FirebaseDatabase.getInstance().getReference("PT");

        // Lắng nghe sự kiện khi nhấn nút lưu
        btnSavePT.setOnClickListener(v -> {
            String tenPT = edtTenPT.getText().toString().trim();

            if (!tenPT.isEmpty()) {
                // Tạo ID mới bằng cách sử dụng Firebase push
                String newId = mDatabase.push().getKey();
                if (newId != null) {
                    // Tạo đối tượng DanhMuc
                    PT pT = new PT(newId, tenPT);

                    // Lưu vào Firebase
                    mDatabase.child(newId).setValue(pT)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(themPT_admin.this, "Thêm PT thành công!", Toast.LENGTH_SHORT).show();
                                finish(); // Trở về màn hình trước
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(themPT_admin.this, "Lỗi khi thêm PT: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(themPT_admin.this, "Vui lòng nhập tên PT", Toast.LENGTH_SHORT).show();
            }
        });
    }
}