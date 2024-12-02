package com.example.qlphonggym;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.qlphonggym.CSDL.SanPham;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class SuaSanPham_admin extends AppCompatActivity {

    private EditText edtTenSanPham, edtGiaSanPham, edtMoTaSanPham, edtSoLuongConLai;
    private Spinner spinnerDanhMuc;
    private ImageView imgSanPham;
    private Button btnChonAnh, btnCapNhatSanPham;
    private Uri imageUri;
    private DatabaseReference dbRefDanhMuc, dbRefSanPham;
    private FirebaseStorage firebaseStorage;
    private SanPham sanPham;

    // Khai báo ActivityResultLauncher
    private final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    imageUri = uri;
                    imgSanPham.setVisibility(View.VISIBLE);
                    imgSanPham.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suasanpham_admin);

        // Lấy ID sản phẩm từ Intent
        String sanPhamId = getIntent().getStringExtra("SANPHAM_ID");

        edtTenSanPham = findViewById(R.id.edtTenSanPham);
        edtGiaSanPham = findViewById(R.id.edtGiaSanPham);
        edtMoTaSanPham = findViewById(R.id.edtMoTaSanPham);
        edtSoLuongConLai = findViewById(R.id.edtSoLuongConLai);
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        imgSanPham = findViewById(R.id.imgSanPham);
        btnChonAnh = findViewById(R.id.btnThayDoiAnh);
        btnCapNhatSanPham = findViewById(R.id.btnCapNhat);

        // Khởi tạo Firebase
        dbRefDanhMuc = FirebaseDatabase.getInstance().getReference("DanhMuc");
        dbRefSanPham = FirebaseDatabase.getInstance().getReference("SanPham");
        firebaseStorage = FirebaseStorage.getInstance();

        // Kiểm tra quyền truy cập bộ nhớ
        checkStoragePermission();

        // Lấy danh mục từ Firebase và điền vào Spinner
        getDanhMucFromFirebase();

        // Lấy thông tin sản phẩm từ Firebase
        getSanPhamInfo(sanPhamId);

        // Chọn ảnh sản phẩm
        btnChonAnh.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            selectImage.launch(intent);
        });

        btnCapNhatSanPham.setOnClickListener(v -> {
            String tenSanPham = edtTenSanPham.getText().toString().trim();
            String giaSanPham = edtGiaSanPham.getText().toString().trim();
            String moTaSanPham = edtMoTaSanPham.getText().toString().trim();
            String soLuongConLaiStr = edtSoLuongConLai.getText().toString().trim();
            String danhMucName = spinnerDanhMuc.getSelectedItem().toString(); // Lấy tên danh mục từ Spinner

            // Kiểm tra xem tất cả các trường thông tin đã được điền đầy đủ chưa
            if (tenSanPham.isEmpty() || giaSanPham.isEmpty() || moTaSanPham.isEmpty() || soLuongConLaiStr.isEmpty()) {
                Toast.makeText(SuaSanPham_admin.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển đổi số lượng còn lại từ String sang int
            int soLuongConLai = Integer.parseInt(soLuongConLaiStr);

            // Lấy số lượng nhập (nếu có)
            int soLuongNhap = sanPham.getSoLuongNhap(); // Số lượng nhập ban đầu

            // Kiểm tra và tính toán lại số lượng nhập
            if (soLuongConLai < sanPham.getSoLuongConLai()) {
                // Nếu số lượng còn lại giảm xuống, không thay đổi số lượng nhập
                // (Điều này có nghĩa là bạn đang bán sản phẩm, và chỉ giảm số lượng còn lại)
                soLuongNhap = sanPham.getSoLuongNhap();
            } else if (soLuongConLai > sanPham.getSoLuongConLai()) {
                // Nếu số lượng còn lại tăng lên, tăng số lượng nhập tương ứng với số lượng tăng thêm
                int soLuongThem = soLuongConLai - sanPham.getSoLuongConLai(); // Số lượng thêm vào kho
                soLuongNhap += soLuongThem; // Tăng số lượng nhập
            }

            // Lấy danh mục ID từ danh sách danhMucIdList
            ArrayList<String> danhMucIdList = (ArrayList<String>) spinnerDanhMuc.getTag(); // Lấy danh sách ID
            int index = spinnerDanhMuc.getSelectedItemPosition();  // Lấy vị trí của danh mục trong Spinner
            String danhMucIdFromDB = danhMucIdList.get(index);  // Lấy ID tương ứng với tên danh mục

            if (danhMucIdFromDB != null) {
                if (imageUri != null) {
                    // Tải ảnh lên Firebase Storage
                    StorageReference storageRef = firebaseStorage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");
                    UploadTask uploadTask = storageRef.putFile(imageUri);

                    int finalSoLuongNhap = soLuongNhap;
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Lấy URL của ảnh đã upload
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            // Cập nhật thông tin sản phẩm
                            sanPham.setTenSanPham(tenSanPham);
                            sanPham.setGiaSanPham(giaSanPham);
                            sanPham.setMoTaSanPham(moTaSanPham);
                            sanPham.setSoLuongConLai(soLuongConLai);
                            sanPham.setSoLuongNhap(finalSoLuongNhap); // Cập nhật số lượng nhập
                            sanPham.setDanhMucId(danhMucIdFromDB);
                            sanPham.setImageUrl(imageUrl);

                            // Cập nhật sản phẩm vào Firebase Database
                            dbRefSanPham.child(sanPham.getIdSanPham()).setValue(sanPham)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(SuaSanPham_admin.this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                            finish(); // Trở về màn hình trước
                                        } else {
                                            String errorMessage = task1.getException() != null ? task1.getException().getMessage() : "Unknown error";
                                            Toast.makeText(SuaSanPham_admin.this, "Lỗi khi cập nhật sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(SuaSanPham_admin.this, "Tải ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Nếu không có ảnh mới, chỉ cập nhật thông tin khác
                    sanPham.setTenSanPham(tenSanPham);
                    sanPham.setGiaSanPham(giaSanPham);
                    sanPham.setMoTaSanPham(moTaSanPham);
                    sanPham.setSoLuongConLai(soLuongConLai);
                    sanPham.setSoLuongNhap(soLuongNhap); // Cập nhật số lượng nhập
                    sanPham.setDanhMucId(danhMucIdFromDB);

                    dbRefSanPham.child(sanPham.getIdSanPham()).setValue(sanPham)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(SuaSanPham_admin.this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                    finish(); // Trở về màn hình trước
                                } else {
                                    String errorMessage = task1.getException() != null ? task1.getException().getMessage() : "Unknown error";
                                    Toast.makeText(SuaSanPham_admin.this, "Lỗi khi cập nhật sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


    }



    // Phương thức kiểm tra quyền truy cập bộ nhớ
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void getSanPhamInfo(String sanPhamId) {
        dbRefSanPham.child(sanPhamId).get().addOnSuccessListener(dataSnapshot -> {
            sanPham = dataSnapshot.getValue(SanPham.class);
            if (sanPham != null) {
                edtTenSanPham.setText(sanPham.getTenSanPham());
                edtGiaSanPham.setText(sanPham.getGiaSanPham());
                edtMoTaSanPham.setText(sanPham.getMoTaSanPham());
                edtSoLuongConLai.setText(String.valueOf(sanPham.getSoLuongConLai()));

                // Hiển thị ảnh sản phẩm nếu có
                if (sanPham.getImageUrl() != null && !sanPham.getImageUrl().isEmpty()) {
                    imgSanPham.setVisibility(View.VISIBLE);
                    Glide.with(SuaSanPham_admin.this)  // Sử dụng Glide để tải ảnh
                            .load(sanPham.getImageUrl())   // Link ảnh từ Firebase Storage
                            .into(imgSanPham);
                }

                // Điền danh mục vào Spinner
                getDanhMucFromFirebase();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SuaSanPham_admin.this, "Lỗi khi lấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
        });
    }

    // Lấy danh mục từ Firebase
    private void getDanhMucFromFirebase() {
        dbRefDanhMuc.get().addOnSuccessListener(dataSnapshot -> {
            ArrayList<String> danhMucList = new ArrayList<>();
            ArrayList<String> danhMucIdList = new ArrayList<>();

            // Lấy tên và id danh mục từ Firebase
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String danhMucName = snapshot.child("tenDanhMuc").getValue(String.class);
                String danhMucId = snapshot.getKey();  // Lấy ID của danh mục
                if (danhMucName != null && danhMucId != null) {
                    danhMucList.add(danhMucName);  // Thêm tên danh mục vào danh sách
                    danhMucIdList.add(danhMucId);  // Thêm ID vào danh sách
                }
            }

            // Kiểm tra xem danhMucList có dữ liệu hay không
            if (danhMucList.isEmpty()) {
                Toast.makeText(SuaSanPham_admin.this, "Danh mục trống!", Toast.LENGTH_SHORT).show();
            } else {
                // Điền dữ liệu vào Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SuaSanPham_admin.this, android.R.layout.simple_spinner_item, danhMucList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDanhMuc.setAdapter(adapter);

                // Lưu danh sách ID của danh mục vào Spinner (sử dụng danhMucIdList)
                spinnerDanhMuc.setTag(danhMucIdList);  // Lưu danh sách ID vào tag của Spinner
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(SuaSanPham_admin.this, "Lỗi khi lấy danh mục", Toast.LENGTH_SHORT).show();
        });
    }
}
