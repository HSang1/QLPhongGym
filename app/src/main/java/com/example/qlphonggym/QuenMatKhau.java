package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;

public class QuenMatKhau extends AppCompatActivity {

    private EditText edtEmail;
    private AppCompatButton btnResetMatKhau;
    TextView QuayVeTrangDangNhap;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quenmatkhau);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các view theo ID trong XML
        edtEmail = findViewById(R.id.edtEmail);
        btnResetMatKhau = findViewById(R.id.btnResetMatKhau);
        QuayVeTrangDangNhap = findViewById(R.id.QuayVeTrangDangNhap);


        btnResetMatKhau.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            // Kiểm tra nếu email rỗng
            if (email.isEmpty()) {
                Toast.makeText(QuenMatKhau.this, "Vui lòng nhập email của bạn.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi yêu cầu reset mật khẩu qua email
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Nếu gửi thành công
                            Toast.makeText(QuenMatKhau.this, "Đã gửi liên kết reset mật khẩu đến email của bạn.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Nếu có lỗi xảy ra khi gửi email
                            Toast.makeText(QuenMatKhau.this, "Có lỗi xảy ra. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        QuayVeTrangDangNhap.setOnClickListener(v -> {
            Intent intent = new Intent(QuenMatKhau.this, dangNhap.class);
            startActivity(intent);
            finish();
        });
    }
}
