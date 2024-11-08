package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class dangNhap extends AppCompatActivity {

    private EditText txtHoTen, txtMatKhau;
    private ImageView imgAnHienMatKhau;
    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangnhap);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các view
        txtHoTen = findViewById(R.id.txtHoTen);
        txtMatKhau = findViewById(R.id.txtMK);
        imgAnHienMatKhau = findViewById(R.id.imgAnHienMatKhau);
        MaterialButton btnDangNhap = findViewById(R.id.btnDangNhap);

        // Thiết lập sự kiện nhấn cho ImageView để ẩn/hiện mật khẩu
        imgAnHienMatKhau.setOnClickListener(v -> {
            if (isPasswordVisible) {
                txtMatKhau.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgAnHienMatKhau.setImageResource(R.drawable.baseline_remove_red_eye_24);
            } else {
                txtMatKhau.setInputType(InputType.TYPE_CLASS_TEXT);
                imgAnHienMatKhau.setImageResource(R.drawable.baseline_visibility_off_24);
            }
            isPasswordVisible = !isPasswordVisible;
            txtMatKhau.setSelection(txtMatKhau.getText().length());
        });

        // Xử lý sự kiện nhấn nút "Đăng nhập"
        btnDangNhap.setOnClickListener(v -> {
            String inputUsername = txtHoTen.getText().toString();
            String inputPassword = txtMatKhau.getText().toString();

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(dangNhap.this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng nhập với Firebase
            mAuth.signInWithEmailAndPassword(inputUsername, inputPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            // Kiểm tra email đã xác thực chưa
                            if (currentUser != null && currentUser.isEmailVerified()) {
                                // Đăng nhập thành công và email đã xác thực
                                Toast.makeText(dangNhap.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                // Chuyển đến màn hình chính
                                Intent intent = new Intent(dangNhap.this, trangChu.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Nếu chưa xác thực email, yêu cầu người dùng xác thực
                                currentUser.sendEmailVerification()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(dangNhap.this, "Vui lòng xác thực email của bạn trước khi đăng nhập.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(dangNhap.this, "Có lỗi khi gửi email xác thực.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Thông báo lỗi đăng nhập
                            Toast.makeText(dangNhap.this, "Tên tài khoản hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Thiết lập sự kiện cho TextView "Quay lại"
        TextView txtQuayLai = findViewById(R.id.txtReturn);
        txtQuayLai.setOnClickListener(v -> {
            Intent intent = new Intent(dangNhap.this, Taikhoan.class);
            startActivity(intent);
            finish();
        });
    }
}
