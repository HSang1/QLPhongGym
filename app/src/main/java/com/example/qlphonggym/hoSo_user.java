package com.example.qlphonggym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class hoSo_user extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Các TextView trong hoso_user.xml để hiển thị thông tin
    private TextView emailValueText, phoneValueText, addressValueText, fullnameValueText, usernameText;  // Cập nhật thêm fullnameValueText
    private TextView forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hoso_user);  // Đảm bảo layout đúng tên file

        // Khởi tạo FirebaseAuth và DatabaseReference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Ánh xạ các TextView
        emailValueText = findViewById(R.id.emailValueText);
        phoneValueText = findViewById(R.id.phoneValueText);
        addressValueText = findViewById(R.id.addressValueText);
        fullnameValueText = findViewById(R.id.textfullnameValueText);
        usernameText = findViewById(R.id.textusernameValueText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        LinearLayout homeButton = findViewById(R.id.homeButton);
        LinearLayout storeButton = findViewById(R.id.storeButton);
        LinearLayout notificationButton = findViewById(R.id.notificationButton);

        // Lấy thông tin người dùng đã đăng nhập
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Lấy user ID của người dùng
            String userId = currentUser.getUid();  // UID của người dùng đăng nhập
            loadUserProfile(userId);  // Tải thông tin người dùng
        } else {
            // Nếu không có người dùng đăng nhập, thông báo lỗi
            Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
        }


        // Lấy Button Đăng xuất và thiết lập sự kiện click cho Button Đăng xuất
        Button btnDangXuat = findViewById(R.id.btDangXuat);
        btnDangXuat.setOnClickListener(v -> {
            // 1. Đăng xuất khỏi Firebase
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();  // Đăng xuất người dùng khỏi Firebase

            // 2. Xóa dữ liệu lưu trữ người dùng trong SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();  // Xóa toàn bộ dữ liệu lưu trữ
            editor.apply();   // Lưu thay đổi

            // 3. Quay về trang chủ (MainActivity)
            Intent intent = new Intent(hoSo_user.this, trangChu.class);
            startActivity(intent);
            finish();  // Kết thúc Activity hoSo_user để không quay lại màn hình này nữa
        });

        Button btnSuaHoSo = findViewById(R.id.btSuaHoSo);
        btnSuaHoSo.setOnClickListener(v -> {
            Intent intent = new Intent(hoSo_user.this, SuaHoSo.class);
            startActivity(intent);
            finish();
        });

        // Sự kiện khi người dùng bấm vào "Quên mật khẩu"
        forgotPasswordText.setOnClickListener(v -> {
            String email = mAuth.getCurrentUser().getEmail();  // Lấy email của người dùng

            if (email != null) {
                mAuth.sendPasswordResetEmail(email)  // Gửi yêu cầu đổi mật khẩu qua email
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(hoSo_user.this, "Đã gửi email đổi mật khẩu đến gmail của bạn", Toast.LENGTH_SHORT).show();

                                // Đăng xuất người dùng
                                mAuth.signOut();

                                // Quay về trang đăng nhập
                                Intent intent = new Intent(hoSo_user.this, trangChu.class);
                                startActivity(intent);
                                finish();  // Kết thúc Activity hiện tại
                            } else {
                                Toast.makeText(hoSo_user.this, "Lỗi gửi email đổi mật khẩu.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(hoSo_user.this, "Không tìm thấy email của bạn.", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút Home: Chuyển về trang chủ
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(hoSo_user.this, trangChu.class);
            startActivity(intent);
            finish();
        });

        // Nút Store: Chuyển sang trang cửa hàng
        storeButton.setOnClickListener(v -> {
            Intent intent = new Intent(hoSo_user.this, CuaHang.class); // Tạo activity CuaHang
            startActivity(intent);
        });

        // Nút Thông báo: Chuyển sang trang thông báo
        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(hoSo_user.this, NhanXet.class); // Tạo activity ThongBao
            startActivity(intent);
        });

    }

    private void loadUserProfile(String userId) {
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String city = dataSnapshot.child("city").getValue(String.class);
                    String fullname = dataSnapshot.child("fullName").getValue(String.class);

                    // Hiển thị thông tin lên các TextView
                    usernameText.setText(username);
                    emailValueText.setText(email);
                    phoneValueText.setText(phoneNumber);
                    fullnameValueText.setText(fullname);  // Đảm bảo đặt đúng tên biến ở đây
                    addressValueText.setText(city);
                } else {
                    Toast.makeText(hoSo_user.this, "Không tìm thấy hồ sơ người dùng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Nếu có lỗi trong việc lấy dữ liệu từ Firebase
                Toast.makeText(hoSo_user.this, "Lỗi kết nối đến Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
