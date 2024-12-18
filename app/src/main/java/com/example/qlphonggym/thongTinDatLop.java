package com.example.qlphonggym;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class thongTinDatLop extends AppCompatActivity {

    private TextView classNameView, classCodeView, dateTimeView, durationView, typeView, locationView;
    private GridLayout seatGrid;
    private Button bookButton, cancelBookingButton, checkInButton;
    private int selectedSeat = -1;  // Biến để lưu vị trí được chọn
    private DatabaseReference bookingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongtindatlop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo Firebase reference
        bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");

        // Khởi tạo UI
        TextView txtQuayLai = findViewById(R.id.txtReturn);
        ImageView classImageView = findViewById(R.id.class_image);
        classNameView = findViewById(R.id.class_name);
        classCodeView = findViewById(R.id.class_code);
        dateTimeView = findViewById(R.id.date_time);
        durationView = findViewById(R.id.duration);
        locationView = findViewById(R.id.location);
        seatGrid = findViewById(R.id.seat_grid);
        bookButton = findViewById(R.id.book_button);
        cancelBookingButton = findViewById(R.id.cancel_booking_button);
        checkInButton = findViewById(R.id.check_in_button);

        // Nhận dữ liệu từ intent
        String className = getIntent().getStringExtra("className");
        String classCode = getIntent().getStringExtra("classCode");
        String date = getIntent().getStringExtra("date"); // Ngày được truyền từ datLop
        String startTime = getIntent().getStringExtra("dateTime"); // Thời gian bắt đầu
        String location = getIntent().getStringExtra("location");
        // Nhận URL ảnh từ Intent
        String imageUrl = getIntent().getStringExtra("imageUrl");
        int maxSeats = getIntent().getIntExtra("maxSeats", 20);

        // Hiển thị dữ liệu
        classNameView.setText(className);
        classCodeView.setText("lớp: " + classCode);
        dateTimeView.setText(" Ngày: " + date); // Hiển thị ngày
        durationView.setText(" Bắt đầu: " + startTime); // Hiển thị thời gian bắt đầu
        locationView.setText("Địa điểm: " + location);

        // Thêm nút chỗ ngồi
        renderSeats(maxSeats, className);

        // Quay lại màn hình trước
        txtQuayLai.setOnClickListener(v -> {
            Intent intent = new Intent(thongTinDatLop.this, datLop.class);
            startActivity(intent);
            finish();
        });

        Glide.with(this)
                .load(imageUrl) // URL ảnh được truyền qua Intent
                .placeholder(R.drawable.placeholder_image) // Ảnh chờ
                .error(R.drawable.placeholder_image) // Ảnh lỗi
                .into(classImageView);

        // Kiểm tra trạng thái đặt chỗ
        checkBookingStatus(className);

        // Đặt chỗ
        bookButton.setOnClickListener(v -> handleBooking(className, date, location));

        // Hủy đặt chỗ
        cancelBookingButton.setOnClickListener(v -> cancelBooking(className, maxSeats));

        // Check-in
        checkInButton.setOnClickListener(v -> {
            Intent intent = new Intent(thongTinDatLop.this, QRdatLop.class);
            intent.putExtra("className", className);
            intent.putExtra("dateTime", date);
            intent.putExtra("location", location);
            intent.putExtra("seatPosition", String.format("%02d", selectedSeat));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Lấy className từ Intent đã truyền trước đó
        String className = getIntent().getStringExtra("className");
        // Kiểm tra trạng thái đặt chỗ để cập nhật giao diện
        checkBookingStatus(className);
    }

    private void handleBooking(String className, String dateTime, String location) {
        if (selectedSeat != -1) {
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            bookingRef.orderByChild("className").equalTo(className)  // Sử dụng className thay vì classCode
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean isAlreadyBooked = false;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String userId = snapshot.child("userId").getValue(String.class);
                                String bookingDate = snapshot.child("bookingDate").getValue(String.class);

                                // Kiểm tra trùng tên lớp (className) và ngày (bookingDate)
                                if (userId != null && userId.equals(currentUserId) &&
                                        bookingDate != null && bookingDate.equals(dateTime) &&
                                        snapshot.child("className").getValue(String.class).equals(className)) {
                                    isAlreadyBooked = true;
                                    break;
                                }
                            }

                            if (!isAlreadyBooked) {
                                HashMap<String, Object> bookingDetails = new HashMap<>();
                                bookingDetails.put("userId", currentUserId);
                                bookingDetails.put("className", className);
                                bookingDetails.put("dateTime", dateTime);
                                bookingDetails.put("location", location);
                                bookingDetails.put("seatPosition", selectedSeat);
                                bookingDetails.put("bookingDate", dateTime);

                                bookingRef.push().setValue(bookingDetails).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(thongTinDatLop.this, "Đặt chỗ thành công!", Toast.LENGTH_SHORT).show();
                                        bookButton.setVisibility(View.GONE);
                                        cancelBookingButton.setVisibility(View.VISIBLE);
                                        checkInButton.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(thongTinDatLop.this, "Lỗi khi đặt chỗ!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(thongTinDatLop.this, "Bạn đã đặt chỗ trong lớp này.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(thongTinDatLop.this, "Lỗi khi kiểm tra trạng thái đặt chỗ.", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "Vui lòng chọn chỗ ngồi!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkBookingStatus(String className) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String selectedDate = getIntent().getStringExtra("date"); // Ngày được truyền từ Intent

        // Kiểm tra xem người dùng đã đặt chỗ cho lớp học này vào ngày đã chọn chưa, sử dụng className thay vì classCode
        bookingRef.orderByChild("className").equalTo(className)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isBooked = false;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userId = snapshot.child("userId").getValue(String.class);
                            String bookingDate = snapshot.child("bookingDate").getValue(String.class);

                            // Kiểm tra nếu userId và bookingDate trùng khớp với người dùng và ngày đã chọn
                            if (userId != null && userId.equals(currentUserId) &&
                                    bookingDate != null && bookingDate.equals(selectedDate)) {
                                // Người dùng đã đặt chỗ cho lớp học này vào ngày này
                                isBooked = true;
                                selectedSeat = snapshot.child("seatPosition").getValue(Integer.class); // Lưu vị trí ghế đã đặt
                                break; // Đã tìm thấy đặt chỗ, thoát vòng lặp
                            }
                        }

                        // Cập nhật giao diện
                        if (isBooked) {
                            // Người dùng đã đặt chỗ, hiển thị nút hủy
                            bookButton.setVisibility(View.GONE);
                            cancelBookingButton.setVisibility(View.VISIBLE);
                            checkInButton.setVisibility(View.VISIBLE);
                        } else {
                            // Người dùng chưa đặt chỗ, hiển thị nút đặt chỗ
                            bookButton.setVisibility(View.VISIBLE);
                            cancelBookingButton.setVisibility(View.GONE);
                            checkInButton.setVisibility(View.GONE);
                            selectedSeat = -1; // Reset trạng thái ghế
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(thongTinDatLop.this, "Lỗi khi kiểm tra trạng thái đặt chỗ.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderSeats(int maxSeats, String className) {
        seatGrid.removeAllViews();

        String selectedDate = getIntent().getStringExtra("date"); // Ngày được truyền từ Intent
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Lọc theo className và bookingDate
        bookingRef.orderByChild("className").equalTo(className).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i = 1; i <= maxSeats; i++) {
                    Button seatButton = new Button(thongTinDatLop.this);
                    seatButton.setText(String.format("%02d", i));
                    seatButton.setTag(i);

                    boolean isSeatTaken = false;
                    boolean isCurrentUser = false;

                    // Lặp qua tất cả các booking trong lớp học này
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String bookingDate = snapshot.child("bookingDate").getValue(String.class); // Ngày đã đặt
                        String userId = snapshot.child("userId").getValue(String.class); // Người dùng đã đặt
                        Integer seatNumber = snapshot.child("seatPosition").getValue(Integer.class); // Ghế đã đặt

                        // Lọc theo className và bookingDate để đảm bảo chỉ lấy ghế của lớp học và ngày đã chọn
                        if (bookingDate != null && bookingDate.equals(selectedDate) &&
                                seatNumber != null && seatNumber == i &&
                                snapshot.child("className").getValue(String.class).equals(className)) {

                            isSeatTaken = true;

                            // Kiểm tra xem ghế này có phải của người dùng hiện tại không
                            if (userId != null && userId.equals(currentUserId)) {
                                isCurrentUser = true;
                                selectedSeat = seatNumber;
                            }
                            break;
                        }
                    }

                    // Cập nhật giao diện của ghế
                    if (isSeatTaken) {
                        seatButton.setEnabled(false);
                        if (isCurrentUser) {
                            seatButton.setBackgroundColor(getResources().getColor(R.color.my_color)); // Màu cho người dùng hiện tại
                        } else {
                            seatButton.setBackgroundColor(getResources().getColor(R.color.my_color2)); // Màu ghế đã đặt
                        }
                    } else {
                        seatButton.setBackgroundColor(getResources().getColor(R.color.white)); // Ghế trống
                        seatButton.setOnClickListener(v -> {
                            if (selectedSeat != -1) {
                                Button previousButton = seatGrid.findViewWithTag(selectedSeat);
                                if (previousButton != null) {
                                    previousButton.setBackgroundColor(getResources().getColor(R.color.white));
                                }
                            }
                            selectedSeat = (int) v.getTag();
                            seatButton.setBackgroundColor(getResources().getColor(R.color.my_color));
                        });
                    }

                    // Thêm ghế vào layout
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.width = 0;
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                    params.setMargins(8, 8, 8, 8);
                    seatGrid.addView(seatButton, params);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(thongTinDatLop.this, "Lỗi khi tải danh sách ghế.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelBooking(String className, int maxSeats) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String selectedDate = getIntent().getStringExtra("date"); // Ngày được truyền từ Intent

        bookingRef.orderByChild("className").equalTo(className)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean bookingFound = false;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userId = snapshot.child("userId").getValue(String.class);
                            String bookingDate = snapshot.child("bookingDate").getValue(String.class);

                            if (userId != null && userId.equals(currentUserId) &&
                                    bookingDate != null && bookingDate.equals(selectedDate) &&
                                    snapshot.child("className").getValue(String.class).equals(className)) {

                                snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(thongTinDatLop.this, "Hủy đặt chỗ thành công!", Toast.LENGTH_SHORT).show();

                                        // Cập nhật trạng thái UI
                                        bookButton.setVisibility(View.VISIBLE);
                                        cancelBookingButton.setVisibility(View.GONE);
                                        checkInButton.setVisibility(View.GONE);

                                        // Reset trạng thái ghế và làm mới giao diện ghế
                                        selectedSeat = -1;
                                        renderSeats(maxSeats, className);
                                    } else {
                                        Toast.makeText(thongTinDatLop.this, "Lỗi khi hủy đặt chỗ.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                bookingFound = true;
                                break;
                            }
                        }

                        if (!bookingFound) {
                            Toast.makeText(thongTinDatLop.this, "Không tìm thấy đặt chỗ của bạn.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(thongTinDatLop.this, "Lỗi khi hủy đặt chỗ.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
