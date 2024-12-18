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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class trangChu extends AppCompatActivity {

    private HashSet<String> displayedClasses = new HashSet<>();
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
        LinearLayout notificationButton = findViewById(R.id.notificationButton);
        LinearLayout storeButton = findViewById(R.id.storeButton);
        LinearLayout TaiKhoan = findViewById(R.id.TaiKhoan);
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

        // Thêm sự kiện click cho nút "Cửa Hàng"
        storeButton.setOnClickListener(v -> {
            Intent intent = new Intent(trangChu.this, CuaHang.class); // Chuyển đến màn hình Nhận xét
            startActivity(intent);
        });

        // Thêm sự kiện click cho nút "Nhận xét"
        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(trangChu.this, NhanXet.class); // Chuyển đến màn hình Nhận xét
            startActivity(intent);
        });

        // Thiết lập sự kiện click cho LinearLayout TaiKhoan
        TaiKhoan.setOnClickListener(v -> {
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
                            Toast.makeText(trangChu.this, "Bạn cần mua thẻ Gold Member tại cửa hàng để sử dụng dịch vụ", Toast.LENGTH_SHORT).show();
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
            return;
        }

        // Xóa các lớp học cũ và bộ nhớ đã hiển thị
        upcomingClassesSection.removeAllViews();
        displayedClasses.clear();

        // Thêm tiêu đề "Lớp học sắp diễn ra"
        TextView title = new TextView(trangChu.this);
        title.setText("Lớp học sắp diễn ra");
        title.setTextSize(18);
        title.setTextColor(getResources().getColor(R.color.black));
        title.setPadding(0, 10, 0, 10);
        upcomingClassesSection.addView(title);

        // Danh sách để lưu thông tin lớp học dưới dạng Map
        List<Map<String, String>> classList = new ArrayList<>();

        // Truy vấn dữ liệu từ Firebase
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        String userId = currentUser.getUid();

        bookingsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String bookingDate = snapshot.child("bookingDate").getValue(String.class); // Ngày
                    String className = snapshot.child("className").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);

                    if (bookingDate != null) {
                        // Lưu thông tin lớp học vào Map
                        Map<String, String> classInfo = new HashMap<>();
                        classInfo.put("className", className);
                        classInfo.put("bookingDate", bookingDate);
                        classInfo.put("location", location);
                        classList.add(classInfo);
                    }
                }

                // Sắp xếp danh sách lớp học theo ngày và thời gian (sắp xếp từ lớp học gần nhất)
                Collections.sort(classList, (o1, o2) -> {
                    return o1.get("bookingDate").compareTo(o2.get("bookingDate"));
                });

                // Hiển thị lớp học gần nhất (chỉ hiển thị 1 lớp học)
                if (!classList.isEmpty()) {
                    Map<String, String> classInfo = classList.get(0); // Lớp học đầu tiên (gần nhất)
                    String classKey = classInfo.get("className") + "_" + classInfo.get("bookingDate") + "_" + classInfo.get("location");
                    if (!displayedClasses.contains(classKey)) {
                        displayedClasses.add(classKey);

                        // Tạo TextView cho thông tin lớp học
                        TextView upcomingClassInfo = new TextView(trangChu.this);
                        upcomingClassInfo.setText(
                                "Tên lớp: " + classInfo.get("className") +
                                        "\nNgày: " + classInfo.get("bookingDate") +
                                        "\nĐịa điểm: " + classInfo.get("location")
                        );
                        upcomingClassInfo.setTextSize(14);
                        upcomingClassInfo.setPadding(20, 10, 20, 10); // Tăng padding
                        upcomingClassInfo.setBackgroundColor(getResources().getColor(R.color.pink_white));
                        upcomingClassInfo.setTextColor(getResources().getColor(R.color.black));

                        // Thêm đường phân cách
                        View separator = new View(trangChu.this);
                        separator.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                2 // Chiều cao của đường phân cách
                        ));
                        separator.setBackgroundColor(getResources().getColor(R.color.white)); // Màu của đường phân cách
                        separator.setPadding(0, 10, 0, 10); // Khoảng cách trên dưới

                        // Thêm các view vào layout
                        upcomingClassesSection.addView(upcomingClassInfo);
                        upcomingClassesSection.addView(separator);
                    }
                } else {
                    // Không có lớp học sắp diễn ra
                    TextView noClasses = new TextView(trangChu.this);

                    noClasses.setTextSize(14);
                    noClasses.setTextColor(getResources().getColor(R.color.black));
                    upcomingClassesSection.addView(noClasses);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(trangChu.this, "Lỗi khi tải dữ liệu lớp học.", Toast.LENGTH_SHORT).show();
            }
        });
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
