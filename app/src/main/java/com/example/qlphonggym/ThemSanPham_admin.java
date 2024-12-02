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

import com.example.qlphonggym.CSDL.SanPham;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ThemSanPham_admin extends AppCompatActivity {

    private EditText edtTenSanPham, edtGiaSanPham, edtMoTaSanPham, edtSoLuongNhap;
    private Spinner spinnerDanhMuc;
    private ImageView imgSanPham;
    private Button btnChonAnh, btnThemSanPham;
    private Uri imageUri;
    private DatabaseReference dbRefDanhMuc, dbRefSanPham;
    private FirebaseStorage firebaseStorage;

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
        setContentView(R.layout.themsanpham_admin);

        edtTenSanPham = findViewById(R.id.edtTenSanPham);
        edtGiaSanPham = findViewById(R.id.edtGiaSanPham);
        edtMoTaSanPham = findViewById(R.id.edtMoTaSanPham);
        edtSoLuongNhap = findViewById(R.id.edtSoLuongNhap);
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        imgSanPham = findViewById(R.id.imgSanPham);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnThemSanPham = findViewById(R.id.btnThemSanPham);

        // Khởi tạo Firebase
        dbRefDanhMuc = FirebaseDatabase.getInstance().getReference("DanhMuc");
        dbRefSanPham = FirebaseDatabase.getInstance().getReference("SanPham");
        firebaseStorage = FirebaseStorage.getInstance();

        // Kiểm tra quyền truy cập bộ nhớ
        checkStoragePermission();

        // Lấy danh mục từ Firebase và điền vào Spinner
        getDanhMucFromFirebase();

        // Chọn ảnh sản phẩm
        btnChonAnh.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            selectImage.launch(intent);
        });

        // Thêm sản phẩm
        btnThemSanPham.setOnClickListener(v -> {
            String tenSanPham = edtTenSanPham.getText().toString().trim();
            String giaSanPham = edtGiaSanPham.getText().toString().trim();
            String moTaSanPham = edtMoTaSanPham.getText().toString().trim();
            String soLuongNhapStr = edtSoLuongNhap.getText().toString().trim();
            String danhMucName = spinnerDanhMuc.getSelectedItem().toString(); // Lấy tên danh mục từ Spinner

            // Kiểm tra xem tất cả các trường thông tin đã được điền đầy đủ chưa
            if (tenSanPham.isEmpty() || giaSanPham.isEmpty() || moTaSanPham.isEmpty() || soLuongNhapStr.isEmpty()) {
                Toast.makeText(ThemSanPham_admin.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển đổi số lượng nhập từ String sang int
            int soLuongNhap = Integer.parseInt(soLuongNhapStr);
            int soLuongConLai = soLuongNhap; // Số lượng còn lại bằng số lượng nhập

            // Lấy danh mục ID từ danh sách danhMucIdList
            ArrayList<String> danhMucIdList = (ArrayList<String>) spinnerDanhMuc.getTag(); // Lấy danh sách ID
            int index = spinnerDanhMuc.getSelectedItemPosition();  // Lấy vị trí của danh mục trong Spinner
            String danhMucIdFromDB = danhMucIdList.get(index);  // Lấy ID tương ứng với tên danh mục

            int motSao = 0, haiSao = 0, baSao = 0, bonSao = 0, namSao = 0; // Các biến cho đánh giá sao
            double diemTrungBinh = 0.0;


            // Kiểm tra xem có chọn ảnh không
            if (danhMucIdFromDB != null) {
                if (imageUri != null) {
                    // Tải ảnh lên Firebase Storage
                    StorageReference storageRef = firebaseStorage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");
                    UploadTask uploadTask = storageRef.putFile(imageUri);

                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        // Lấy URL của ảnh đã upload
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            // Tạo sản phẩm và thêm vào Firebase
                            String sanPhamId = dbRefSanPham.push().getKey();
                            SanPham sanPham = new SanPham(sanPhamId, tenSanPham, giaSanPham, moTaSanPham,
                                    imageUrl, danhMucIdFromDB, soLuongNhap, soLuongConLai, motSao, haiSao, baSao, bonSao, namSao, diemTrungBinh);

                            if (sanPhamId != null) {
                                dbRefSanPham.child(sanPhamId).setValue(sanPham)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(ThemSanPham_admin.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                                finish(); // Trở về màn hình trước
                                            } else {
                                                String errorMessage = task1.getException() != null ? task1.getException().getMessage() : "Unknown error";
                                                Toast.makeText(ThemSanPham_admin.this, "Lỗi khi thêm sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(ThemSanPham_admin.this, "Tải ảnh thất bại: "
                                + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    danhMucList.add(danhMucName);  // Thêm tên danh mục vào list
                    danhMucIdList.add(danhMucId);  // Thêm ID danh mục vào list
                }
            }

            // Thiết lập spinner cho danh mục
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, danhMucList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDanhMuc.setAdapter(adapter);

            // Lưu danh mục ID vào tag của spinner để sau này lấy ra
            spinnerDanhMuc.setTag(danhMucIdList);
        });
    }
}
