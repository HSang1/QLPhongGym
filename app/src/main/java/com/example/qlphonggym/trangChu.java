package com.example.qlphonggym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

public class trangChu extends AppCompatActivity {

    private LinearLayout upcomingClassesSection;
    private LinearLayout taiKhoanSection;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private DatabaseReference thetapRef;
    private TextView txtHoVaTen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trangchu);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        thetapRef = database.getReference("TheTap");

        // Lấy các thành phần giao diện
        txtHoVaTen = findViewById(R.id.txtHoVaTen);
        Button btnDatLop = findViewById(R.id.btDatLop);
        Button btnDatPT = findViewById(R.id.btTapPT);
        Button btnNhanXet = findViewById(R.id.btnNhanXet);
        upcomingClassesSection = findViewById(R.id.upcomingClassesSection);
        taiKhoanSection = findViewById(R.id.TaiKhoan);
        ImageView cuaHang = findViewById(R.id.cuaHang);
        LinearLayout baiBao1 = findViewById(R.id.BaiBao1);
        LinearLayout baiBao2 = findViewById(R.id.BaiBao2);
        LinearLayout baiBao3 = findViewById(R.id.BaiBao3);
        LinearLayout baiBao4 = findViewById(R.id.BaiBao4);

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

                            // Kiểm tra vai trò người dùng
                            String role = dataSnapshot.child("role").getValue(String.class);
                            if (role != null && role.equals("Admin")) {
                                Intent intent = new Intent(trangChu.this, trangchu_admin.class);
                                startActivity(intent);
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

        btnDatLop.setOnClickListener(v -> {
            // Kiểm tra thẻ hợp lệ trước khi chuyển màn hình
            checkUserCardValidityAndNavigateToDatLop();
        });

        // Thiết lập sự kiện onClick cho Button "Đặt PT"
        btnDatPT.setOnClickListener(v -> {
            // Kiểm tra thẻ hợp lệ trước khi chuyển màn hình
            checkUserCardValidityAndNavigateToDatPT();
        });


        btnNhanXet.setOnClickListener(v -> {
            Intent intent = new Intent(trangChu.this, NhanXet.class); // Chuyển sang màn hình Đặt lớp
            startActivity(intent);
        });

        cuaHang.setOnClickListener(v -> {
            Intent intent = new Intent(trangChu.this, CuaHang.class); // Chuyển sang màn hình Đặt lớp
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

        baiBao1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangChu.this, Baibao1.class);
                startActivity(intent);
            }
        });
        baiBao2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangChu.this, Baibao2.class);
                startActivity(intent);
            }
        });

        baiBao3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangChu.this, Baibao3.class);
                startActivity(intent);
            }
        });

        baiBao4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(trangChu.this, Baibao4.class);
                startActivity(intent);
            }
        });


    }



    private void checkUserCardValidityAndNavigateToDatLop() {
        // Lấy thông tin từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isRegistered = sharedPreferences.getBoolean("IS_REGISTERED", false);
        String username = sharedPreferences.getString("USERNAME", null);

        // Kiểm tra xem người dùng đã đăng ký và có username hợp lệ
        if (isRegistered && username != null && !username.isEmpty()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // Truy vấn Firebase theo username đã lấy từ SharedPreferences
                thetapRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean hasValidCard = false;

                        // Duyệt qua các thẻ tập
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String loaiThe = snapshot.child("loaiThe").getValue(String.class);
                            String ngayKetThuc = snapshot.child("ngayKetThuc").getValue(String.class);

                            // Kiểm tra loại thẻ và ngày kết thúc
                            if (isCardTypeValid(loaiThe) && isCardValid(ngayKetThuc)) {
                                hasValidCard = true;
                                break;
                            }
                        }

                        // Điều hướng nếu thẻ hợp lệ
                        if (hasValidCard) {
                            Intent intent = new Intent(trangChu.this, datLop.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(trangChu.this, "Thẻ không hợp lệ hoặc đã hết hạn!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(trangChu.this, "Lỗi khi kiểm tra thẻ!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(trangChu.this, "Người dùng chưa đăng nhập hoặc thông tin không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserCardValidityAndNavigateToDatPT() {
        // Lấy thông tin từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isRegistered = sharedPreferences.getBoolean("IS_REGISTERED", false);
        String username = sharedPreferences.getString("USERNAME", null);

        // Kiểm tra xem người dùng đã đăng ký và có username hợp lệ
        if (isRegistered && username != null && !username.isEmpty()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // Truy vấn Firebase theo username đã lấy từ SharedPreferences
                thetapRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean hasValidCard = false;

                        // Duyệt qua các thẻ tập
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String loaiThe = snapshot.child("loaiThe").getValue(String.class);
                            String ngayKetThuc = snapshot.child("ngayKetThuc").getValue(String.class);

                            // Kiểm tra loại thẻ và ngày kết thúc
                            if (isCardTypeValid(loaiThe) && isCardValid(ngayKetThuc)) {
                                hasValidCard = true;
                                break;
                            }
                        }

                        // Điều hướng nếu thẻ hợp lệ
                        if (hasValidCard) {
                            Intent intent = new Intent(trangChu.this, datPT.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(trangChu.this, "Thẻ không hợp lệ hoặc đã hết hạn!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(trangChu.this, "Lỗi khi kiểm tra thẻ!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(trangChu.this, "Người dùng chưa đăng nhập hoặc thông tin không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCardTypeValid(String loaiThe) {
        return loaiThe.equals("3") || loaiThe.equals("4");
    }

    private boolean isCardValid(String ngayKetThuc) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            String currentDate = sdf.format(calendar.getTime());
            return currentDate.compareTo(ngayKetThuc) <= 0; // So sánh ngày hiện tại với ngày kết thúc
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            displayUpcomingClass(); // Làm mới thông tin lớp học
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayUpcomingClass() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            return; // Thoát nếu người dùng chưa đăng nhập
        }

        // Xóa các lớp học cũ trước khi hiển thị mới
        upcomingClassesSection.removeAllViews();

        // Luôn giữ tiêu đề "Lớp học sắp diễn ra"
        TextView title = new TextView(trangChu.this);
        title.setText("Lớp học sắp diễn ra");
        title.setTextSize(18);
        title.setTextColor(getResources().getColor(R.color.black));
        title.setPadding(0, 10, 0, 10);
        upcomingClassesSection.addView(title);

        // Tập hợp các lớp học đã hiển thị (để tránh trùng lặp)
        HashSet<String> displayedClasses = new HashSet<>();

        // Lấy dữ liệu từ Intent nếu có
        Intent intent = getIntent();
        boolean isBooked = intent.getBooleanExtra("isBooked", false);
        if (isBooked) {
            String className = intent.getStringExtra("className");
            String dateTime = intent.getStringExtra("dateTime");
            String startTime = intent.getStringExtra("startTime"); // Thời gian bắt đầu
            String location = intent.getStringExtra("location");

            // Kiểm tra lớp học đã hiển thị hay chưa
            String classKey = className + "_" + dateTime + "_" + location;
            if (!displayedClasses.contains(classKey)) {
                displayedClasses.add(classKey);

                // Hiển thị thông tin lớp học
                TextView upcomingClassInfo = new TextView(trangChu.this);
                upcomingClassInfo.setText("Tên lớp: " + className + "\nNgày: " + dateTime + "\nThời gian bắt đầu: " + startTime + "\nĐịa điểm: " + location);
                upcomingClassInfo.setTextSize(14);
                upcomingClassInfo.setPadding(10, 10, 10, 10);
                upcomingClassInfo.setBackgroundColor(getResources().getColor(R.color.white));
                upcomingClassInfo.setTextColor(getResources().getColor(R.color.black));

                upcomingClassesSection.addView(upcomingClassInfo);
            }
        } else {
            // Truy vấn Firebase nếu không có dữ liệu từ Intent
            DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
            String userId = currentUser.getUid();
            String today = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            bookingsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String bookingDate = snapshot.child("bookingDate").getValue(String.class);
                        String className = snapshot.child("className").getValue(String.class);
                        String startTime = snapshot.child("dateTime").getValue(String.class); // Lấy thời gian bắt đầu
                        String location = snapshot.child("location").getValue(String.class);

                        // Kiểm tra nếu lớp học diễn ra hôm nay
                        if (bookingDate != null && bookingDate.equals(today)) {
                            // Kiểm tra lớp học đã hiển thị hay chưa
                            String classKey = className + "_" + bookingDate + "_" + location;
                            if (!displayedClasses.contains(classKey)) {
                                displayedClasses.add(classKey);

                                // Tạo TextView hiển thị thông tin lớp học
                                TextView upcomingClassInfo = new TextView(trangChu.this);
                                upcomingClassInfo.setText("Tên lớp: " + className + "\nNgày: " + bookingDate + "\nThời gian bắt đầu: " + startTime + "\nĐịa điểm: " + location);
                                upcomingClassInfo.setTextSize(14);
                                upcomingClassInfo.setPadding(10, 10, 10, 10);
                                upcomingClassInfo.setBackgroundColor(getResources().getColor(R.color.white));
                                upcomingClassInfo.setTextColor(getResources().getColor(R.color.black));

                                upcomingClassesSection.addView(upcomingClassInfo);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(trangChu.this, "Lỗi khi tải dữ liệu lớp học.", Toast.LENGTH_SHORT).show();
                }
            });
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
