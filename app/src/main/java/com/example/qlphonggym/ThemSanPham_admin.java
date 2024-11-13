package com.example.qlphonggym;

import android.Manifest;
import android.content.ContentResolver;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ThemSanPham_admin extends AppCompatActivity {

    private EditText edtTenSanPham, edtGiaSanPham, edtMoTaSanPham;
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
        spinnerDanhMuc = findViewById(R.id.spinnerDanhMuc);
        imgSanPham = findViewById(R.id.imgSanPham);
        btnChonAnh = findViewById(R.id.btnChonAnh);
        btnThemSanPham = findViewById(R.id.btnThemSanPham);

        // Khởi tạo Firebase
        dbRefDanhMuc = FirebaseDatabase.getInstance().getReference("DanhMuc");
        dbRefSanPham = FirebaseDatabase.getInstance().getReference("SanPham");
        firebaseStorage = FirebaseStorage.getInstance(); // Khởi tạo Firebase Storage

        // Kiểm tra quyền truy cập bộ nhớ
        checkStoragePermission();

        // Lấy danh mục từ Firebase và điền vào Spinner
        getDanhMucFromFirebase();

        // Chọn ảnh sản phẩm
        btnChonAnh.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            // Sử dụng ActivityResultLauncher thay cho startActivityForResult
            selectImage.launch(intent);
        });

        // Thêm sản phẩm
        btnThemSanPham.setOnClickListener(v -> {
            String tenSanPham = edtTenSanPham.getText().toString().trim();
            String giaSanPham = edtGiaSanPham.getText().toString().trim();
            String moTaSanPham = edtMoTaSanPham.getText().toString().trim();
            String danhMucId = spinnerDanhMuc.getSelectedItem().toString(); // Lấy tên danh mục từ Spinner

            if (tenSanPham.isEmpty() || giaSanPham.isEmpty() || moTaSanPham.isEmpty()) {
                Toast.makeText(ThemSanPham_admin.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                // Tải ảnh lên Firebase Storage
                StorageReference storageRef = firebaseStorage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");
                UploadTask uploadTask = storageRef.putFile(imageUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của ảnh đã upload
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // Tạo đối tượng sản phẩm
                        String sanPhamId = dbRefSanPham.push().getKey();
                        SanPham sanPham = new SanPham(sanPhamId, tenSanPham, giaSanPham, moTaSanPham, imageUrl, danhMucId);

                        // Thêm sản phẩm vào Firebase Database
                        if (sanPhamId != null) {
                            dbRefSanPham.child(sanPhamId).setValue(sanPham)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ThemSanPham_admin.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                            finish(); // Trở về màn hình trước
                                        } else {
                                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                            Toast.makeText(ThemSanPham_admin.this, "Lỗi khi thêm sản phẩm: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(ThemSanPham_admin.this, "Tải ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

    private void getDanhMucFromFirebase() {
        dbRefDanhMuc.get().addOnSuccessListener(dataSnapshot -> {
            ArrayList<String> danhMucList = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                // Lấy tên danh mục từ trường "tenDanhMuc"
                String danhMucName = snapshot.child("tenDanhMuc").getValue(String.class);
                if (danhMucName != null) {
                    danhMucList.add(danhMucName);
                }
            }

            // Kiểm tra xem danhMucList có dữ liệu hay không
            if (danhMucList.isEmpty()) {
                Toast.makeText(ThemSanPham_admin.this, "Danh mục trống!", Toast.LENGTH_SHORT).show();
            } else {
                // Điền dữ liệu vào Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ThemSanPham_admin.this, android.R.layout.simple_spinner_item, danhMucList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDanhMuc.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ThemSanPham_admin.this, "Lỗi khi lấy danh mục", Toast.LENGTH_SHORT).show();
        });
    }
}
