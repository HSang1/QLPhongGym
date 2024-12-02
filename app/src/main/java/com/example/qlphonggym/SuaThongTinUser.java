package com.example.qlphonggym;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.CSDL_Users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SuaThongTinUser extends AppCompatActivity {

    private EditText edtFullName, edtUsername, edtPhoneNumber, edtEmail;
    private Spinner spinnerCity; // Chúng ta sử dụng Spinner để chọn Thành Phố
    private CheckBox checkboxAdmin, checkboxUser;
    private Button btnSave;
    private DatabaseReference dbRefUsers;
    private String userId;
    private CSDL_Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suathongtinuser_admin);


        edtFullName = findViewById(R.id.edtFullName);
        edtUsername = findViewById(R.id.edtUsername);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtEmail = findViewById(R.id.edtEmail);
        spinnerCity = findViewById(R.id.spinnerCity);
        checkboxAdmin = findViewById(R.id.checkboxAdmin);
        checkboxUser = findViewById(R.id.checkboxUser);
        btnSave = findViewById(R.id.btnSave);


        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");


        userId = getIntent().getStringExtra("USER_ID");

        getUserInfo(userId);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vietnam_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);


        btnSave.setOnClickListener(v -> {
            String fullName = edtFullName.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String city = spinnerCity.getSelectedItem().toString();

            if (!checkboxAdmin.isChecked() && !checkboxUser.isChecked()) {
                Toast.makeText(SuaThongTinUser.this, "Vui lòng chọn vai trò", Toast.LENGTH_SHORT).show();
                return;
            }


            String role = "";
            if (checkboxAdmin.isChecked()) {
                role = "Admin";
            } else if (checkboxUser.isChecked()) {
                role = "User";
            }

            if (fullName.isEmpty() || username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || city.isEmpty()) {
                Toast.makeText(SuaThongTinUser.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }


            user = new CSDL_Users(username, phoneNumber, email, city, fullName, role);


            dbRefUsers.child(userId).setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SuaThongTinUser.this, "Cập nhật thông tin người dùng thành công", Toast.LENGTH_SHORT).show();
                            finish(); // Trở về màn hình trước
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Có lỗi xảy ra";
                            Toast.makeText(SuaThongTinUser.this, "Lỗi khi cập nhật thông tin người dùng: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void getUserInfo(String userId) {
        dbRefUsers.child(userId).get().addOnSuccessListener(dataSnapshot -> {
            user = dataSnapshot.getValue(CSDL_Users.class);
            if (user != null) {
                edtFullName.setText(user.getFullName());
                edtUsername.setText(user.getUsername());
                edtPhoneNumber.setText(user.getPhoneNumber());
                edtEmail.setText(user.getEmail());


                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerCity.getAdapter();
                int cityPosition = adapter.getPosition(user.getCity());
                spinnerCity.setSelection(cityPosition);


                if ("Admin".equals(user.getRole())) {
                    checkboxAdmin.setChecked(true);
                } else if ("User".equals(user.getRole())) {
                    checkboxUser.setChecked(true);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SuaThongTinUser.this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        });
    }
}
