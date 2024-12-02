package com.example.qlphonggym;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.ThongKeAdapter;
import com.example.qlphonggym.CSDL.SanPham;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ThongKe extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ThongKeAdapter thongKeAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference doanhThuRef;
    private TextView doanhThuTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thongke);

        // Khởi tạo RecyclerView
        recyclerView = findViewById(R.id.recyclerViewThongKe);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Kết nối với Firebase (node "SanPham")
        mDatabase = FirebaseDatabase.getInstance().getReference("SanPham");


        doanhThuTextView = findViewById(R.id.DoanhThu);
        doanhThuRef = FirebaseDatabase.getInstance().getReference("DoanhThu").child("doanhthu");

        // Danh sách các sản phẩm
        List<SanPham> sanPhamList = new ArrayList<>();
        thongKeAdapter = new ThongKeAdapter(sanPhamList);
        recyclerView.setAdapter(thongKeAdapter);


        // Lấy dữ liệu từ Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sanPhamList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SanPham sanPham = snapshot.getValue(SanPham.class);
                    if (sanPham != null) {
                        sanPhamList.add(sanPham);
                    }
                }
                thongKeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.w("ThongKe", "loadPost:onCancelled", databaseError.toException());
            }
        });

        doanhThuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu doanh thu từ Firebase
                    Long doanhThu = dataSnapshot.getValue(Long.class);
                    Log.d("ThongKe", "Doanh thu nhận được: " + doanhThu);  // Thêm log để kiểm tra
                    if (doanhThu != null) {
                        // Cập nhật TextView với giá trị doanh thu
                        doanhThuTextView.setText("Doanh thu: " + doanhThu + " VND");
                    } else {
                        // Nếu giá trị doanh thu là null, bạn có thể xử lý theo cách khác
                        doanhThuTextView.setText("Doanh thu: 0 VND");
                    }
                } else {
                    doanhThuTextView.setText("Doanh thu không có sẵn");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi khi có sự cố với việc lấy dữ liệu
                Toast.makeText(ThongKe.this, "Lỗi khi lấy dữ liệu doanh thu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
