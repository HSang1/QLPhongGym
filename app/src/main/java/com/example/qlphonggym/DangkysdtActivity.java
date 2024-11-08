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
            if (!checkBox.isChecked()) {
                Toast.makeText(this, "Chưa đồng ý điều khoản sử dụng!", Toast.LENGTH_SHORT).show();
            } else if (!txtSDT.getText().toString().matches("^(03[2-9]|07[0|5-9]|08[1-5|8-9]|09[0-4])\\d{7}$")) {
                Toast.makeText(this, "Số điện thoại không đúng định dạng", Toast.LENGTH_SHORT).show();
            } else {
                // Khi điều kiện hợp lệ, chuyển đến trang đăng ký
                Intent intent = new Intent(DangkysdtActivity.this, dangKy.class);
                startActivity(intent);
            }
        });
    }

    private void validateInput() {
        String phoneNumber = txtSDT.getText().toString();
        boolean isPhoneNumberValid = phoneNumber.matches("^(03[2-9]|07[0|5-9]|08[1-5|8-9]|09[0-4])\\d{7}$");
        boolean isPhoneNumberLengthValid = phoneNumber.length() == 10;
        boolean isCheckboxChecked = checkBox.isChecked();

        buttonXacThucSDT.setEnabled(isPhoneNumberValid && isPhoneNumberLengthValid && isCheckboxChecked);
    }
}
