package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


public class XacThucOTP_DienThoai extends AppCompatActivity {

    private EditText txtOTP;
    private Button buttonXacThucOTP;
    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xac_thuc_otp_dien_thoai);

        txtOTP = findViewById(R.id.txtOTP);
        buttonXacThucOTP = findViewById(R.id.buttonXacThucOTP);
        mAuth = FirebaseAuth.getInstance();

        // Lấy số điện thoại và mã xác minh từ intent
        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        verificationId = getIntent().getStringExtra("VERIFICATION_ID");

        // Kiểm tra khi người dùng nhập OTP
        buttonXacThucOTP.setOnClickListener(v -> {
            String otp = txtOTP.getText().toString();
            if (otp.isEmpty() || otp.length() < 6) {
                Toast.makeText(XacThucOTP_DienThoai.this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
            } else {
                // Xác thực OTP
                verifyOTP(verificationId, otp);
            }
        });

        // Đảm bảo mã OTP có đủ 6 ký tự
        txtOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    buttonXacThucOTP.setEnabled(true);  // Kích hoạt nút xác thực khi có đủ 6 ký tự
                } else {
                    buttonXacThucOTP.setEnabled(false);  // Nếu thiếu ký tự, nút xác thực không hoạt động
                }
            }
        });
    }

    // Xác thực mã OTP
    private void verifyOTP(String verificationId, String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                // Chuyển sang trang đăng ký nếu OTP chính xác
                Intent intent = new Intent(XacThucOTP_DienThoai.this, dangKy.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(XacThucOTP_DienThoai.this, "Xác thực OTP thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
