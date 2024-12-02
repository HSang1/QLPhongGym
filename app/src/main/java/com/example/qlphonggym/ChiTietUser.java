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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChiTietUser extends AppCompatActivity {

    private EditText etFullName, etUsername, etPhoneNumber, etEmail;
    private Spinner spinnerCity;
    private RadioGroup roleGroup;
    private RadioButton rbAdmin, rbUser;
    private DatabaseReference dbRefUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietuser);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        spinnerCity = findViewById(R.id.spinnerCity);
        roleGroup = findViewById(R.id.roleGroup);
        rbAdmin = findViewById(R.id.rbAdmin);
        rbUser = findViewById(R.id.rbUser);

        etEmail.setEnabled(false);
        etUsername.setEnabled(false);

        dbRefUser = FirebaseDatabase.getInstance().getReference("users");

        String userId = getIntent().getStringExtra("USER_ID");
        getUserInfo(userId);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vietnam_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveUserInfo(userId));
    }

    private void getUserInfo(String userId) {
        dbRefUser.orderByChild("username").equalTo(userId).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    getUserInfoByEmail(email);
                }
            } else {
                Toast.makeText(ChiTietUser.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfoByEmail(String email) {
        dbRefUser.orderByChild("email").equalTo(email).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String fullName = userSnapshot.child("fullName").getValue(String.class);
                    String username = userSnapshot.child("username").getValue(String.class);
                    String phoneNumber = userSnapshot.child("phoneNumber").getValue(String.class);
                    String city = userSnapshot.child("city").getValue(String.class);
                    String role = userSnapshot.child("role").getValue(String.class);

                    etFullName.setText(fullName != null ? fullName : "N/A");
                    etUsername.setText(username != null ? username : "N/A");
                    etPhoneNumber.setText(phoneNumber != null ? phoneNumber : "N/A");
                    etEmail.setText(email != null ? email : "N/A");

                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerCity.getAdapter();
                    int position = adapter.getPosition(city);

                    if (position != -1) {
                        spinnerCity.setSelection(position);
                    } else {
                        spinnerCity.setSelection(0);
                    }

                    if ("Admin".equals(role)) {
                        rbAdmin.setChecked(true);
                    } else {
                        rbUser.setChecked(true);
                    }
                }
            } else {
                Toast.makeText(ChiTietUser.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveUserInfo(String userId) {
        String fullName = etFullName.getText().toString();
        String username = etUsername.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String email = etEmail.getText().toString();
        String city = spinnerCity.getSelectedItem().toString();
        String role = roleGroup.getCheckedRadioButtonId() == R.id.rbAdmin ? "Admin" : "User";

        dbRefUser.orderByChild("username").equalTo(userId).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userEmail = userSnapshot.child("email").getValue(String.class);

                    dbRefUser.orderByChild("email").equalTo(userEmail).get().addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            for (DataSnapshot user : snapshot.getChildren()) {
                                String userIdToUpdate = user.getKey();

                                dbRefUser.child(userIdToUpdate).child("fullName").setValue(fullName);
                                dbRefUser.child(userIdToUpdate).child("username").setValue(username);
                                dbRefUser.child(userIdToUpdate).child("phoneNumber").setValue(phoneNumber);
                                dbRefUser.child(userIdToUpdate).child("city").setValue(city);
                                dbRefUser.child(userIdToUpdate).child("role").setValue(role);

                                Toast.makeText(ChiTietUser.this, "Đã lưu thông tin chỉnh sửa!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(ChiTietUser.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
