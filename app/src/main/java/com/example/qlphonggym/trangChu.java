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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class trangChu extends AppCompatActivity {

    private LinearLayout upcomingClassesSection;
    private LinearLayout taiKhoanSection;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private TextView txtHoVaTen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Lấy các thành phần giao diện
        txtHoVaTen = findViewById(R.id.txtHoVaTen);
        Button btnDatLop = findViewById(R.id.btDatLop);
        upcomingClassesSection = findViewById(R.id.upcomingClassesSection);
        taiKhoanSection = findViewById(R.id.TaiKhoan);

        // Đảm bảo người dùng luôn đăng xuất mỗi lần mở ứng dụng
       // mAuth.signOut();

        // Xử lý tấm nền (EdgeToEdge) để có giao diện đẹp trên các thiết bị có thanh trạng thái
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Kiểm tra trạng thái đăng ký của người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isRegistered = sharedPreferences.getBoolean("IS_REGISTERED", false);
        String username = sharedPreferences.getString("USERNAME", null);

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (isRegistered && username != null && !username.isEmpty()) {
            // Người dùng đã đăng nhập, lấy thông tin người dùng từ Firebase
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                usersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Kiểm tra xem dữ liệu có tồn tại
                        if (dataSnapshot.exists()) {
                            String fullName = dataSnapshot.child("fullName").getValue(String.class);
                            if (fullName != null && !fullName.isEmpty()) {
                                txtHoVaTen.setText("Xin chào, " + fullName);
                            } else {
                                txtHoVaTen.setText("Xin chào, " + username);
                            }
                        } else {
                            txtHoVaTen.setText("Thông tin người dùng không tồn tại.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(trangChu.this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            txtHoVaTen.setText("Đăng nhập");
        }

        // Hiển thị lớp học sắp diễn ra nếu đã đặt chỗ
        if (isRegistered) {
            displayUpcomingClass();
        }

        // Thiết lập sự kiện onClick cho Button "Đặt lớp"
        btnDatLop.setOnClickListener(v -> {
            Intent intent = new Intent(trangChu.this, datLop.class); // Chuyển sang màn hình Đặt lớp
            startActivity(intent);
        });

        // Thiết lập sự kiện click cho LinearLayout TaiKhoan
        taiKhoanSection.setOnClickListener(v -> {
            if (isRegistered) {
                Intent intent = new Intent(trangChu.this, hoSo_user.class); // Mở hồ sơ người dùng
                startActivity(intent);
            } else {
                Intent intent = new Intent(trangChu.this, Taikhoan.class); // Mở màn hình đăng nhập
                startActivity(intent);
            }
        });
    }

    private void displayUpcomingClass() {
        SharedPreferences classBookingPrefs = getSharedPreferences("ClassBooking", MODE_PRIVATE);
        boolean isBooked = classBookingPrefs.getBoolean("isBooked", false);

        if (isBooked) {
            // Lấy thông tin lớp học từ SharedPreferences
            String className = classBookingPrefs.getString("className", "N/A");
            String startTime = classBookingPrefs.getString("startTime", "N/A");
            String location = classBookingPrefs.getString("location", "N/A");

            // Tạo TextView để hiển thị thông tin lớp học
            TextView upcomingClassInfo = new TextView(this);
            upcomingClassInfo.setText("Tên lớp: " + className + "\nThời gian: " + startTime + "\nĐịa điểm: " + location);
            upcomingClassInfo.setTextSize(14);
            upcomingClassInfo.setPadding(0, 10, 0, 10);

            // Thêm TextView vào upcomingClassesSection
            upcomingClassesSection.addView(upcomingClassInfo);
        } else {
            // Hiển thị Toast nếu không có lớp học sắp diễn ra
            Toast.makeText(this, "Không có lớp học nào sắp diễn ra", Toast.LENGTH_SHORT).show();
        }
    }

    // Đăng nhập vào Firebase và lưu thông tin vào SharedPreferences
    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Lưu thông tin vào SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("IS_REGISTERED", true);
                        editor.putString("USERNAME", user.getDisplayName()); // Hoặc bạn có thể dùng user.getEmail() nếu muốn
                        editor.apply();

                        // Cập nhật giao diện người dùng với tên người dùng
                        txtHoVaTen.setText("Xin chào, " + user.getDisplayName());
                        Toast.makeText(trangChu.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(trangChu.this, "Đăng nhập thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
