package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash_Screen extends AppCompatActivity {
    private TextView welcomeText;
    private String fullText = "Welcome to PTS GYM";
    private int index = 0;
    private long delay = 180; // Thời gian delay giữa các chữ (ms)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        welcomeText = findViewById(R.id.welcomeText);

        // Gọi phương thức để hiển thị từng chữ
        showTextWithEffect();

        // Chờ 3 giây rồi chuyển sang màn hình chính
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Splash_Screen.this, trangChu.class);
            startActivity(intent);
            finish();
        }, 4000);
    }

    private void showTextWithEffect() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < fullText.length()) {
                    welcomeText.setText(welcomeText.getText().toString() + fullText.charAt(index));
                    index++;
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }
}