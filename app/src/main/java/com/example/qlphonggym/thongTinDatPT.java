package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//khai báo thêm dòng này
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class thongTinDatPT extends AppCompatActivity {

    private TextView PTNameView, dateTimeView, locationView, sessionView,cityView;
    private Button bookPTButton, cancelPTBookingButton, checkInPTButton;
    private DatabaseReference bookingRef;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongtindatpt);

        // Thiết lập padding cho hệ thống UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
            return insets;
        });

        // Firebase database reference
        bookingRef = FirebaseDatabase.getInstance().getReference("BookingsPT");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Gán giá trị `currentUserId`

        // Ánh xạ UI
        TextView txtQuayLai = findViewById(R.id.txtReturn);
        sessionView = findViewById(R.id.session);
        cityView = findViewById(R.id.city);
        ImageView classImageView = findViewById(R.id.PT_image);
        PTNameView = findViewById(R.id.PT_name);
        dateTimeView = findViewById(R.id.date_time);
        locationView = findViewById(R.id.location);
        bookPTButton = findViewById(R.id.bookPT_button);
        cancelPTBookingButton = findViewById(R.id.cancelPT_booking_button);
        checkInPTButton = findViewById(R.id.check_inPT_button);

        // Lấy dữ liệu từ Intent
        String PTId = getIntent().getStringExtra("PTId");
        String session = translateVietnameseSession(getIntent().getStringExtra("Session"));
        String date = getIntent().getStringExtra("Date");
        String city = getIntent().getStringExtra("City");
        String location = getIntent().getStringExtra("Location");
        String imageUrl = getIntent().getStringExtra("imageUrl");


        // Hiển thị dữ liệu
        PTNameView.setText("Tên PT : " + PTId);
        sessionView.setText("Buổi: " + session);
        dateTimeView.setText("Ngày: " + date);
        cityView.setText("Thành phố: " + city);
        locationView.setText("Địa điểm: " + location);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(classImageView);

        // Kiểm tra trạng thái đặt chỗ
        checkBookingStatus(PTId, date, session);

        // Quay lại màn hình trước
        txtQuayLai.setOnClickListener(v -> finish());

        // Đặt chỗ
        bookPTButton.setOnClickListener(v -> handleBooking(PTId, session, date, location, city));

        // Hủy đặt chỗ
        cancelPTBookingButton.setOnClickListener(v -> cancelBooking(PTId, session, date));
    }

    private String translateVietnameseSession(String session) {
        switch (session) {
            case "Morning":
                return "Sáng";
            case "Afternoon":
                return "Trưa";
            case "Evening":
                return "Chiều";
            case "Night":
                return "Tối";
            default:
                return session; // Giữ nguyên nếu không khớp
        }
    }


    private void handleBooking(String PTId, String session, String date, String location, String city) {
        bookingRef.orderByChild("PTId").equalTo(PTId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int bookingCount = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String bookingDate = snapshot.child("date").getValue(String.class);
                    String bookedSession = snapshot.child("session").getValue(String.class);

                    if (bookingDate != null && bookedSession != null &&
                            bookingDate.equals(date) && bookedSession.equals(session)) {
                        bookingCount++;
                    }
                }

                if (bookingCount >= 3) {
                    Toast.makeText(thongTinDatPT.this, "Lớp đã đủ 3 người, không thể đặt thêm!", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, Object> bookingDetails = new HashMap<>();
                bookingDetails.put("PTId", PTId);
                bookingDetails.put("date", date);
                bookingDetails.put("city", city);
                bookingDetails.put("location", location);
                bookingDetails.put("session", session);
                bookingDetails.put("userId", currentUserId);

                bookingRef.push().setValue(bookingDetails).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(thongTinDatPT.this, "Đặt PT thành công!", Toast.LENGTH_SHORT).show();
                        bookPTButton.setVisibility(View.GONE);
                        cancelPTBookingButton.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(thongTinDatPT.this, "Lỗi khi đặt PT!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(thongTinDatPT.this, "Lỗi khi đặt PT!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cancelBooking(String PTId, String session, String date) {
        bookingRef.orderByChild("PTId").equalTo(PTId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.child("userId").getValue(String.class);
                    String bookingDate = snapshot.child("date").getValue(String.class);
                    String bookedSession = snapshot.child("session").getValue(String.class);

                    // Chỉ xóa đặt chỗ nếu đúng lớp, buổi, và ngày
                    if (userId != null && userId.equals(currentUserId) &&
                            bookingDate != null && bookingDate.equals(date) &&
                            bookedSession != null && bookedSession.equals(session)) {
                        snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(thongTinDatPT.this, "Hủy đặt PT thành công!", Toast.LENGTH_SHORT).show();
                                bookPTButton.setVisibility(View.VISIBLE);
                                cancelPTBookingButton.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(thongTinDatPT.this, "Lỗi khi hủy đặt PT!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(thongTinDatPT.this, "Lỗi khi hủy đặt PT!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkBookingStatus(String PTId, String date, String session) {
        bookingRef.orderByChild("PTId").equalTo(PTId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isBookedByCurrentUser = false;
                int bookingCount = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.child("userId").getValue(String.class);
                    String bookingDate = snapshot.child("date").getValue(String.class);
                    String bookedSession = snapshot.child("session").getValue(String.class);

                    if (bookingDate != null && bookingDate.equals(date)) {
                        bookingCount++;
                        if (userId != null && userId.equals(currentUserId) &&
                                bookedSession != null && bookedSession.equals(session)) {
                            isBookedByCurrentUser = true;
                        }
                    }
                }

                if (isBookedByCurrentUser) {
                    bookPTButton.setVisibility(View.GONE);
                    cancelPTBookingButton.setVisibility(View.VISIBLE);
                } else if (bookingCount >= 3) {
                    bookPTButton.setVisibility(View.GONE);
                    cancelPTBookingButton.setVisibility(View.GONE);
                    Toast.makeText(thongTinDatPT.this, "Lớp đã đủ 3 người, không thể đặt thêm!", Toast.LENGTH_SHORT).show();
                } else {
                    bookPTButton.setVisibility(View.VISIBLE);
                    cancelPTBookingButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(thongTinDatPT.this, "Lỗi khi kiểm tra trạng thái đặt chỗ!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}