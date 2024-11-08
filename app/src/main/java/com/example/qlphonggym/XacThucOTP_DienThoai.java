package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class XacThucOTP_DienThoai extends AppCompatActivity {

    private static final String TAG = XacThucOTP_DienThoai.class.getName();

    private EditText txtOTP;
    private Button buttonXacNhanOTP;
    private FirebaseAuth mAuth;
    private String verificationId;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xac_thuc_otp_dien_thoai);

        txtOTP = findViewById(R.id.txtOTP);
        buttonXacNhanOTP = findViewById(R.id.buttonXacThucOTP);
        mAuth = FirebaseAuth.getInstance();

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("PHONE_NUMBER");
        verificationId = intent.getStringExtra("VERIFICATION_ID");

        // Xử lý khi nhấn nút xác nhận OTP
        buttonXacNhanOTP.setOnClickListener(view -> {
            String otpCode = txtOTP.getText().toString();
            if (otpCode.isEmpty()) {
                Toast.makeText(XacThucOTP_DienThoai.this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
            } else {
                verifyOTP(otpCode);
            }
        });
    }

    // Xác thực mã OTP
    private void verifyOTP(String otpCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Xác thực thành công, chuyển sang màn hình đăng ký
                        Intent intent = new Intent(XacThucOTP_DienThoai.this, dangKy.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Xác thực thất bại
                        Toast.makeText(XacThucOTP_DienThoai.this, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
