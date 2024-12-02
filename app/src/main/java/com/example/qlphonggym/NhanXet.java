package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.CSDL.CSDL_NhanXet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NhanXet extends AppCompatActivity {

    private EditText etNhanXet;
    private Button btnGuiNhanXet;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nhanxet);

        // Khởi tạo các đối tượng
        etNhanXet = findViewById(R.id.etNhanXet);
        btnGuiNhanXet = findViewById(R.id.btnGuiGopY);

        // Khởi tạo FirebaseAuth và FirebaseDatabase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("NhanXet");  // Tạo nhánh "NhanXet" trong Firebase

        // Xử lý sự kiện khi người dùng nhấn nút gửi nhận xét
        btnGuiNhanXet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin người dùng
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();  // Lấy userId của người dùng

                    // Gọi phương thức getUsername() để lấy username từ Firebase
                    getUsername(userId);
                } else {
                    Toast.makeText(NhanXet.this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUsername(String userId) {
        // Lấy tham chiếu đến user trong Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lấy giá trị username từ DataSnapshot
                String currentUsername = dataSnapshot.getValue(String.class);

                if (currentUsername != null) {
                    String moTa = etNhanXet.getText().toString().trim();

                    // Kiểm tra nếu nhận xét không rỗng
                    if (!moTa.isEmpty()) {
                        // Lấy ngày giờ hiện tại
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String currentDateTime = sdf.format(new Date());

                        // Tạo đối tượng nhận xét từ lớp CSDL_NhanXet
                        CSDL_NhanXet nhanXet = new CSDL_NhanXet(currentUsername, moTa, currentDateTime);

                        // Lưu nhận xét vào Firebase
                        mRef.push().setValue(nhanXet).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(NhanXet.this, "Cảm ơn góp ý của bạn!", Toast.LENGTH_SHORT).show();
                                etNhanXet.setText("");
                                Intent intent = new Intent(NhanXet.this, trangChu.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(NhanXet.this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(NhanXet.this, "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NhanXet.this, "Không tìm thấy username", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(NhanXet.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
