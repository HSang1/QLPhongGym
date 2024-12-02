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

import com.example.qlphonggym.CSDL.DatPT;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ThemDatPT_admin extends AppCompatActivity {

    private EditText edtThanhPho, edtDiaDiem;
    private Spinner spinnerPT;
    private ImageView imgDatPT;
    private Button btnChonAnhPT, btnThemPT;
    private Uri imageUri;

    // Các CheckBox để chọn ngày
    private CheckBox checkboxMonday, checkboxTuesday, checkboxWednesday, checkboxThursday,
            checkboxFriday, checkboxSaturday, checkboxSunday;

    // Các CheckBox để chọn buổi
    private CheckBox checkboxMorning, checkboxAfternoon, checkboxEvening, checkboxNight;

    private DatabaseReference dbRefPT, dbRefDatPT;
    private FirebaseStorage firebaseStorage;

    // Khai báo ActivityResultLauncher
    private final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    imageUri = uri;
                    imgDatPT.setVisibility(View.VISIBLE);
                    imgDatPT.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.themdatpt_admin);

        // Ánh xạ CheckBox ngày
        checkboxMonday = findViewById(R.id.checkboxMonday);
        checkboxTuesday = findViewById(R.id.checkboxTuesday);
        checkboxWednesday = findViewById(R.id.checkboxWednesday);
        checkboxThursday = findViewById(R.id.checkboxThursday);
        checkboxFriday = findViewById(R.id.checkboxFriday);
        checkboxSaturday = findViewById(R.id.checkboxSaturday);
        checkboxSunday = findViewById(R.id.checkboxSunday);

        // Ánh xạ CheckBox buổi
        checkboxMorning = findViewById(R.id.checkboxMorning);
        checkboxAfternoon = findViewById(R.id.checkboxAfternoon);
        checkboxEvening = findViewById(R.id.checkboxEvening);
        checkboxNight = findViewById(R.id.checkboxNight);


        edtThanhPho = findViewById(R.id.edtThanhPho);
        edtDiaDiem = findViewById(R.id.edtDiaDiem);
        spinnerPT = findViewById(R.id.spinnerPT);
        imgDatPT = findViewById(R.id.imgDatPT);
        btnChonAnhPT = findViewById(R.id.btnChonAnhPT);
        btnThemPT = findViewById(R.id.btnThemPT);

        // Khởi tạo Firebase
        dbRefPT = FirebaseDatabase.getInstance().getReference("PT");
        dbRefDatPT = FirebaseDatabase.getInstance().getReference("DatPT");
        firebaseStorage = FirebaseStorage.getInstance(); // Khởi tạo Firebase Storage

        // Kiểm tra quyền truy cập bộ nhớ
        checkStoragePermission();

        // Lấy danh mục từ Firebase và điền vào Spinner
        getPTFromFirebase();

        // Chọn ảnh sản phẩm
        btnChonAnhPT.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            // Sử dụng ActivityResultLauncher thay cho startActivityForResult
            selectImage.launch(intent);
        });

        // Thêm sản phẩm
        btnThemPT.setOnClickListener(v -> {
            String thanhPho = edtThanhPho.getText().toString().trim();
            String diaDiem = edtDiaDiem.getText().toString().trim();
            String pTId = spinnerPT.getSelectedItem().toString(); // Lấy tên danh mục từ Spinner

            // Tạo danh sách ngày được chọn
            List<String> days = new ArrayList<>();
            if (checkboxMonday.isChecked()) days.add("Monday");
            if (checkboxTuesday.isChecked()) days.add("Tuesday");
            if (checkboxWednesday.isChecked()) days.add("Wednesday");
            if (checkboxThursday.isChecked()) days.add("Thursday");
            if (checkboxFriday.isChecked()) days.add("Friday");
            if (checkboxSaturday.isChecked()) days.add("Saturday");
            if (checkboxSunday.isChecked()) days.add("Sunday");

            // Tạo danh sách buổi được chọn
            List<String> sessions = new ArrayList<>();
            if (checkboxMorning.isChecked()) sessions.add("Morning");
            if (checkboxAfternoon.isChecked()) sessions.add("Afternoon");
            if (checkboxEvening.isChecked()) sessions.add("Evening");
            if (checkboxNight.isChecked()) sessions.add("Night");

            if (thanhPho.isEmpty() || diaDiem.isEmpty() || days.isEmpty() || sessions.isEmpty()) {
                Toast.makeText(ThemDatPT_admin.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
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
                        String datPTId = dbRefDatPT.push().getKey();
                        DatPT datPT = new DatPT(datPTId, thanhPho, diaDiem, imageUrl, pTId, days, sessions);

                        // Thêm sản phẩm vào Firebase Database
                        if (datPTId != null) {
                            dbRefDatPT.child(datPTId).setValue(datPT)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ThemDatPT_admin.this, "Thêm PT thành công", Toast.LENGTH_SHORT).show();
                                            finish(); // Trở về màn hình trước
                                        } else {
                                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                            Toast.makeText(ThemDatPT_admin.this, "Lỗi khi thêm PT: " + errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(ThemDatPT_admin.this, "Tải ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getPTFromFirebase() {
        dbRefPT.get().addOnSuccessListener(dataSnapshot -> {
            ArrayList<String> pTList = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                // Lấy tên danh mục từ trường "tenPT"
                String pTName = snapshot.child("tenPT").getValue(String.class);
                if (pTName != null) {
                    pTList.add(pTName);
                }
            }

            // Kiểm tra xem danhMucList có dữ liệu hay không
            if (pTList.isEmpty()) {
                Toast.makeText(ThemDatPT_admin.this, "Danh mục trống!", Toast.LENGTH_SHORT).show();
            } else {
                // Điền dữ liệu vào Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ThemDatPT_admin.this, android.R.layout.simple_spinner_item, pTList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPT.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ThemDatPT_admin.this, "Lỗi khi lấy danh mục", Toast.LENGTH_SHORT).show();
        });
    }
}