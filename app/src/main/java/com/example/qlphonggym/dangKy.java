package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.qlphonggym.CSDL.CSDL_Users; // Import lớp CSDL_Users
import com.google.firebase.database.ValueEventListener;

public class dangKy extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database; // Firebase Realtime Database
    private DatabaseReference usersRef; // Tham chiếu đến Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangky);

        // Khởi tạo Firebase Auth và Realtime Database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users"); // Tham chiếu đến node "users" trong Realtime Database

        // Khởi tạo Spinner cho Thành Phố
        Spinner spinnerCity = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vietnam_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        // Nhận số điện thoại từ DangkysdtActivity
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("PHONE_NUMBER"); // Nhận số điện thoại từ Intent

        // Khởi tạo các thành phần trong form đăng ký
        Button btnDangKy = findViewById(R.id.btDangKy);
        EditText txtHoTen = findViewById(R.id.txtHoVaTen); // Lấy tên người dùng
        EditText txtTaiKhoan = findViewById(R.id.txtTaiKhoan);
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtPassword = findViewById(R.id.txtMK); // Thêm trường nhập mật khẩu

        // Khởi tạo TextView "Quay lại" và thiết lập sự kiện onClick
        TextView txtQuayLai = findViewById(R.id.txtReturn);
        txtQuayLai.setOnClickListener(v -> {
            Intent backIntent = new Intent(dangKy.this, DangkysdtActivity.class);
            startActivity(backIntent);
            finish();
        });

        btnDangKy.setOnClickListener(v -> {
            String fullname = txtHoTen.getText().toString(); // Lấy tên người dùng
            String username = txtTaiKhoan.getText().toString();
            String email = txtEmail.getText().toString();
            String city = spinnerCity.getSelectedItem().toString(); // Lấy thành phố từ Spinner
            String password = txtPassword.getText().toString(); // Lấy mật khẩu từ trường nhập

            // Kiểm tra xem tất cả các trường có đầy đủ không
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(dangKy.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                txtTaiKhoan.setError("Vui lòng nhập tên đăng nhập");
                txtTaiKhoan.requestFocus();
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(dangKy.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                txtEmail.setError("Vui lòng nhập email");
                txtEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(dangKy.this, "Vui lòng nhập lại email hợp lệ", Toast.LENGTH_SHORT).show();
                txtEmail.setError("Email không hợp lệ");
                txtEmail.requestFocus();
            } else if (TextUtils.isEmpty(password) || password.length() < 6) {
                Toast.makeText(dangKy.this, "Mật khẩu phải từ 6 ký tự trở lên", Toast.LENGTH_SHORT).show();
                txtPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
                txtPassword.requestFocus();
            } else if (TextUtils.isEmpty(fullname)) {
                Toast.makeText(dangKy.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                txtHoTen.setError("Vui lòng nhập họ và tên");
                txtHoTen.requestFocus();
            } else {
                // Kiểm tra xem username đã tồn tại trong Firebase chưa
                usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Nếu username đã tồn tại
                            Toast.makeText(dangKy.this, "Tên người dùng đã tồn tại. Vui lòng chọn tên khác.", Toast.LENGTH_SHORT).show();
                            txtTaiKhoan.setError("Tên người dùng đã tồn tại");
                            txtTaiKhoan.requestFocus();
                        } else {
                            // Kiểm tra xem email đã tồn tại trong Firebase chưa
                            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Nếu email đã tồn tại
                                        Toast.makeText(dangKy.this, "Email đã được sử dụng. Vui lòng chọn email khác.", Toast.LENGTH_SHORT).show();
                                        txtEmail.setError("Email đã tồn tại");
                                        txtEmail.requestFocus();
                                    } else {
                                        // Tạo tài khoản Firebase bằng email và mật khẩu
                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(dangKy.this, task -> {
                                                    if (task.isSuccessful()) {
                                                        // Đăng ký thành công, lưu thông tin vào Realtime Database
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        if (user != null) {
                                                            String userId = user.getUid();
                                                            // Tạo đối tượng CSDL_Users với quyền "user"
                                                            CSDL_Users userInfo = new CSDL_Users(username, phoneNumber, email, city, fullname, "user");

                                                            // Lưu vào Realtime Database tại node "users/{userId}"
                                                            usersRef.child(userId).setValue(userInfo)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        // Gửi email xác thực
                                                                        user.sendEmailVerification()
                                                                                .addOnCompleteListener(task1 -> {
                                                                                    if (task1.isSuccessful()) {
                                                                                        Toast.makeText(dangKy.this, "Đăng ký thành công! Kiểm tra email để xác thực.", Toast.LENGTH_SHORT).show();
                                                                                    } else {
                                                                                        Toast.makeText(dangKy.this, "Gửi email xác thực thất bại", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });

                                                                        // Chuyển sang trang đăng nhập hoặc màn hình khác
                                                                        Intent intent1 = new Intent(dangKy.this, trangChu.class);
                                                                        startActivity(intent1);
                                                                        finish();
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        Toast.makeText(dangKy.this, "Lỗi khi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
                                                                    });
                                                        }
                                                    } else {
                                                        Toast.makeText(dangKy.this, "Đăng ký thất bại. Kiểm tra lại thông tin", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(dangKy.this, "Lỗi khi kiểm tra email", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(dangKy.this, "Lỗi khi kiểm tra username", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
