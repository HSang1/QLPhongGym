package com.example.qlphonggym;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.CSDL_Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ThemUser_Admin extends AppCompatActivity {

    private EditText edtTenNguoiDung, edtTaiKhoan, edtMatKhau, edtSDT, edtDiaChi, edtEmail;
    private RadioGroup radioGroupQuyen;
    private RadioButton radioUser, radioAdmin;
    private Button btnDangKy;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themuser_admin);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users"); // Tạo reference đến node "Users" trong Firebase

        // Kết nối với các view
        edtTenNguoiDung = findViewById(R.id.edtTenNguoiDung);
        edtTaiKhoan = findViewById(R.id.edtTaiKhoan);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtSDT = findViewById(R.id.edtSDT);

        edtEmail = findViewById(R.id.edtEmail);
        radioGroupQuyen = findViewById(R.id.radioGroupQuyen);
        radioUser = findViewById(R.id.radioUser);
        radioAdmin = findViewById(R.id.radioAdmin);
        btnDangKy = findViewById(R.id.btnDangKy);

        // Khởi tạo Spinner cho Thành Phố
        Spinner spinnerCity = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vietnam_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        // Xử lý sự kiện khi người dùng click vào nút đăng ký
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các EditText
                String fullName = edtTenNguoiDung.getText().toString().trim();
                String username = edtTaiKhoan.getText().toString().trim();
                String password = edtMatKhau.getText().toString().trim();
                String phoneNumber = edtSDT.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String city = spinnerCity.getSelectedItem().toString();

                // Kiểm tra quyền
                String role = "User";  // Mặc định là "User"
                if (radioAdmin.isChecked()) {
                    role = "Admin"; // Nếu chọn Admin thì gán quyền là "Admin"
                }

                // Kiểm tra các trường hợp đầu vào
                if (fullName.isEmpty() || username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ThemUser_Admin.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra username đã tồn tại chưa
                String finalRole = role;
                myRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Nếu username đã tồn tại
                            Toast.makeText(ThemUser_Admin.this, "Tên người dùng đã tồn tại. Vui lòng chọn tên khác.", Toast.LENGTH_SHORT).show();
                            edtTaiKhoan.setError("Tên người dùng đã tồn tại");
                            edtTaiKhoan.requestFocus();
                        } else {
                            // Kiểm tra email đã tồn tại chưa
                            myRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Nếu email đã tồn tại
                                        Toast.makeText(ThemUser_Admin.this, "Email đã được sử dụng. Vui lòng chọn email khác.", Toast.LENGTH_SHORT).show();
                                        edtEmail.setError("Email đã tồn tại");
                                        edtEmail.requestFocus();
                                    } else {
                                        // Tạo tài khoản Firebase với email và mật khẩu
                                        mAuth.createUserWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(ThemUser_Admin.this, task -> {
                                                    if (task.isSuccessful()) {
                                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                        if (firebaseUser != null) {
                                                            // Gửi email xác thực
                                                            firebaseUser.sendEmailVerification()
                                                                    .addOnCompleteListener(task1 -> {
                                                                        if (task1.isSuccessful()) {
                                                                            Toast.makeText(ThemUser_Admin.this, "Đã gửi email xác thực!", Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            Toast.makeText(ThemUser_Admin.this, "Gửi email xác thực thất bại!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });

                                                            // Tạo đối tượng CSDL_Users
                                                            CSDL_Users newUser = new CSDL_Users(username, phoneNumber, email, city, fullName, finalRole);

                                                            // Lưu dữ liệu vào Firebase Realtime Database
                                                            myRef.child(firebaseUser.getUid()).setValue(newUser)
                                                                    .addOnCompleteListener(task2 -> {
                                                                        if (task2.isSuccessful()) {
                                                                            Toast.makeText(ThemUser_Admin.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                                        } else {
                                                                            Toast.makeText(ThemUser_Admin.this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    } else {
                                                        Toast.makeText(ThemUser_Admin.this, "Đăng ký thất bại. Kiểm tra lại thông tin.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(ThemUser_Admin.this, "Lỗi khi kiểm tra email", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ThemUser_Admin.this, "Lỗi khi kiểm tra username", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}