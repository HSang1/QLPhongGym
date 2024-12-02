package com.example.qlphonggym;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.qlphonggym.CSDL.DatLop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ThemDatLop_admin extends AppCompatActivity {

    private EditText edtTenLopHoc, edtThoiGianBatDau, edtThoiLuong, edtThanhPho, edtDiaDiem;
    private Spinner spinnerLopHoc;
    private ImageView imgDatLop;
    private Button btnChonAnhLop, btnThemLopHoc;
    private Uri imageUri;

    // Các CheckBox để chọn ngày
    private CheckBox checkboxMonday, checkboxTuesday, checkboxWednesday, checkboxThursday,
            checkboxFriday, checkboxSaturday, checkboxSunday;

    private DatabaseReference dbRefLopHoc, dbRefDatLop;
    private FirebaseStorage firebaseStorage;

    // Khai báo ActivityResultLauncher
    private final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    imageUri = uri;
                    imgDatLop.setVisibility(View.VISIBLE);
                    imgDatLop.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themdatlop_admin);

        // Ánh xạ CheckBox
        checkboxMonday = findViewById(R.id.checkboxMonday);
        checkboxTuesday = findViewById(R.id.checkboxTuesday);
        checkboxWednesday = findViewById(R.id.checkboxWednesday);
        checkboxThursday = findViewById(R.id.checkboxThursday);
        checkboxFriday = findViewById(R.id.checkboxFriday);
        checkboxSaturday = findViewById(R.id.checkboxSaturday);
        checkboxSunday = findViewById(R.id.checkboxSunday);


        edtTenLopHoc = findViewById(R.id.edtTenLopHoc);
        edtThoiGianBatDau = findViewById(R.id.edtThoiGianBatDau);
        edtThoiLuong = findViewById(R.id.edtThoiLuong);
        edtThanhPho = findViewById(R.id.edtThanhPho);
        edtDiaDiem = findViewById(R.id.edtDiaDiem);
        spinnerLopHoc = findViewById(R.id.spinnerLopHoc);
        imgDatLop = findViewById(R.id.imgDatLop);
        btnChonAnhLop = findViewById(R.id.btnChonAnhLop);
        btnThemLopHoc = findViewById(R.id.btnThemLopHoc);

        // Khởi tạo Firebase
        dbRefLopHoc = FirebaseDatabase.getInstance().getReference("LopHoc");
        dbRefDatLop = FirebaseDatabase.getInstance().getReference("DatLop");
        firebaseStorage = FirebaseStorage.getInstance(); // Khởi tạo Firebase Storage

        // Kiểm tra quyền truy cập bộ nhớ
        checkStoragePermission();

        // Lấy danh mục từ Firebase và điền vào Spinner
        getLopHocFromFirebase();

        // Chọn ảnh sản phẩm
        btnChonAnhLop.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            // Sử dụng ActivityResultLauncher thay cho startActivityForResult
            selectImage.launch(intent);
        });

        // Thêm sản phẩm
        btnThemLopHoc.setOnClickListener(v -> {
            String tenLopHoc = edtTenLopHoc.getText().toString().trim();
            String thoiGianBatDau = edtThoiGianBatDau.getText().toString().trim();
            String thoiLuong = edtThoiLuong.getText().toString().trim();
            String thanhPho = edtThanhPho.getText().toString().trim();
            String diaDiem = edtDiaDiem.getText().toString().trim();
            String lopHocId = spinnerLopHoc.getSelectedItem().toString(); // Lấy tên danh mục từ Spinner

            // Tạo danh sách ngày được chọn
            List<String> days = new ArrayList<>();
            if (checkboxMonday.isChecked()) days.add("Monday");
            if (checkboxTuesday.isChecked()) days.add("Tuesday");
            if (checkboxWednesday.isChecked()) days.add("Wednesday");
            if (checkboxThursday.isChecked()) days.add("Thursday");
            if (checkboxFriday.isChecked()) days.add("Friday");
            if (checkboxSaturday.isChecked()) days.add("Saturday");
            if (checkboxSunday.isChecked()) days.add("Sunday");


            if (tenLopHoc.isEmpty() || thoiGianBatDau.isEmpty() || thoiLuong.isEmpty() || thanhPho.isEmpty() || diaDiem.isEmpty() || days.isEmpty()) {
                Toast.makeText(ThemDatLop_admin.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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
                        String datLopId = dbRefDatLop.push().getKey();
                        DatLop datLop = new DatLop(datLopId, tenLopHoc, thoiGianBatDau, thoiLuong, thanhPho, diaDiem, imageUrl, lopHocId, days);

                        // Thêm sản phẩm vào Firebase Database
                        if (datLopId != null) {
                            dbRefDatLop.child(datLopId).setValue(datLop)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ThemDatLop_admin.this, "Thêm lớp học thành công", Toast.LENGTH_SHORT).show();
                                            finish(); // Trở về màn hình trước
                                        } else {
                                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                            Toast.makeText(ThemDatLop_admin.this, "Lỗi khi thêm lớp học: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(ThemDatLop_admin.this, "Tải ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getLopHocFromFirebase() {
        dbRefLopHoc.get().addOnSuccessListener(dataSnapshot -> {
            ArrayList<String> lopHocList = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                // Lấy tên danh mục từ trường "tenLopHoc"
                String lopHocName = snapshot.child("tenLopHoc").getValue(String.class);
                if (lopHocName != null) {
                    lopHocList.add(lopHocName);
                }
            }

            // Kiểm tra xem danhMucList có dữ liệu hay không
            if (lopHocList.isEmpty()) {
                Toast.makeText(ThemDatLop_admin.this, "Danh mục trống!", Toast.LENGTH_SHORT).show();
            } else {
                // Điền dữ liệu vào Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ThemDatLop_admin.this, android.R.layout.simple_spinner_item, lopHocList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLopHoc.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ThemDatLop_admin.this, "Lỗi khi lấy danh mục", Toast.LENGTH_SHORT).show();
        });
    }
}
