
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class datPT extends AppCompatActivity {
    private final HashMap<String, List<String>> diaDiemMap = new HashMap<>();
    private ArrayAdapter<String> diaDiemAdapter;

    private LinearLayout datesContainer;
    private LinearLayout classesContainer;

    private String selectedPT = "";
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

        // Khởi tạo TextView "Quay lại" và thiết lập sự kiện onClick
        TextView txtQuayLai = findViewById(R.id.back_btn1);
        txtQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(datPT.this, trangChu.class); // Chuyển sang màn hình Trang Chủ
                startActivity(intent);
            }
        });

        datesContainer = findViewById(R.id.dates_container);
        classesContainer = findViewById(R.id.classes_container);
        TextView filterButton = findViewById(R.id.txtBoLoc1);

        selectedDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());


        // Khởi tạo danh sách địa điểm theo Thành phố
        setupDiaDiemMap();

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

    private void setupDiaDiemMap() {
        diaDiemMap.put("Hồ Chí Minh", Arrays.asList("Quận 1", "Quận 3","Quận 7", "Gò Vấp", "Bình Thạnh"));
        diaDiemMap.put("Hà Nội", Arrays.asList("Hoàng Mai", "Cầu Giấy", "Đống Đa"));
    }

    private void renderClasses(String selectedDay, String selectedDate) {
        classesContainer.removeAllViews();
        DatabaseReference dbRefDatPT = FirebaseDatabase.getInstance().getReference("DatPT");

        // Lấy thời gian hiện tại
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // Giờ hiện tại
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calendar.getTime());

        dbRefDatPT.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasClass = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatPT datPT = snapshot.getValue(DatPT.class);

                    if (datPT != null && filterMatches(datPT)) {
                        // Kiểm tra xem ngày hiện tại có trong danh sách ngày của PT không
                        if (datPT.getDays() != null && datPT.getDays().contains(selectedDay)) {
                            for (String session : datPT.getSessions()) {
                                // Ẩn các buổi đã qua nếu ngày được chọn là hôm nay
                                if (selectedDate.equals(todayDate) && !isSessionValid(session, currentHour)) {
                                    continue; // Bỏ qua các session đã qua
                                }
                                addClassToUI(datPT, session);
                                hasClass = true;
                            }
                        }
                    }
                }

                if (!hasClass) {
                    TextView noClassMessage = new TextView(datPT.this);
                    noClassMessage.setText("Không tìm thấy PT phù hợp với bộ lọc.");
                    noClassMessage.setPadding(10, 10, 10, 10);
                    noClassMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    classesContainer.addView(noClassMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(datPT.this, "Lỗi khi tải dữ liệu PT.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm kiểm tra xem session còn hợp lệ không dựa vào giờ hiện tại
    private boolean isSessionValid(String session, int currentHour) {
        switch (session.toLowerCase()) {
            case "sáng":
                return currentHour < 11; // Buổi sáng kết thúc lúc 11h
            case "trưa":
                return currentHour < 13; // Buổi trưa kết thúc lúc 13h
            case "chiều":
                return currentHour < 18; // Buổi chiều kết thúc lúc 18h
            case "tối":
                return currentHour < 21; // Buổi tối kết thúc lúc 21h
            default:
                return true; // Nếu không rõ session thì không ẩn
        }
    }


    private boolean filterMatches(DatPT datPT) {

        boolean cityMatches = selectedCity.isEmpty() || // Không lọc nếu rỗng
                (datPT.getThanhPho() != null && datPT.getThanhPho().equalsIgnoreCase(selectedCity));

        boolean locationMatches = selectedDiaDiem.isEmpty() || // Không lọc nếu rỗng
                (datPT.getDiaDiem() != null && datPT.getDiaDiem().equalsIgnoreCase(selectedDiaDiem));

        return cityMatches && locationMatches;
    }

    private void addClassToUI(DatPT datPT, String session) {
        LinearLayout classLayout = new LinearLayout(this);
        classLayout.setOrientation(LinearLayout.VERTICAL);
        classLayout.setPadding(15, 15, 15, 15);
        classLayout.setBackground(getResources().getDrawable(R.drawable.bottom_nav_gradient));

        // Thiết lập khoảng cách giữa các lớp học
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 20, 0, 20);
        classLayout.setLayoutParams(params);

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

        // Buổi
        TextView sessionView = new TextView(this);
        sessionView.setText("Buổi: " + session);
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

    private void updateDiaDiemSpinner(String city) {
        List<String> diaDiemList = diaDiemMap.get(city);
        diaDiemAdapter.clear();
        if (diaDiemList != null) {
            diaDiemAdapter.addAll(diaDiemList);
        }
        diaDiemAdapter.notifyDataSetChanged();
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bộ lọc PT");

        View view = getLayoutInflater().inflate(R.layout.bolocdatpt, null);
        builder.setView(view);

        Spinner citySpinner = view.findViewById(R.id.city_spinner);
        Spinner locationSpinner = view.findViewById(R.id.diadiem_spinner);

        // Thiết lập Adapter cho Spinner Thành phố
        List<String> thanhPhoList = new ArrayList<>(diaDiemMap.keySet());
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, thanhPhoList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        // Thiết lập Adapter cho Spinner Địa điểm
        diaDiemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        diaDiemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(diaDiemAdapter);

        // Sự kiện chọn Thành phố
        citySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
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

        Button applyButton = view.findViewById(R.id.apply_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        applyButton.setOnClickListener(v -> {
            // Lấy giá trị từ Spinner
            selectedCity = citySpinner.getSelectedItem().toString();
            selectedDiaDiem = locationSpinner.getSelectedItem().toString();

            renderClasses(selectedDay, selectedDate);
            builder.create().dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            selectedCity = "";
            selectedDiaDiem = "";
            renderClasses(selectedDay, selectedDate); // Hiển thị tất cả lớp học
            builder.create().dismiss();
        });

        builder.create().show();
    }


}
