package com.example.qlphonggym;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//khai bao them dong nay
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;

public class trangChu extends AppCompatActivity {

    private LinearLayout upcomingClassesSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.trangchu);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy TextView và Button từ giao diện
        TextView txtTaiKhoan = findViewById(R.id.txtTaiKhoan);
        TextView txtHoTen = findViewById(R.id.txtHoTen);
        Button btnDatLop = findViewById(R.id.btDatLop);

        //Đây là biến đại diện cho LinearLayout đó trong mã Java. Sau khi liên kết, bạn có thể tương tác
        // với LinearLayout này thông qua biến upcomingClassesSection,
        // chẳng hạn như thêm các TextView hoặc thay đổi thuộc tính của nó.
        upcomingClassesSection = findViewById(R.id.upcomingClassesSection);

        // Kiểm tra trạng thái đăng ký của người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isRegistered = sharedPreferences.getBoolean("IS_REGISTERED", false);
        String username = sharedPreferences.getString("USERNAME", null);

        // Cập nhật TextView txtHoTen để hiển thị tên đăng nhập hoặc "Đăng nhập" nếu chưa đăng nhập
        if (isRegistered && username != null && !username.isEmpty()) {
            txtHoTen.setText("Xin chào, " + username);
        } else {
            txtHoTen.setText("Đăng nhập");
        }

        // Hiển thị lớp học sắp diễn ra nếu đã đặt chỗ
        if (isRegistered) {
            displayUpcomingClass();
        }

        // Thiết lập sự kiện onClick cho phần "Tài khoản"
        txtTaiKhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (isRegistered) {
                    // Nếu người dùng đã đăng ký, chuyển sang màn hình giaoDienUser để hiển thị thông tin tài khoản
                    intent = new Intent(trangChu.this, giaoDienUser.class);
                } else {
                    // Nếu chưa đăng ký, chuyển sang màn hình taiKhoan để đăng ký tài khoản
                    intent = new Intent(trangChu.this, Taikhoan.class);
                }
                startActivity(intent);
            }
        });

        // Thiết lập sự kiện onClick cho Button "Đặt lớp"
        btnDatLop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangChu.this, datLop.class); // Chuyển sang màn hình Đặt lớp
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
            Toast.makeText(this, "Không có lớp học nào sắp diễn ra", Toast.LENGTH_SHORT).show();
        }
    }
}
