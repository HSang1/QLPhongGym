package com.example.qlphonggym;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.CSDL_Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ThemUser_Admin extends AppCompatActivity {

    private EditText edtTenNguoiDung, edtTaiKhoan, edtMatKhau, edtSDT, edtDiaChi, edtEmail;
    private RadioGroup radioGroupQuyen;
    private RadioButton radioUser, radioAdmin;
    private Button btnDangKy;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themuser_admin);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users"); // Tạo reference đến node "Users" trong Firebase

        // Kết nối với các view
        edtTenNguoiDung = findViewById(R.id.edtTenNguoiDung);
        edtTaiKhoan = findViewById(R.id.edtTaiKhoan);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtSDT = findViewById(R.id.edtSDT);
        edtDiaChi = findViewById(R.id.edtDiaChi);
        edtEmail = findViewById(R.id.edtEmail);
        radioGroupQuyen = findViewById(R.id.radioGroupQuyen);
        radioUser = findViewById(R.id.radioUser);
        radioAdmin = findViewById(R.id.radioAdmin);
        btnDangKy = findViewById(R.id.btnDangKy);

        // Xử lý sự kiện khi người dùng click vào nút đăng ký
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các EditText
                String fullName = edtTenNguoiDung.getText().toString().trim();
                String username = edtTaiKhoan.getText().toString().trim();
                String password = edtMatKhau.getText().toString().trim();
                String phoneNumber = edtSDT.getText().toString().trim();
                String address = edtDiaChi.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();

                // Kiểm tra quyền
                String role = "User";  // Mặc định là "User"
                if (radioAdmin.isChecked()) {
                    role = "Admin"; // Nếu chọn Admin thì gán quyền là "Admin"
                }

                // Kiểm tra các trường hợp đầu vào
                if (fullName.isEmpty() || username.isEmpty() || phoneNumber.isEmpty() || address.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ThemUser_Admin.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo tài khoản Firebase với email và mật khẩu
                String finalRole = role;
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(ThemUser_Admin.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    // Gửi email xác thực
                                    firebaseUser.sendEmailVerification()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(ThemUser_Admin.this, "Đã gửi email xác thực, vui lòng kiểm tra email của bạn.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ThemUser_Admin.this, "Gửi email xác thực thất bại!", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    // Tạo đối tượng CSDL_Users
                                    CSDL_Users newUser = new CSDL_Users(username, phoneNumber, email, address, "", fullName, finalRole);

                                    // Lưu dữ liệu vào Firebase Realtime Database
                                    myRef.child(firebaseUser.getUid()).setValue(newUser)
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    Toast.makeText(ThemUser_Admin.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ThemUser_Admin.this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(ThemUser_Admin.this, "Đăng ký thất bại. Kiểm tra lại thông tin.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
