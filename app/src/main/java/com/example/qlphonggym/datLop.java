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

public class datLop extends AppCompatActivity {
    private LinearLayout datesContainer;
    private LinearLayout classesContainer;

    private String selectedServiceType = "";
    private String selectedCity = "Hồ Chí Minh";
    private String selectedClub = "Landmark Centurion Club";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datlop);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        datesContainer = findViewById(R.id.dates_container);
        classesContainer = findViewById(R.id.classes_container);

        renderDates();
        renderClasses();

       TextView filterButton = findViewById(R.id.txtBoLoc); // Nút lọc
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void renderDates() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd/MM", new Locale("vi", "VN"));

        for (int i = 0; i < 7; i++) {
            TextView dateView = new TextView(this);
            dateView.setPadding(20, 10, 20, 10);
            dateView.setText(dateFormat.format(calendar.getTime()));
            dateView.setTextSize(16);
            dateView.setClickable(true);

            if (i == 0) {
                dateView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dateView.setTextColor(getResources().getColor(R.color.white));
            } else {
                dateView.setTextColor(getResources().getColor(R.color.black));
            }

            datesContainer.addView(dateView);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void renderClasses() {
        classesContainer.removeAllViews();

        String[] classNames = {"Sexy Dance", "J Dance", "Belly Dance", "Zumba"};
        String[] types = {"Dance", "Dance", "Dance", "Dance"};
        String[] locations = {"Landmark Centurion Club", "Landmark Centurion Club", "Landmark Centurion Club", "Landmark Centurion Club"};
        String[] startTimes = {"09:45", "11:00", "17:00", "19:20"};
        String[] durations = {"50 phút", "60 phút", "60 phút", "60 phút"};
        String[] cities = {"Hồ Chí Minh", "Hồ Chí Minh", "Hồ Chí Minh", "Hồ Chí Minh"};

        for (int i = 0; i < classNames.length; i++) {
            if ((selectedServiceType.isEmpty() || types[i].contains(selectedServiceType)) &&
                    (selectedCity.isEmpty() || cities[i].equals(selectedCity)) &&
                    (selectedClub.isEmpty() || locations[i].equals(selectedClub))) {

                LinearLayout classLayout = new LinearLayout(this);
                classLayout.setOrientation(LinearLayout.VERTICAL);
                classLayout.setPadding(15, 15, 15, 15);
                classLayout.setBackground(getResources().getDrawable(R.drawable.bottom_nav_gradient));
                classLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

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
