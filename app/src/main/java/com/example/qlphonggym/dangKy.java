package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter; // Tạo Adapter để kết nối Spinner
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//khai báo thêm dòng sau
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.content.SharedPreferences;

public class dangKy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dangky);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Khởi tạo Spinner cho Thành Phố
        Spinner spinnerCity = findViewById(R.id.spinner); // Đảm bảo rằng ID của Spinner là "spinner"
        //Liên kết Spinner với giao diện XML bằng ID của nó (R.id.spinner)

        // Tạo Adapter để kết nối Spinner với danh sách các thành phố
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vietnam_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);


        // Khởi tạo TextView "Quay lại" và thiết lập sự kiện onClick
        TextView txtQuayLai = findViewById(R.id.txtReturn);
        txtQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dangKy.this, DangkysdtActivity.class); // Chuyển sang màn hình Trang Chủ
                startActivity(intent);
                finish(); // Đóng Activity hiện tại để tránh quay lại màn hình này khi bấm nút Back
            }
        });


        // Khởi tạo các thành phần trong form đăng ký
        Button btnDangKy = findViewById(R.id.btDangKy);
        EditText txtHoTen = findViewById(R.id.txtHoTen);
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtAddress = findViewById(R.id.txtDiaChi);

        // Nhận số điện thoại từ DangkysdtActivity
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("PHONE_NUMBER");

        // Xử lý sự kiện khi nhấn nút Đăng Ký
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtHoTen.getText().toString();
                String email = txtEmail.getText().toString();
                String address = txtAddress.getText().toString();
                String city = spinnerCity.getSelectedItem().toString(); // Lấy thành phố từ Spinner

                if (username.isEmpty()) {
                    Toast.makeText(dangKy.this, "Vui lòng nhập tên đăng nhập", Toast.LENGTH_SHORT).show();
                } else {
                    // Lưu thông tin người dùng vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USERNAME", username);
                    editor.putString("PHONE_NUMBER", phoneNumber); // Lưu số điện thoại
                    editor.putString("EMAIL", email);
                    editor.putString("ADDRESS", address);
                    editor.putString("CITY", city); // Lưu thành phố
                    editor.putBoolean("IS_REGISTERED", true); // Đánh dấu đã đăng ký
                    editor.apply(); // Lưu thay đổi

                    // Hiển thị thông báo khi lưu thành công
                    Toast.makeText(dangKy.this, "Đã lưu thông tin đăng ký thành công", Toast.LENGTH_SHORT).show();

                    // Chuyển đến trang chủ sau khi đăng ký thành công và gửi tên đăng nhập
                    Intent intent = new Intent(dangKy.this, trangChu.class);
                    intent.putExtra("USERNAME", username); // Thêm tên đăng nhập vào Intent
                    startActivity(intent);
                    overridePendingTransition(0, 0);  // Tắt animation chuyển Activity
                    finish();
                }
            }
        });

    }
}
