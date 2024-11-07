package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//khai báo thêm dòng này
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;
import android.content.SharedPreferences;


public class thongTinDatLop extends AppCompatActivity {

    private TextView classNameView, classCodeView, dateTimeView, durationView, typeView, locationView;
    private GridLayout seatGrid;
    private Button bookButton, cancelBookingButton, checkInButton;
    private int selectedSeat = -1;  // Biến để lưu vị trí được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.thongtindatlop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo TextView "Quay lại" và thiết lập sự kiện onClick
        TextView txtQuayLai = findViewById(R.id.txtReturn);
        txtQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thongTinDatLop.this, datLop.class); // Chuyển sang màn hình Trang Chủ
                startActivity(intent);
                finish(); // Đóng Activity hiện tại để tránh quay lại màn hình này khi bấm nút Back
            }
        });


        classNameView = findViewById(R.id.class_name);
        classCodeView = findViewById(R.id.class_code);
        dateTimeView = findViewById(R.id.date_time);
        durationView = findViewById(R.id.duration);
        typeView = findViewById(R.id.type);
        locationView = findViewById(R.id.location);
        seatGrid = findViewById(R.id.seat_grid);  // Kết nối GridLayout từ XML
        bookButton = findViewById(R.id.book_button);
        cancelBookingButton = findViewById(R.id.cancel_booking_button);
        checkInButton = findViewById(R.id.check_in_button);

        // Nhận dữ liệu từ intent
        String className = getIntent().getStringExtra("className");
        String classCode = getIntent().getStringExtra("classCode");
        String dateTime = getIntent().getStringExtra("dateTime");
        String duration = getIntent().getStringExtra("duration");
        String type = getIntent().getStringExtra("type");
        String location = getIntent().getStringExtra("location");
        int maxSeats = getIntent().getIntExtra("maxSeats", 20);

        // Hiển thị dữ liệu trong các TextView
        classNameView.setText(className);
        classCodeView.setText(classCode);
        dateTimeView.setText(dateTime);
        durationView.setText(duration);
        typeView.setText(type);
        locationView.setText(location);


        // Thêm các nút chỗ ngồi vào GridLayout
        for (int i = 1; i <= maxSeats; i++) {
            Button seatButton = new Button(this);
            seatButton.setText(String.format("%02d", i));  // Đặt số ghế từ 01, 02, ...
            seatButton.setTag(i);  // Sử dụng tag để lưu số ghế

            // Kiểm tra nếu chỗ đã bị đặt, thì disable nó
            if (i == 1) { // Giả sử chỗ 1 đã được đặt
                seatButton.setEnabled(false);
                seatButton.setBackgroundColor(getResources().getColor(R.color.my_color2));
            } else {
                seatButton.setBackgroundColor(getResources().getColor(R.color.white));
            }

            // Thiết lập sự kiện khi nhấp vào chỗ ngồi
            seatButton.setOnClickListener(v -> {
                // Nếu đã chọn một chỗ trước đó, bỏ chọn chỗ đó
                if (selectedSeat != -1) {
                    Button previousButton = seatGrid.findViewWithTag(selectedSeat);
                    previousButton.setBackgroundColor(getResources().getColor(R.color.white));
                }

                // Chọn chỗ ngồi mới
                selectedSeat = (int) v.getTag();
                seatButton.setBackgroundColor(getResources().getColor(R.color.my_color));  // Màu khi chọn
            });

            // Thêm nút vào GridLayout
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            seatGrid.addView(seatButton, params);
        }

        // Xử lý khi nhấn "Đặt chỗ"
        bookButton.setOnClickListener(v -> {
            if (selectedSeat != -1) {
                Toast.makeText(this, "Bạn đã chọn vị trí: " + selectedSeat, Toast.LENGTH_SHORT).show();
                bookButton.setVisibility(View.GONE);
                cancelBookingButton.setVisibility(View.VISIBLE);
                checkInButton.setVisibility(View.VISIBLE);

                // Lưu thông tin đặt chỗ vào SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("ClassBooking", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("className", className); // Tên lớp học
                editor.putString("startTime", dateTime); // Thời gian bắt đầu (sử dụng dateTime thay vì startTime)
                editor.putString("location", location);   // Địa điểm
                editor.putBoolean("isBooked", true);      // Đánh dấu đã đặt chỗ
                editor.apply();

            } else {
                Toast.makeText(this, "Vui lòng chọn một chỗ trước khi đặt!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý khi nhấn "Hủy đặt chỗ"
        cancelBookingButton.setOnClickListener(v -> {
            if (selectedSeat != -1) {
                Button selectedButton = seatGrid.findViewWithTag(selectedSeat);
                selectedButton.setBackgroundColor(getResources().getColor(R.color.my_color));
                selectedSeat = -1;
                Toast.makeText(this, "Đã hủy đặt chỗ.", Toast.LENGTH_SHORT).show();

                // Xóa thông tin đặt chỗ trong SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("ClassBooking", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("classCode");
                editor.remove("dateTime");
                editor.remove("location");
                editor.putBoolean("isBooked", false); // Đánh dấu chưa có lớp nào được đặt
                editor.apply();

                // Ẩn các nút "Hủy đặt chỗ" và "Quét để check-in"
                cancelBookingButton.setVisibility(View.GONE);
                checkInButton.setVisibility(View.GONE);
                bookButton.setVisibility(View.VISIBLE);
            }
        });

        // Xử lý khi nhấn "Quét để check-in"
        checkInButton.setOnClickListener(v -> {
            // Chuyển sang màn hình hiển thị mã QR
            Intent intent = new Intent(thongTinDatLop.this, QRdatLop.class);
            intent.putExtra("classCode", classCode);
            intent.putExtra("dateTime", dateTime);
            intent.putExtra("location", location); // dòng này để truyền "location"
            intent.putExtra("seatPosition", String.format("%02d", selectedSeat)); // Thêm "seatPosition" để truyền vị trí đã chọn
            startActivity(intent);
        });
    }
}
