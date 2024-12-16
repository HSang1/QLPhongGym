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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.qlphonggym.CSDL.DatPT;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ThemDatPT_admin extends AppCompatActivity {

    private Spinner spinnerPT, spinnerThanhPho, spinnerDiaDiem;
    private ArrayAdapter<String> diaDiemAdapter;
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

    // Khai báo danh sách địa điểm theo thành phố
    private final HashMap<String, List<String>> diaDiemMap = new HashMap<>();

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

        spinnerThanhPho = findViewById(R.id.spinnerThanhPho);
        spinnerDiaDiem = findViewById(R.id.spinnerDiaDiem);
        spinnerPT = findViewById(R.id.spinnerPT);
        imgDatPT = findViewById(R.id.imgDatPT);
        btnChonAnhPT = findViewById(R.id.btnChonAnhPT);
        btnThemPT = findViewById(R.id.btnThemPT);

        // Khởi tạo Firebase
        dbRefPT = FirebaseDatabase.getInstance().getReference("PT");
        dbRefDatPT = FirebaseDatabase.getInstance().getReference("DatPT");
        firebaseStorage = FirebaseStorage.getInstance(); // Khởi tạo Firebase Storage

        // Thiết lập danh sách địa điểm theo thành phố
        setupDiaDiemMap();

        // Thiết lập Spinner Thành phố và Địa điểm
        setupThanhPhoSpinner();

        // Kiểm tra quyền truy cập bộ nhớ
        checkStoragePermission();

        // Lấy danh mục PT từ Firebase và điền vào Spinner
        getPTFromFirebase();

        // Chọn ảnh sản phẩm
        btnChonAnhPT.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            selectImage.launch(intent);
        });

        // Thêm PT
        btnThemPT.setOnClickListener(v -> addPT());
    }

    // Phương thức thiết lập danh sách địa điểm theo thành phố
    private void setupDiaDiemMap() {
        diaDiemMap.put("Hồ Chí Minh", Arrays.asList("Gò Vấp", "Bình Thạnh", "Quận 1", "Quận 3", "Quận 7"));
        diaDiemMap.put("Hà Nội", Arrays.asList("Hoàng Mai", "Cầu Giấy", "Đống Đa"));
    }

    // Phương thức thiết lập Spinner Thành phố và Địa điểm
    private void setupThanhPhoSpinner() {
        List<String> thanhPhoList = new ArrayList<>(diaDiemMap.keySet());
        ArrayAdapter<String> thanhPhoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, thanhPhoList);
        thanhPhoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerThanhPho.setAdapter(thanhPhoAdapter);

        diaDiemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        diaDiemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiaDiem.setAdapter(diaDiemAdapter);

        spinnerThanhPho.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = thanhPhoList.get(position);
                updateDiaDiemSpinner(selectedCity);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                diaDiemAdapter.clear();
            }
        });
    }

    // Phương thức cập nhật Spinner Địa điểm
    private void updateDiaDiemSpinner(String city) {
        List<String> diaDiemList = diaDiemMap.get(city);
        diaDiemAdapter.clear();
        if (diaDiemList != null) {
            diaDiemAdapter.addAll(diaDiemList);
        }
        diaDiemAdapter.notifyDataSetChanged();
    }

    // Phương thức kiểm tra quyền truy cập bộ nhớ
    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    // Lấy danh mục PT từ Firebase và điền vào Spinner
    private void getPTFromFirebase() {
        dbRefPT.get().addOnSuccessListener(dataSnapshot -> {
            ArrayList<String> pTList = new ArrayList<>();
            for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String pTName = snapshot.child("tenPT").getValue(String.class);
                if (pTName != null) {
                    pTList.add(pTName);
                }
            }

            if (!pTList.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pTList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPT.setAdapter(adapter);
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi lấy danh mục PT", Toast.LENGTH_SHORT).show());
    }

    // Thêm PT vào Firebase
    private void addPT() {
        String thanhPho = spinnerThanhPho.getSelectedItem().toString();
        String diaDiem = spinnerDiaDiem.getSelectedItem().toString();
        String pTId = spinnerPT.getSelectedItem().toString();

        List<String> days = new ArrayList<>();
        if (checkboxMonday.isChecked()) days.add("Monday");
        if (checkboxTuesday.isChecked()) days.add("Tuesday");
        if (checkboxWednesday.isChecked()) days.add("Wednesday");
        if (checkboxThursday.isChecked()) days.add("Thursday");
        if (checkboxFriday.isChecked()) days.add("Friday");
        if (checkboxSaturday.isChecked()) days.add("Saturday");
        if (checkboxSunday.isChecked()) days.add("Sunday");

        List<String> sessions = new ArrayList<>();
        if (checkboxMorning.isChecked()) sessions.add("Sáng");
        if (checkboxAfternoon.isChecked()) sessions.add("Trưa");
        if (checkboxEvening.isChecked()) sessions.add("Chiều");
        if (checkboxNight.isChecked()) sessions.add("Tối");

        if (days.isEmpty() || sessions.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = firebaseStorage.getReference().child("images/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            String datPTId = dbRefDatPT.push().getKey();
            DatPT datPT = new DatPT(datPTId, thanhPho, diaDiem, imageUrl, pTId, days, sessions);

            if (datPTId != null) {
                dbRefDatPT.child(datPTId).setValue(datPT).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Thêm PT thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Thêm PT thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        })).addOnFailureListener(e -> Toast.makeText(this, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show());
    }
}
