package com.example.qlphonggym;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private EditText txtMatKhau;
    private ImageView imgAnHienMatKhau;
    private boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Thiết lập layout trước khi tìm kiếm các view
        setContentView(R.layout.activity_main);

        // Gán view sau khi setContentView
        txtMatKhau = findViewById(R.id.txtMatKhau);
        imgAnHienMatKhau = findViewById(R.id.imgAnHienMatKhau);

        // Thiết lập sự kiện nhấn cho ImageView
        imgAnHienMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    txtMatKhau.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgAnHienMatKhau.setImageResource(R.drawable.baseline_remove_red_eye_24);
                } else {
                    // Show password
                    txtMatKhau.setInputType(InputType.TYPE_CLASS_TEXT);
                    imgAnHienMatKhau.setImageResource(R.drawable.baseline_visibility_off_24);
                }
                isPasswordVisible = !isPasswordVisible; // Toggle state for next click
                txtMatKhau.setSelection(txtMatKhau.getText().length()); // Set cursor to end after toggle
            }
        });

        EdgeToEdge.enable(this);

        // Áp dụng WindowInsets sau khi view đã được thiết lập
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
