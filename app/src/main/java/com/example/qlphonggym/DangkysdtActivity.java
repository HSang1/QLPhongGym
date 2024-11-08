package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class DangkysdtActivity extends AppCompatActivity {

    private static final String TAG = DangkysdtActivity.class.getName();

    private EditText txtSDT;
    private CheckBox checkBox;
    private Button buttonXacThucSDT;
    private FirebaseAuth mAuth;

    private String verificationId; // Lưu mã xác minh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangkysdt);

        txtSDT = findViewById(R.id.txtSDT);
        checkBox = findViewById(R.id.checkBox);
        buttonXacThucSDT = findViewById(R.id.buttonXacThucSDT);
        mAuth = FirebaseAuth.getInstance();

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
                String phoneNumber = txtSDT.getText().toString();
                sendVerificationCode(phoneNumber);  // Gửi mã OTP đến số điện thoại
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

    private void sendVerificationCode(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            phoneNumber = "+84" + phoneNumber.substring(1);  // Chuyển "0" thành "+84"
        }

        Log.d(TAG, "Phone number to send OTP: " + phoneNumber);

        String finalPhoneNumber = phoneNumber;
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber) // Số điện thoại đã chuẩn hóa
                .setTimeout(60L, TimeUnit.SECONDS) // Thời gian chờ dài hơn nếu cần
                .setActivity(this) // Hoạt động (Activity cho callback)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted: " + phoneAuthCredential);
                        mAuth.signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(DangkysdtActivity.this, task -> {
                                    if (task.isSuccessful()) {
                                        // Thành công, chuyển đến màn hình đăng ký
                                        Intent intent = new Intent(DangkysdtActivity.this, dangKy.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e(TAG, "onVerificationCompleted failed: " + task.getException());
                                        Toast.makeText(DangkysdtActivity.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.e(TAG, "onVerificationFailed: " + e.getMessage());
                        Toast.makeText(DangkysdtActivity.this, "Xác minh thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        Log.d(TAG, "onCodeSent: verificationId = " + verificationId);
                        DangkysdtActivity.this.verificationId = verificationId;
                        Intent intent = new Intent(DangkysdtActivity.this, XacThucOTP_DienThoai.class);
                        intent.putExtra("PHONE_NUMBER", finalPhoneNumber);
                        intent.putExtra("VERIFICATION_ID", verificationId);
                        startActivity(intent);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

}
