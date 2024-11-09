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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.qlphonggym.CSDL.CSDL_Users; // Import lớp CSDL_Users

public class dangKy extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database; // Firebase Realtime Database
    private DatabaseReference usersRef; // Tham chiếu đến Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangky);

        // Khởi tạo Firebase Auth và Realtime Database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users"); // Tham chiếu đến node "users" trong Realtime Database

        // Khởi tạo Spinner cho Thành Phố
        Spinner spinnerCity = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vietnam_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        // Nhận số điện thoại từ DangkysdtActivity
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("PHONE_NUMBER"); // Nhận số điện thoại từ Intent

        // Khởi tạo các thành phần trong form đăng ký
        Button btnDangKy = findViewById(R.id.btDangKy);
        EditText txtHoTen = findViewById(R.id.txtHoVaTen); // Lấy tên người dùng
        EditText txtTaiKhoan = findViewById(R.id.txtTaiKhoan);
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
            String fullname = txtHoTen.getText().toString(); // Lấy tên người dùng
            String username = txtTaiKhoan.getText().toString();
            String email = txtEmail.getText().toString();
            String address = txtAddress.getText().toString();
            String city = spinnerCity.getSelectedItem().toString(); // Lấy thành phố từ Spinner
            String password = txtPassword.getText().toString(); // Lấy mật khẩu từ trường nhập

            // Kiểm tra xem tất cả các trường có đầy đủ không
            if (username.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty() || fullname.isEmpty()) {
                Toast.makeText(dangKy.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Tạo tài khoản Firebase bằng email và mật khẩu
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Đăng ký thành công, lưu thông tin vào Realtime Database
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    // Tạo đối tượng CSDL_Users với số điện thoại và fullName
                                    CSDL_Users userInfo = new CSDL_Users(username, phoneNumber, email, address, city, fullname);

                                    // Lưu vào Realtime Database tại node "users/{userId}"
                                    usersRef.child(userId).setValue(userInfo)
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

                                                // Chuyển sang trang đăng nhập hoặc màn hình khác
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
}
