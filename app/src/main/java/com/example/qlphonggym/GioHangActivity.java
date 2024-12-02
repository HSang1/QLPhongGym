package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.GioHangAdapter;
import com.example.qlphonggym.CSDL.GioHang;
import com.example.qlphonggym.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GioHangActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GioHangAdapter gioHangAdapter;
    private List<GioHang> gioHangList;
    private DatabaseReference gioHangRef;
    private TextView tvTongTien;

    int tongTien = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giohang);

        // Khởi tạo RecyclerView, adapter và dữ liệu
        recyclerView = findViewById(R.id.recycler_giohang);
        tvTongTien = findViewById(R.id.tongtien);
        Button btnThanhToan = findViewById(R.id.btnThanhToan);
        gioHangList = new ArrayList<>();
        gioHangAdapter = new GioHangAdapter(this, gioHangList);

        // Thiết lập LayoutManager cho RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gioHangAdapter);

        // Lấy dữ liệu giỏ hàng từ Firebase
        gioHangRef = FirebaseDatabase.getInstance().getReference("GioHang");

        // Lọc giỏ hàng theo username của người dùng
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Lấy UID của người dùng hiện tại

        if (currentUserId != null) {
            getUsername(currentUserId);
        } else {
            Toast.makeText(GioHangActivity.this, "Không tìm thấy người dùng đang đăng nhập", Toast.LENGTH_SHORT).show();
        }


        btnThanhToan.setOnClickListener(v -> {

            Intent intent = new Intent(GioHangActivity.this, ThanhToan.class);
            intent.putExtra("total", tongTien);
            startActivity(intent);

        });
    }

    // Phương thức lấy username từ Firebase theo UID
    private void getUsername(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentUsername = dataSnapshot.getValue(String.class); // Lấy username từ cơ sở dữ liệu

                if (currentUsername == null) {
                    Toast.makeText(GioHangActivity.this, "Không tìm thấy username của người dùng", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sau khi có username, lọc giỏ hàng theo username
                filterGioHangByUsername(currentUsername);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GioHangActivity.this, "Lỗi khi lấy username: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức lọc giỏ hàng theo username
    private void filterGioHangByUsername(String currentUsername) {
        gioHangRef.orderByChild("username").equalTo(currentUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gioHangList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GioHang gioHang = snapshot.getValue(GioHang.class);
                    if (gioHang != null) {
                        gioHangList.add(gioHang);
                    }
                }
                gioHangAdapter.notifyDataSetChanged();
                // Cập nhật tổng tiền khi giỏ hàng đã được tải
                updateTongTien();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GioHangActivity.this, "Lỗi khi lấy dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức tính lại tổng tiền
    public void updateTongTien() {

        tongTien=0;
        for (GioHang gioHang : gioHangList) {
            tongTien += Integer.parseInt(gioHang.getGiaSP()) * Integer.parseInt(gioHang.getSoLuong());
        }
        tvTongTien.setText("Tổng tiền: " + formatPrice(String.valueOf(tongTien)) + " VND");
    }

    // Phương thức định dạng giá
    private String formatPrice(String price) {
        try {
            // Chuyển giá sang long để xử lý
            long priceValue = Long.parseLong(price);

            // Sử dụng NumberFormat để định dạng giá tiền
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
            String formatted = numberFormat.format(priceValue);

            // Thay dấu phẩy bằng khoảng trắng
            return formatted.replace(',', ' ');
        } catch (NumberFormatException e) {
            return price;  // Nếu không thể chuyển đổi, giữ nguyên giá gốc
        }
    }
}
