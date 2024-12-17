package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DangkysdtActivity extends AppCompatActivity {

    private EditText txtSDT;
    private CheckBox checkBox;
    private Button buttonXacThucSDT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangkysdt); // Layout cho DangkysdtActivity

        // Khởi tạo các view
        txtSDT = findViewById(R.id.txtSDT);
        checkBox = findViewById(R.id.checkBox);
        buttonXacThucSDT = findViewById(R.id.buttonXacThucSDT); // Khởi tạo button

        // Lắng nghe thay đổi trong trường số điện thoại
        txtSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Lắng nghe sự kiện CheckBox
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> validateInput());

        // Xử lý khi nhấn nút xác thực số điện thoại
        buttonXacThucSDT.setOnClickListener(view -> {
            String phoneNumber = txtSDT.getText().toString().trim();
            boolean isPhoneNumberValid = phoneNumber.matches("^(03[2-9]|05[6|8]|07[0|6-9]|08[1-6|8-9]|09[0-4|6-8])\\d{7}$");
            boolean isCheckboxChecked = checkBox.isChecked();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại.", Toast.LENGTH_SHORT).show();
                txtSDT.requestFocus();
            } else if (!phoneNumber.matches("\\d{10}")) {
                Toast.makeText(this, "Số điện thoại phải có đúng 10 chữ số.", Toast.LENGTH_SHORT).show();
                txtSDT.requestFocus();
            } else if (!isPhoneNumberValid) {
                Toast.makeText(this, "Số điện thoại không đúng định dạng. Vui lòng nhập lại!", Toast.LENGTH_SHORT).show();
                txtSDT.requestFocus();
            } else if (!isCheckboxChecked) {
                Toast.makeText(this, "Bạn chưa đồng ý với các điều khoản sử dụng.", Toast.LENGTH_SHORT).show();
                checkBox.requestFocus();
            } else {
                Intent intent = new Intent(DangkysdtActivity.this, dangKy.class);
                intent.putExtra("PHONE_NUMBER", phoneNumber); // Truyền số điện thoại qua Intent
                startActivity(intent);
            }
        });


    }

    private void validateInput() {
        buttonXacThucSDT.setEnabled(true);
    }
}
