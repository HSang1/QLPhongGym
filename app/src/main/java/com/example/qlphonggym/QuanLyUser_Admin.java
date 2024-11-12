package com.example.qlphonggym;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.UserAdapter;
import com.example.qlphonggym.CSDL.CSDL_Users;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuanLyUser_Admin extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<CSDL_Users> userList;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quanlyuser_admin);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CSDL_Users user = snapshot.getValue(CSDL_Users.class);
                    userList.add(user);
                }
                userAdapter = new UserAdapter(QuanLyUser_Admin.this, userList);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi trong việc đọc dữ liệu
                Toast.makeText(QuanLyUser_Admin.this, "Lỗi khi lấy dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Nút thêm người dùng
        Button btnAddUser = findViewById(R.id.btnAddUser);
        btnAddUser.setOnClickListener(v -> {
            // Chuyển đến màn hình thêm người dùng
            // Intent intent = new Intent(QuanLyUserActivity.this, AddUserActivity.class);
            // startActivity(intent);
        });
    }
}