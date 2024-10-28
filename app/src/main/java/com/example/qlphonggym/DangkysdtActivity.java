package com.example.qlphonggym;

import android.os.Bundle;

import android.content.Intent;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DangkysdtActivity extends AppCompatActivity {

    private EditText txtSDT;
    private CheckBox checkBox;
    private Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.dangkysdt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtSDT = findViewById(R.id.txtSDT);
        checkBox = findViewById(R.id.checkBox);
        button3 = findViewById(R.id.button3);

        txtSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInput();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Sự kiện lắng nghe cho CheckBox
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> validateInput());

        // Thêm sự kiện OnClickListener cho nút Tiếp tục
        button3.setOnClickListener(view -> {
            if (!checkBox.isChecked()) {
                Toast.makeText(this, "Chưa đồng ý điều khoản sử dụng!", Toast.LENGTH_SHORT).show();
            } else if (!txtSDT.getText().toString().matches("^(03[2-9]|07[0|5-9]|08[1-5|8-9]|09[0-4])\\d{7}$")) {
                Toast.makeText(this, "Số điện thoại không đúng định dạng", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu điều kiện thỏa mãn, chuyển sang màn hình đăng ký
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

        button3.setEnabled(isPhoneNumberValid && isPhoneNumberLengthValid && isCheckboxChecked);
    }
}