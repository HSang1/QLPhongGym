package com.example.qlphonggym;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SuaHoSo extends AppCompatActivity {

    private EditText etFullName, etUsername, etPhoneNumber, etEmail;
    private Spinner spinnerCity;
    private DatabaseReference dbRefUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suahoso);

        // Ánh xạ các view trong layout
        etFullName = findViewById(R.id.edtFullName);
        etUsername = findViewById(R.id.edtUsername);
        etPhoneNumber = findViewById(R.id.edtPhoneNumber);
        etEmail = findViewById(R.id.edtEmail);
        spinnerCity = findViewById(R.id.spinnerCity);

        // Không cho phép chỉnh sửa email và username
        etEmail.setEnabled(false);
        etUsername.setEnabled(false); // Không cho phép chỉnh sửa Username

        // Khởi tạo Firebase Database và Firebase Auth
        dbRefUser = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Lấy email của người dùng đã đăng nhập
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

        if (userEmail != null) {
            // Gọi hàm để lấy thông tin người dùng từ Firebase
            getUserInfoByEmail(userEmail);
        } else {
            Toast.makeText(SuaHoSo.this, "Lỗi khi lấy email người dùng", Toast.LENGTH_SHORT).show();
        }

        // Cấu hình Spinner với danh sách thành phố
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vietnam_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        // Lưu thông tin khi nhấn nút Save
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> {
            saveUserInfo(userEmail);
            Intent intent = new Intent(SuaHoSo.this, hoSo_user.class);
            startActivity(intent);
            finish();
        });

        // Nút Xóa Tài Khoản
        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnDeleteAccount.setOnClickListener(v -> {
            // Hiển thị cảnh báo xác nhận xóa tài khoản
            showDeleteAccountDialog();
        });
    }

    private void getUserInfoByEmail(String email) {
        // Set email vào trường email để hiển thị
        etEmail.setText(email);

        // Truy vấn tất cả dữ liệu trong "users" và lọc ra người dùng với email trùng khớp
        dbRefUser.get().addOnSuccessListener(dataSnapshot -> {
            boolean found = false;
            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                String storedEmail = userSnapshot.child("email").getValue(String.class);
                if (storedEmail != null && storedEmail.equals(email)) {
                    found = true;

                    // Lấy thông tin người dùng
                    String fullName = userSnapshot.child("fullName").getValue(String.class);
                    String username = userSnapshot.child("username").getValue(String.class);
                    String phoneNumber = userSnapshot.child("phoneNumber").getValue(String.class);
                    String city = userSnapshot.child("city").getValue(String.class);

                    // Cập nhật các trường thông tin vào EditText và Spinner
                    etFullName.setText(fullName != null ? fullName : "N/A");
                    etUsername.setText(username != null ? username : "N/A");  // Hiển thị Username nhưng không thể chỉnh sửa
                    etPhoneNumber.setText(phoneNumber != null ? phoneNumber : "N/A");

                    // Cập nhật city vào spinner (nếu có)
                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerCity.getAdapter();
                    int position = adapter.getPosition(city);
                    if (position != -1) {
                        spinnerCity.setSelection(position);  // Chọn thành phố đúng trong spinner
                    } else {
                        spinnerCity.setSelection(0);  // Mặc định chọn mục đầu tiên nếu không tìm thấy
                    }

                    // Lấy userId từ getKey() để sử dụng trong phương thức lưu thông tin
                    String userId = userSnapshot.getKey();
                    etUsername.setTag(userId); // Lưu userId trong tag của EditText (hoặc có thể sử dụng một biến toàn cục)
                    break;
                }
            }
            if (!found) {
                Toast.makeText(SuaHoSo.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SuaHoSo.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveUserInfo(String userEmail) {
        String fullName = etFullName.getText().toString();
        String username = etUsername.getText().toString(); // Giá trị này sẽ không thay đổi
        String phoneNumber = etPhoneNumber.getText().toString();
        String email = etEmail.getText().toString();
        String city = spinnerCity.getSelectedItem().toString();

        // Kiểm tra các trường quan trọng
        if (fullName.isEmpty() || username.isEmpty() || phoneNumber.isEmpty() || city.isEmpty()) {
            Toast.makeText(SuaHoSo.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy userId từ tag của EditText (được lưu ở trên trong getUserInfoByEmail)
        String userId = (String) etUsername.getTag();

        // Cập nhật thông tin người dùng trong Firebase (không cập nhật username)
        dbRefUser.child(userId).child("fullName").setValue(fullName);
        dbRefUser.child(userId).child("phoneNumber").setValue(phoneNumber);
        dbRefUser.child(userId).child("city").setValue(city);

        // Thông báo đã lưu thành công
        Toast.makeText(SuaHoSo.this, "Đã lưu thông tin chỉnh sửa!", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteAccountDialog() {
        // Hiển thị cảnh báo xác nhận xóa tài khoản
        new AlertDialog.Builder(SuaHoSo.this)
                .setMessage("Bạn chắc chắn muốn xóa tài khoản? Việc này sẽ không thể phục hồi!")
                .setCancelable(false)
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAccount() {
        String userId = (String) etUsername.getTag(); // Lấy userId từ tag của EditText

        mAuth.getCurrentUser().delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                dbRefUser.child(userId).removeValue().addOnCompleteListener(removeTask -> {
                    if (removeTask.isSuccessful()) {
                        Toast.makeText(SuaHoSo.this, "Tài khoản đã được xóa!", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        mAuth.signOut();  // Đăng xuất người dùng khỏi Firebase

                        // 2. Xóa dữ liệu lưu trữ người dùng trong SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();  // Xóa toàn bộ dữ liệu lưu trữ
                        editor.apply();   // Lưu thay đổi

                        Intent intent = new Intent(SuaHoSo.this, trangChu.class);

                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SuaHoSo.this, "Lỗi khi xóa dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(SuaHoSo.this, "Lỗi khi xóa tài khoản", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
