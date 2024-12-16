package com.example.qlphonggym;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

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
            String qrContent = "Mã Lớp: " + classCode + "\n"
                    + "Địa điểm: " + location + "\n"
                    + "Thời gian: " + dateTime + "\n"
                    + "Vị trí đặt chỗ: " + seatPosition;
            generateQRCode(qrContent);
        } else {
            Toast.makeText(this, "Không có thông tin để tạo mã QR.", Toast.LENGTH_SHORT).show();
        }
    }


    private void generateQRCode(String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            // Mã hóa nội dung sang UTF-8
            String encodedContent;
            try {
                byte[] bytes = content.getBytes("UTF-8");
                encodedContent = new String(bytes, "ISO-8859-1");
            } catch (java.io.UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi mã hóa chuỗi.", Toast.LENGTH_SHORT).show();
                return; // Dừng nếu có lỗi mã hóa
            }

            // Tạo mã QR
            int size = 300;  // Kích thước mã QR
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(encodedContent, BarcodeFormat.QR_CODE, size, size);

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