package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.Activity.DanhMuc_CuaHang_Adapter;
import com.example.qlphonggym.Activity.SanPhamCuaHangAdapter;
import com.example.qlphonggym.CSDL.DanhMuc;
import com.example.qlphonggym.CSDL.SanPham;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CuaHang extends AppCompatActivity {

    private RecyclerView recyclerView, recyclerSanPham;
    private DanhMuc_CuaHang_Adapter adapter;
    private SanPhamCuaHangAdapter sanPhamAdapter;
    private List<DanhMuc> danhMucList;
    private List<SanPham> sanPhamList;
    private List<SanPham> filteredSanPhamList;
    private DatabaseReference mDatabase;
    private SearchView searchView;
    private ImageView gioHangImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuahang);

        recyclerView = findViewById(R.id.recyclerDanhMuc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        danhMucList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerSanPham = findViewById(R.id.recyclerSanPham);
        recyclerSanPham.setLayoutManager(new GridLayoutManager(this, 2));

        sanPhamList = new ArrayList<>();
        filteredSanPhamList = new ArrayList<>();

        // Lấy dữ liệu cho Danh Mục
        mDatabase.child("DanhMuc").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                danhMucList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DanhMuc danhMuc = snapshot.getValue(DanhMuc.class);
                        if (danhMuc != null) {
                            danhMucList.add(danhMuc);
                        }
                    }

                    // Cập nhật Adapter cho danh mục và thêm sự kiện click
                    adapter = new DanhMuc_CuaHang_Adapter(CuaHang.this, danhMucList, new DanhMuc_CuaHang_Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(DanhMuc danhMuc) {
                            if (danhMuc == null) {
                                // Nếu không có danh mục được chọn, hiển thị tất cả sản phẩm
                                filterProductsByCategory(null);
                            } else {
                                // Lọc sản phẩm theo danh mục
                                filterProductsByCategory(danhMuc.getId());
                            }
                        }
                    });
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(CuaHang.this, "Không có danh mục nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CuaHang.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        // Lấy dữ liệu cho Sản Phẩm
        mDatabase.child("SanPham").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sanPhamList.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SanPham sanPham = snapshot.getValue(SanPham.class);
                        if (sanPham != null) {
                            sanPhamList.add(sanPham);
                        }
                    }

                    filteredSanPhamList.addAll(sanPhamList); // Hiển thị tất cả sản phẩm ban đầu
                    sanPhamAdapter = new SanPhamCuaHangAdapter(CuaHang.this, filteredSanPhamList);
                    recyclerSanPham.setAdapter(sanPhamAdapter);
                } else {
                    Toast.makeText(CuaHang.this, "Không có sản phẩm nào", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CuaHang.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        // Cập nhật Spinner
        Spinner spinnerLocGia = findViewById(R.id.LocTheoGia);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.LocTheoGia, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocGia.setAdapter(adapterSpinner);

        spinnerLocGia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 0:
                        sanPhamAdapter = new SanPhamCuaHangAdapter(CuaHang.this, sanPhamList);
                        recyclerSanPham.setAdapter(sanPhamAdapter);
                        break;

                    case 1:
                        sortProductsByPrice(true);
                        break;

                    case 2:
                        sortProductsByPrice(false);
                        break;

                    case 3:
                        sortProductsBySales(); // Sắp xếp theo số lượng bán chạy
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Khởi tạo SearchView
        searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false); // Đảm bảo không bị thu gọn khi khởi tạo

        // Thiết lập sự kiện khi người dùng thay đổi từ khóa tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProductsBySearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProductsBySearch(newText);
                return false;
            }
        });

        gioHangImageView = findViewById(R.id.gioHang);
        gioHangImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang Activity giỏ hàng
                Intent intent = new Intent(CuaHang.this, GioHangActivity.class);
                startActivity(intent);
            }
        });
    }

    // Phương thức lọc sản phẩm theo danh mục
    private void filterProductsByCategory(String danhMucId) {
        if (danhMucId == null || danhMucId.isEmpty()) {
            filteredSanPhamList.clear();
            filteredSanPhamList.addAll(sanPhamList);
        } else {
            filteredSanPhamList.clear();
            for (SanPham sanPham : sanPhamList) {
                if (sanPham.getDanhMucId().equals(danhMucId)) {
                    filteredSanPhamList.add(sanPham);
                }
            }
        }
        sanPhamAdapter.notifyDataSetChanged();
    }

    // Phương thức sắp xếp sản phẩm theo giá
    private void sortProductsByPrice(boolean isAscending) {
        Collections.sort(filteredSanPhamList, new Comparator<SanPham>() {
            @Override
            public int compare(SanPham o1, SanPham o2) {
                int price1 = o1.getGiaSanPhamInt();
                int price2 = o2.getGiaSanPhamInt();
                if (isAscending) {
                    return Integer.compare(price1, price2);
                } else {
                    return Integer.compare(price2, price1);
                }
            }
        });
        sanPhamAdapter.notifyDataSetChanged();
    }

    // Phương thức sắp xếp sản phẩm theo số lượng bán (SoLuongNhap - SoLuongConLai)
    private void sortProductsBySales() {
        Collections.sort(filteredSanPhamList, new Comparator<SanPham>() {
            @Override
            public int compare(SanPham o1, SanPham o2) {
                int sales1 = o1.getSoLuongNhap() - o1.getSoLuongConLai();
                int sales2 = o2.getSoLuongNhap() - o2.getSoLuongConLai();

                return Integer.compare(sales2, sales1); // Sắp xếp từ cao đến thấp
            }
        });
        sanPhamAdapter.notifyDataSetChanged();
    }

    // Phương thức lọc sản phẩm theo từ khóa tìm kiếm
    private void filterProductsBySearch(String query) {
        filteredSanPhamList.clear();
        for (SanPham sanPham : sanPhamList) {
            if (sanPham.getTenSanPham().toLowerCase().contains(query.toLowerCase())) {
                filteredSanPhamList.add(sanPham);
            }
        }
        sanPhamAdapter.notifyDataSetChanged();
    }
}
