package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.qlphonggym.CSDL.DatPT;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class datPT extends AppCompatActivity {
    private LinearLayout datesContainer;
    private LinearLayout classesContainer;
    private TextView selectedDateView;

    private String selectedPT = "";
    private String selectedSession = "";
    private String selectedCity = "";
    private String selectedDiaDiem = "";
    private String selectedDay = "";
    private String selectedDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datpt);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        datesContainer = findViewById(R.id.dates_container);
        classesContainer = findViewById(R.id.classes_container);
        TextView filterButton = findViewById(R.id.txtBoLoc1);

        selectedDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());


        // Kết nối bộ lọc
        filterButton.setOnClickListener(v -> showFilterDialog());

        renderDates();
        renderClasses(selectedDay, selectedDate);
    }

    private void renderDates() {
        datesContainer.removeAllViews(); // Xóa các view cũ trước khi thêm mới
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", new Locale("vi", "VN"));
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        // Biến lưu trữ ngày được chọn
        String[] selectedDateHolder = {selectedDate}; // Lưu ngày được chọn hiện tại

        for (int i = 0; i < 7; i++) {
            // Lưu ngày hiện tại để dùng khi bấm
            final String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            final String currentDate = fullDateFormat.format(calendar.getTime()); // Lấy ngày/tháng/năm tương ứng

            // Tạo LinearLayout dọc cho từng ngày
            LinearLayout dateLayout = new LinearLayout(this);
            dateLayout.setOrientation(LinearLayout.VERTICAL);
            dateLayout.setPadding(20, 10, 20, 10);
            dateLayout.setClickable(true); // Cho phép bấm

            // TextView cho thứ
            TextView dayView = new TextView(this);
            dayView.setText(dayFormat.format(calendar.getTime()));
            dayView.setTextSize(16);
            dayView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            // TextView cho ngày
            TextView dateView = new TextView(this);
            dateView.setText(dateFormat.format(calendar.getTime()));
            dateView.setTextSize(14);
            dateView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            // Thiết lập màu sắc cho ngày hiện tại
            if (currentDate.equals(selectedDateHolder[0])) {
                dayView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dayView.setTextColor(getResources().getColor(R.color.white));
                dateView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dateView.setTextColor(getResources().getColor(R.color.white));
            } else {
                dayView.setTextColor(getResources().getColor(R.color.black));
                dateView.setTextColor(getResources().getColor(R.color.black));
            }

            // Thêm các TextView vào LinearLayout
            dateLayout.addView(dayView);
            dateLayout.addView(dateView);

            // Thêm sự kiện bấm vào ngày
            dateLayout.setOnClickListener(v -> {
                // Xóa màu highlight cũ
                for (int j = 0; j < datesContainer.getChildCount(); j++) {
                    LinearLayout childLayout = (LinearLayout) datesContainer.getChildAt(j);
                    ((TextView) childLayout.getChildAt(0)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    ((TextView) childLayout.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) childLayout.getChildAt(1)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    ((TextView) childLayout.getChildAt(1)).setTextColor(getResources().getColor(R.color.black));
                }

                // Cập nhật màu highlight cho ngày được chọn
                dayView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dayView.setTextColor(getResources().getColor(R.color.white));
                dateView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dateView.setTextColor(getResources().getColor(R.color.white));

                // Cập nhật trạng thái ngày được chọn
                selectedDateHolder[0] = currentDate;
                selectedDay = dayOfWeek;
                selectedDate = currentDate;

                // Gọi renderClasses với ngày được chọn
                renderClasses(dayOfWeek, currentDate);
            });

            // Thêm LinearLayout vào datesContainer
            datesContainer.addView(dateLayout);

            // Di chuyển sang ngày tiếp theo
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void renderClasses(String selectedDay, String selectedDate) {
        classesContainer.removeAllViews();
        DatabaseReference dbRefDatPT = FirebaseDatabase.getInstance().getReference("DatPT");

        dbRefDatPT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasClass = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatPT datPT = snapshot.getValue(DatPT.class);

                    if (datPT != null && datPT.getDays().contains(selectedDay)) {
                        if (filterMatches(datPT)) {
                            for (String session : datPT.getSessions()) {
                                addClassToUI(datPT, session);
                                hasClass = true;
                            }
                        }
                    }
                }

                if (!hasClass) {
                    TextView noClassMessage = new TextView(datPT.this);
                    noClassMessage.setText("Không tìm thấy PT phù hợp.");
                    classesContainer.addView(noClassMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(datPT.this, "Lỗi khi tải dữ liệu PT.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean filterMatches(DatPT datPT) {
        boolean sessionMatches = selectedSession.isEmpty() ||
                (datPT.getSessions() != null &&
                        datPT.getSessions().contains(selectedSession));

        boolean cityMatches = selectedCity.isEmpty() ||
                (datPT.getThanhPho() != null &&
                        datPT.getThanhPho().equalsIgnoreCase(selectedCity));

        boolean locationMatches = selectedDiaDiem.isEmpty() ||
                (datPT.getDiaDiem() != null &&
                        datPT.getDiaDiem().equalsIgnoreCase(selectedDiaDiem));

        return sessionMatches && cityMatches && locationMatches;
    }

    private void addClassToUI(DatPT datPT, String session) {
        LinearLayout classLayout = new LinearLayout(this);
        classLayout.setOrientation(LinearLayout.VERTICAL);
        classLayout.setPadding(15, 15, 15, 15);
        classLayout.setBackground(getResources().getDrawable(R.drawable.bottom_nav_gradient));

        // Ảnh
        ImageView classImageView = new ImageView(this);
        classImageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        Glide.with(this)
                .load(datPT.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(classImageView);
        classLayout.addView(classImageView);

        // PT ID
        TextView ptIdView = new TextView(this);
        ptIdView.setText("PT: " + datPT.getPTId());
        ptIdView.setTextSize(18);
        ptIdView.setPadding(0, 0, 0, 10);
        classLayout.addView(ptIdView);

        // Dịch session sang tiếng Việt
        String vietnameseSession = translateSession(session);

        // Buổi
        TextView sessionView = new TextView(this);
        sessionView.setText("Buổi: " + vietnameseSession); // Hiển thị tiếng Việt
        sessionView.setTextSize(16);
        classLayout.addView(sessionView);

        // Thành phố
        TextView cityView = new TextView(this);
        cityView.setText("Thành phố: " + datPT.getThanhPho());
        cityView.setTextSize(16);
        classLayout.addView(cityView);

        // Địa điểm
        TextView locationView = new TextView(this);
        locationView.setText("Địa điểm: " + datPT.getDiaDiem());
        locationView.setTextSize(16);
        classLayout.addView(locationView);

        // Sự kiện click để chuyển sang thongtindatpt
        classLayout.setOnClickListener(v -> {
            selectedPT = datPT.getPTId(); // Gán PT ID
            selectedCity = datPT.getThanhPho(); // Gán Thành phố
            selectedDiaDiem = datPT.getDiaDiem(); // Gán Địa điểm

            Intent intent = new Intent(datPT.this, thongTinDatPT.class);
            intent.putExtra("PTId", selectedPT);
            intent.putExtra("Session", session);
            intent.putExtra("Date", selectedDate);
            intent.putExtra("City", selectedCity);
            intent.putExtra("Location", selectedDiaDiem);
            startActivity(intent);
        });

        classesContainer.addView(classLayout);
    }

    // Hàm chuyển đổi session sang tiếng Việt
    private String translateSession(String session) {
        switch (session) {
            case "Morning":
                return "Sáng";
            case "Afternoon":
                return "Trưa";
            case "Evening":
                return "Chiều";
            case "Night":
                return "Tối";
            default:
                return session; // Giữ nguyên nếu không khớp
        }
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bộ lọc PT");

        View view = getLayoutInflater().inflate(R.layout.bolocdatpt, null);
        builder.setView(view);

        Spinner sessionSpinner = view.findViewById(R.id.session_spinner);
        Spinner citySpinner = view.findViewById(R.id.city_spinner);
        Spinner locationSpinner = view.findViewById(R.id.diadiem_spinner);

        // Gắn Adapter cho Spinner
        ArrayAdapter<String> sessionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Sáng", "Trưa", "Chiều", "Tối"});
        sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sessionSpinner.setAdapter(sessionAdapter);

        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,
                R.array.cities, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.diadiems, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);

        Button applyButton = view.findViewById(R.id.apply_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        applyButton.setOnClickListener(v -> {
            selectedSession = translateVietnameseSession(sessionSpinner.getSelectedItem().toString());
            selectedCity = citySpinner.getSelectedItem().toString();
            selectedDiaDiem = locationSpinner.getSelectedItem().toString();
            renderClasses(selectedDay, selectedDate); // Làm mới danh sách lớp học
            builder.create().dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            selectedSession = "";
            selectedCity = "";
            selectedDiaDiem = "";
            renderClasses(selectedDay, selectedDate); // Hiển thị tất cả lớp học
            builder.create().dismiss();
        });

        builder.create().show();
    }

    // Hàm chuyển đổi session từ tiếng Việt về tiếng Anh để so khớp với dữ liệu Firebase
    private String translateVietnameseSession(String session) {
        switch (session) {
            case "Sáng":
                return "Morning";
            case "Trưa":
                return "Afternoon";
            case "Chiều":
                return "Evening";
            case "Tối":
                return "Night";
            default:
                return ""; // Không lọc nếu trống
        }
    }
}