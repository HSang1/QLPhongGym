package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dangNhap extends AppCompatActivity {

    private EditText txtHoTen, txtMatKhau;
    private ImageView imgAnHienMatKhau;
    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;  // Firebase Realtime Database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangnhap);

        // Khởi tạo FirebaseAuth và Firebase Realtime Database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("users"); // Trỏ đến nhánh "users" trong Realtime Database

        // Ánh xạ các view
        txtHoTen = findViewById(R.id.txtHoTen);
        txtMatKhau = findViewById(R.id.txtMatKhau);
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
            String inputUsername = txtHoTen.getText().toString().trim().toLowerCase();
            String inputPassword = txtMatKhau.getText().toString();

            // Kiểm tra nếu các trường nhập liệu rỗng
            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(dangNhap.this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tìm kiếm email tương ứng với username trong Realtime Database
            userRef.orderByChild("username").equalTo(inputUsername)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Lấy email từ dữ liệu người dùng
                                String email = null;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    email = snapshot.child("email").getValue(String.class);
                                    Log.d("FirebaseQuery", "Found email: " + email);
                                    break;  // Chỉ cần lấy email đầu tiên tìm thấy
                                }

                                if (email != null) {
                                    // Đăng nhập với Firebase bằng email và mật khẩu
                                    mAuth.signInWithEmailAndPassword(email, inputPassword)
                                            .addOnCompleteListener(dangNhap.this, task -> {
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
                                                                        Toast.makeText(dangNhap.this, "Vui lòng kiểm tra email!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    // Thông báo lỗi đăng nhập
                                                    Log.e("LoginError", "Lỗi đăng nhập: " + task.getException().getMessage());
                                                    Toast.makeText(dangNhap.this, "Tên tài khoản hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(dangNhap.this, "Không tìm thấy email tương ứng với tài khoản này.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(dangNhap.this, "Tên tài khoản không tồn tại.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(dangNhap.this, "Lỗi kết nối, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
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
