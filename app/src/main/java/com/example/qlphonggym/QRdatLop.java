package com.example.qlphonggym;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

//khai báo dòng sau
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.zxing.qrcode.QRCodeWriter;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class QRdatLop extends AppCompatActivity {

    private ImageView qrCodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_datlop);

        qrCodeImage = findViewById(R.id.qr_code_image);

        // Lấy dữ liệu từ Intent
        String classCode = getIntent().getStringExtra("classCode");
        String dateTime = getIntent().getStringExtra("dateTime");
        String location = getIntent().getStringExtra("location");
        String seatPosition = getIntent().getStringExtra("seatPosition");

        // Khởi tạo TextView "Quay lại" và thiết lập sự kiện onClick
        TextView txtQuayLai = findViewById(R.id.txtReturn);
        txtQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRdatLop.this, datLop.class); // Chuyển sang màn hình Trang Chủ
                startActivity(intent);
                finish(); // Đóng Activity hiện tại để tránh quay lại màn hình này khi bấm nút Back
            }
        });


        // Kiểm tra dữ liệu và tạo nội dung QR
        if (classCode != null && dateTime != null && location != null && seatPosition != null) {
            // Kết hợp thông tin thành một chuỗi để hiển thị trong mã QR
            String qrContent = "Ma Lop: " + classCode + "\n"
                    + "Dia diem: " + location + "\n"
                    + "Thoi gian: " + dateTime + "\n"
                    + "Vi tri dat cho: " + seatPosition;
            generateQRCode(qrContent);
        } else {
            Toast.makeText(this, "Không có thông tin để tạo mã QR.", Toast.LENGTH_SHORT).show();
        }
    }


    private void generateQRCode(String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            // Tạo mã QR
            int size = 300;  // Kích thước mã QR
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size);

            // Chuyển BitMatrix thành Bitmap để hiển thị
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
                }
            }
            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Lỗi khi tạo mã QR.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}