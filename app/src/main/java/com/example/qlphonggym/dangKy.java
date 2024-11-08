package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class dangKy extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangky);

        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Khởi tạo Spinner cho Thành Phố
        Spinner spinnerCity = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vietnam_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        // Nhận số điện thoại từ DangkysdtActivity
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("PHONE_NUMBER");

        // Khởi tạo các thành phần trong form đăng ký
        Button btnDangKy = findViewById(R.id.btDangKy);
        EditText txtHoTen = findViewById(R.id.txtHoTen);
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtAddress = findViewById(R.id.txtDiaChi);
        EditText txtPassword = findViewById(R.id.txtMK); // Thêm trường nhập mật khẩu

        // Khởi tạo TextView "Quay lại" và thiết lập sự kiện onClick
        TextView txtQuayLai = findViewById(R.id.txtReturn);
        txtQuayLai.setOnClickListener(v -> {
            Intent backIntent = new Intent(dangKy.this, DangkysdtActivity.class);
            startActivity(backIntent);
            finish();
        });

        // Xử lý sự kiện khi nhấn nút Đăng Ký
        btnDangKy.setOnClickListener(v -> {
            String username = txtHoTen.getText().toString();
            String email = txtEmail.getText().toString();
            String address = txtAddress.getText().toString();
            String city = spinnerCity.getSelectedItem().toString(); // Lấy thành phố từ Spinner
            String password = txtPassword.getText().toString(); // Lấy mật khẩu từ trường nhập

            // Kiểm tra xem tất cả các trường có đầy đủ không
            if (username.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty()) {
                Toast.makeText(dangKy.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Tạo tài khoản Firebase bằng email và mật khẩu
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Đăng ký thành công, lưu thông tin vào Firestore
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    // Tạo một đối tượng người dùng
                                    User userInfo = new User(username, phoneNumber, email, address, city);

                                    // Lưu vào Firestore
                                    db.collection("users").document(userId)
                                            .set(userInfo)
                                            .addOnSuccessListener(aVoid -> {
                                                // Gửi email xác thực
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                Toast.makeText(dangKy.this, "Đăng ký thành công! Kiểm tra email để xác thực.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(dangKy.this, "Gửi email xác thực thất bại", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                // Chuyển sang trang chủ
                                                Intent intent1 = new Intent(dangKy.this, trangChu.class);
                                                startActivity(intent1);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(dangKy.this, "Lỗi khi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                Toast.makeText(dangKy.this, "Đăng ký thất bại. Kiểm tra lại thông tin", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    // Lớp User để lưu thông tin người dùng vào Firestore
    public static class User {
        String username;
        String phoneNumber;
        String email;
        String address;
        String city;

        public User(String username, String phoneNumber, String email, String address, String city) {
            this.username = username;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.address = address;
            this.city = city;
        }
    }
}
