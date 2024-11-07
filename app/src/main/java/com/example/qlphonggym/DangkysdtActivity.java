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
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangkysdt);

        txtSDT = findViewById(R.id.txtSDT);
        checkBox = findViewById(R.id.checkBox);
        buttonXacThucSDT = findViewById(R.id.buttonXacThucSDT);
        mAuth = FirebaseAuth.getInstance();

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

        // Lắng nghe sự kiện CheckBox
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> validateInput());

        // Sự kiện bấm nút xác thực số điện thoại
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

    // Gửi mã OTP
    private void sendVerificationCode(String phoneNumber) {
        // Kiểm tra nếu số điện thoại bắt đầu với "0", thay thế bằng "+84"
        if (phoneNumber.startsWith("0")) {
            phoneNumber = "+84" + phoneNumber.substring(1);  // Bỏ "0" và thay bằng "+84"
        }

        String finalPhoneNumber = phoneNumber;
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber) // Số điện thoại đã được chuẩn hóa
                .setTimeout(60L, TimeUnit.SECONDS) // Thời gian hết hạn
                .setActivity(this) // Hoạt động (Activity cho callback)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        // Xử lý khi xác minh thành công
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // Xử lý khi xác minh thất bại
                        Log.e(TAG, "Verification failed: " + e.getMessage());
                        Toast.makeText(DangkysdtActivity.this, "Xác minh thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        super.onCodeSent(verificationId, token);
                        Intent intent = new Intent(DangkysdtActivity.this, XacThucOTP_DienThoai.class);
                        intent.putExtra("PHONE_NUMBER", finalPhoneNumber);  // Truyền số điện thoại đã được chuẩn hóa
                        intent.putExtra("VERIFICATION_ID", verificationId); // Truyền mã xác minh
                        startActivity(intent);  // Chuyển sang màn hình nhập OTP
                    }
                }) // Callbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


}
