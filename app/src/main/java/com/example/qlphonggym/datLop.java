package com.example.qlphonggym;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//Thêm dòng sau
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.widget.Button;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter; // Tạo Adapter để kết nối Spinner
import android.widget.Spinner;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class datLop extends AppCompatActivity {
    private LinearLayout datesContainer;
    private LinearLayout classesContainer;

    private String selectedServiceType = ""; // Không lọc loại dịch vụ mặc định
    private String selectedCity = ""; // Không lọc thành phố mặc định
    private String selectedClub = ""; // Không lọc câu lạc bộ mặc định


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datlop);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo TextView "Quay lại" và thiết lập sự kiện onClick
        TextView txtQuayLai = findViewById(R.id.back_btn);
        txtQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(datLop.this, trangChu.class); // Chuyển sang màn hình Trang Chủ
                startActivity(intent);
            }
        });

        datesContainer = findViewById(R.id.dates_container);
        classesContainer = findViewById(R.id.classes_container);

        renderDates();
        renderClasses();

       TextView filterButton = findViewById(R.id.txtBoLoc); // Nút lọc
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void renderDates() {
        datesContainer.removeAllViews(); // Xóa các view cũ trước khi thêm mới
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", new Locale("vi", "VN"));

        for (int i = 0; i < 7; i++) {
            // Tạo LinearLayout dọc cho từng ngày
            LinearLayout dayLayout = new LinearLayout(this);
            dayLayout.setOrientation(LinearLayout.VERTICAL);
            dayLayout.setPadding(20, 10, 20, 10);

            // TextView cho thứ
            TextView dayOfWeekView = new TextView(this);
            String dayOfWeek = dayFormat.format(calendar.getTime());
            dayOfWeekView.setText(dayOfWeek);
            dayOfWeekView.setTextSize(16);
            dayOfWeekView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            // TextView cho ngày
            TextView dayOfMonthView = new TextView(this);
            String dayOfMonth = dateFormat.format(calendar.getTime());
            dayOfMonthView.setText(dayOfMonth);
            dayOfMonthView.setTextSize(16);
            dayOfMonthView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            // Thiết lập màu sắc cho TextView
            if (i == 0) {
                dayOfWeekView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dayOfWeekView.setTextColor(getResources().getColor(R.color.white));
                dayOfMonthView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dayOfMonthView.setTextColor(getResources().getColor(R.color.white));
            } else {
                dayOfWeekView.setTextColor(getResources().getColor(R.color.black));
                dayOfMonthView.setTextColor(getResources().getColor(R.color.black));
            }

            // Thêm các TextView vào LinearLayout
            dayLayout.addView(dayOfWeekView);
            dayLayout.addView(dayOfMonthView);

            // Thêm LinearLayout vào datesContainer
            datesContainer.addView(dayLayout);

            // Di chuyển sang ngày tiếp theo
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
    private void renderClasses() {
        classesContainer.removeAllViews();

        String[] classNames = {"Tango Dance", "Fo Yoga", "Belly Dance", "T Cycling"};
        String[] types = {"Group-X", "Yoga", "Group-X", "Cycling"};
        String[] locations = {"Landmark Centurion Club", "Go Vap Club", "Landmark Centurion Club", "Landmark Centurion Club"};
        String[] startTimes = {"09:45", "11:00", "17:00", "19:20"};
        String[] durations = {"50 phút", "60 phút", "60 phút", "60 phút"};
        String[] cities = {"Hồ Chí Minh", "Hồ Chí Minh", "Hồ Chí Minh", "Hồ Chí Minh"};
        int[] imageIds = {R.drawable.login, R.drawable.login, R.drawable.login, R.drawable.login};

        for (int i = 0; i < classNames.length; i++) {
            if ((selectedServiceType.isEmpty() || types[i].contains(selectedServiceType)) &&
                    (selectedCity.isEmpty() || cities[i].equals(selectedCity)) &&
                    (selectedClub.isEmpty() || locations[i].equals(selectedClub))) {

                LinearLayout classLayout = new LinearLayout(this);
                classLayout.setOrientation(LinearLayout.VERTICAL);
                classLayout.setPadding(15, 15, 15, 15);
                classLayout.setBackground(getResources().getDrawable(R.drawable.bottom_nav_gradient));

                // Thiết lập khoảng cách giữa các lớp học
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 20, 0, 20); // Tạo khoảng cách trên và dưới giữa các lớp học
                classLayout.setLayoutParams(params);


                // Thêm ImageView cho ảnh đại diện của lớp
                ImageView classImage = new ImageView(this);
                classImage.setImageResource(imageIds[i]);
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        200  // chiều cao ảnh, tùy chỉnh theo nhu cầu
                );
                classImage.setLayoutParams(imageParams);
                classLayout.addView(classImage);


                TextView classNameView = new TextView(this);
                classNameView.setText(classNames[i]);
                classNameView.setTextSize(18);
                classNameView.setPadding(0, 0, 0, 10);
                classLayout.addView(classNameView);

                TextView locationView = new TextView(this);
                locationView.setText("Nơi: " + locations[i]);
                locationView.setTextColor(getResources().getColor(R.color.my_color2));
                classLayout.addView(locationView);

                TextView startTimeView = new TextView(this);
                startTimeView.setText("Thời gian bắt đầu: " + startTimes[i]);
                startTimeView.setTextColor(getResources().getColor(R.color.my_color2));
                classLayout.addView(startTimeView);

                TextView durationView = new TextView(this);
                durationView.setText("Thời gian: " + durations[i]);
                durationView.setTextColor(getResources().getColor(R.color.my_color2));
                classLayout.addView(durationView);

                TextView cityView = new TextView(this);
                cityView.setText("Địa điểm: " + cities[i]);
                cityView.setTextColor(getResources().getColor(R.color.my_color2));
                classLayout.addView(cityView);

                // Đặt sự kiện onClickListener cho từng lớp học
                int finalI = i; // Biến tạm cho phép truy cập trong onClickListener
                classLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(datLop.this, thongTinDatLop.class);
                    intent.putExtra("className", classNames[finalI]);
                    intent.putExtra("classCode", "798435"); // Mã lớp ví dụ
                    intent.putExtra("dateTime", startTimes[finalI] + " " + cities[finalI]);
                    intent.putExtra("duration", durations[finalI]);  //khoảng thời gian
                    intent.putExtra("type", types[finalI]);
                    intent.putExtra("location", locations[finalI]);
                    intent.putExtra("maxSeats", 20); // Truyền số lượng chỗ tối đa

                    startActivity(intent);
                });

                classesContainer.addView(classLayout);
            }
        }

    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bộ lọc");

        // Bố cục hộp thoại bộ lọc tăng cường
        View view = getLayoutInflater().inflate(R.layout.bolocdatlop, null);
        builder.setView(view);

        // Khởi tạo các tùy chọn bộ lọc trong bố cục hộp thoại
        Spinner serviceTypeSpinner = view.findViewById(R.id.service_type_spinner);
        Spinner citySpinner = view.findViewById(R.id.city_spinner);
        Spinner clubSpinner = view.findViewById(R.id.club_spinner);

        // Điền dữ liệu vào các spinners
        ArrayAdapter<CharSequence> serviceTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.service_types, android.R.layout.simple_spinner_item);
        serviceTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  // Cập nhật ArrayAdapter
        serviceTypeSpinner.setAdapter(serviceTypeAdapter);

        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,
                R.array.cities, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  // Cập nhật ArrayAdapter
        citySpinner.setAdapter(cityAdapter);

        ArrayAdapter<CharSequence> clubAdapter = ArrayAdapter.createFromResource(this,
                R.array.clubs, android.R.layout.simple_spinner_item);
        clubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  // Cập nhật ArrayAdapter
        clubSpinner.setAdapter(clubAdapter);

        // Đặt nút onClick cho nút Áp dụng trong hộp thoại
        Button applyButton = view.findViewById(R.id.apply_button);

        AlertDialog dialog = builder.create();  // Tạo đối tượng dialog
        applyButton.setOnClickListener(v -> {
            // Đặt biến bộ lọc từ các giá trị đã chọn trong hộp thoại
            selectedServiceType = serviceTypeSpinner.getSelectedItem().toString();
            selectedCity = citySpinner.getSelectedItem().toString();
            selectedClub = clubSpinner.getSelectedItem().toString();

            renderClasses(); // Làm mới các lớp dựa trên các bộ lọc mới
            dialog.dismiss(); // Đóng hộp thoại sau khi áp dụng
        });

        dialog.show(); // Hiển thị hộp thoại
    }

}
