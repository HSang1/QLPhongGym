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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.widget.Button;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter; // Tạo Adapter để kết nối Spinner
import android.widget.Spinner;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qlphonggym.CSDL.DatLop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class datLop extends AppCompatActivity {
    private final HashMap<String, List<String>> diaDiemMap = new HashMap<>(); // Danh sách thành phố và địa điểm
    private ArrayAdapter<String> diaDiemAdapter; // Adapter cho địa điểm
    private LinearLayout datesContainer;
    private LinearLayout classesContainer;

    private String selectedLop = ""; // Không lọc loại dịch vụ mặc định
    private String selectedCity = ""; // Không lọc thành phố mặc định
    private String selectedDiaDiem = ""; // Không lọc câu lạc bộ mặc định


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

        // Khởi tạo danh sách địa điểm theo Thành phố
        setupDiaDiemMap();


        // Lấy thứ hiện tại và ngày hiện tại
        String selectedDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(Calendar.getInstance().getTime()); // Ví dụ: "Friday"
        String selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()); // Ví dụ: "17/11/2024"

        // Hiển thị danh sách ngày và lớp học
        renderDates();
        renderClasses(selectedDay, selectedDate); // Truyền cả selectedDay và selectedDate


        TextView filterButton = findViewById(R.id.txtBoLoc); // Nút lọc
        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void renderDates() {
        datesContainer.removeAllViews(); // Xóa các view cũ trước khi thêm mới
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", new Locale("vi", "VN"));
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

        // Biến lưu trữ ngày được chọn
        String[] selectedDateHolder = {null}; // Mảng để lưu trạng thái vì cần biến final trong onClickListener

        for (int i = 0; i < 7; i++) {
            // Lưu ngày hiện tại để dùng khi bấm
            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            String selectedDate = fullDateFormat.format(calendar.getTime()); // Lấy ngày/tháng/năm tương ứng

            // Tạo LinearLayout dọc cho từng ngày
            LinearLayout dayLayout = new LinearLayout(this);
            dayLayout.setOrientation(LinearLayout.VERTICAL);
            dayLayout.setPadding(20, 10, 20, 10);
            dayLayout.setClickable(true); // Cho phép bấm

            // TextView cho thứ
            TextView dayOfWeekView = new TextView(this);
            dayOfWeekView.setText(dayFormat.format(calendar.getTime()));
            dayOfWeekView.setTextSize(16);
            dayOfWeekView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            // TextView cho ngày
            TextView dayOfMonthView = new TextView(this);
            dayOfMonthView.setText(dateFormat.format(calendar.getTime()));
            dayOfMonthView.setTextSize(16);
            dayOfMonthView.setGravity(View.TEXT_ALIGNMENT_CENTER);

            // Thiết lập màu sắc cho TextView
            if (i == 0) {
                dayOfWeekView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dayOfWeekView.setTextColor(getResources().getColor(R.color.white));
                dayOfMonthView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dayOfMonthView.setTextColor(getResources().getColor(R.color.white));
                selectedDateHolder[0] = selectedDate; // Ngày đầu tiên được chọn mặc định
            } else {
                dayOfWeekView.setTextColor(getResources().getColor(R.color.black));
                dayOfMonthView.setTextColor(getResources().getColor(R.color.black));
            }

            // Thêm các TextView vào LinearLayout
            dayLayout.addView(dayOfWeekView);
            dayLayout.addView(dayOfMonthView);

            // Thêm sự kiện bấm vào từng ngày
            final String finalDayOfWeek = dayOfWeek; // Biến tạm để dùng trong sự kiện
            final String finalSelectedDate = selectedDate;
            dayLayout.setOnClickListener(v -> {
                // Xóa màu highlight cũ
                for (int j = 0; j < datesContainer.getChildCount(); j++) {
                    LinearLayout child = (LinearLayout) datesContainer.getChildAt(j);
                    ((TextView) child.getChildAt(0)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    ((TextView) child.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) child.getChildAt(1)).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    ((TextView) child.getChildAt(1)).setTextColor(getResources().getColor(R.color.black));
                }

                // Cập nhật màu highlight cho ngày được chọn
                dayOfWeekView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dayOfWeekView.setTextColor(getResources().getColor(R.color.white));
                dayOfMonthView.setBackgroundColor(getResources().getColor(R.color.my_color));
                dayOfMonthView.setTextColor(getResources().getColor(R.color.white));

                // Lưu trạng thái ngày được chọn
                selectedDateHolder[0] = finalSelectedDate;

                // Gọi renderClasses với ngày được chọn
                renderClasses(finalDayOfWeek, finalSelectedDate);
            });

            // Thêm LinearLayout vào datesContainer
            datesContainer.addView(dayLayout);

            // Di chuyển sang ngày tiếp theo
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void setupDiaDiemMap() {
        diaDiemMap.put("Hồ Chí Minh", Arrays.asList("Quận 1", "Quận 3","Quận 7", "Gò Vấp", "Bình Thạnh"));

        diaDiemMap.put("Hà Nội", Arrays.asList("Hoàng Mai", "Cầu Giấy", "Đống Đa"));
    }


    private void renderClasses(String selectedDay, String selectedDate) {
        classesContainer.removeAllViews(); // Xóa các lớp học cũ

        DatabaseReference dbRefDatLop = FirebaseDatabase.getInstance().getReference("DatLop");
        DatabaseReference dbRefBookings = FirebaseDatabase.getInstance().getReference("Bookings");

        dbRefDatLop.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasClass = false;
                List<DatLop> classList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatLop datLop = snapshot.getValue(DatLop.class);

                    if (datLop != null) {
                        // Điều kiện lọc
                        if ((selectedCity.isEmpty() || datLop.getThanhPho().equalsIgnoreCase(selectedCity)) &&
                                (selectedDiaDiem.isEmpty() || datLop.getDiaDiem().equalsIgnoreCase(selectedDiaDiem)) &&
                                datLop.getDays() != null && datLop.getDays().contains(selectedDay)) {

                            // Lọc giờ chỉ nếu ngày là hôm nay
                            if (selectedDate.equals(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime()))) {
                                if (!isTimeValid(datLop.getThoiGianBatDau())) {
                                    continue; // Bỏ qua lớp học nếu thời gian đã qua
                                }
                            }
                            classList.add(datLop);
                            hasClass = true;
                        }
                    }
                }

                // Sắp xếp danh sách theo thời gian bắt đầu
                classList.sort((DatLop o1, DatLop o2) -> {
                    try {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH'h'mm", Locale.ENGLISH);
                        return timeFormat.parse(o1.getThoiGianBatDau()).compareTo(timeFormat.parse(o2.getThoiGianBatDau()));
                    } catch (Exception e) {
                        return 0; // Nếu lỗi, giữ nguyên thứ tự
                    }
                });

                // Hiển thị danh sách lớp học
                for (DatLop datLop : classList) {
                    dbRefBookings.orderByChild("classCode").equalTo(datLop.getLopHocId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot bookingSnapshot) {
                                    boolean isBooked = false;

                                    for (DataSnapshot booking : bookingSnapshot.getChildren()) {
                                        String bookingDate = booking.child("bookingDate").getValue(String.class);

                                        if (bookingDate != null && bookingDate.equals(selectedDate)) {
                                            isBooked = true;
                                            break;
                                        }
                                    }

                                    addClassToUI(datLop, isBooked, selectedDate);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(datLop.this, "Lỗi khi tải trạng thái đặt chỗ.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                if (!hasClass) {
                    TextView noClassMessage = new TextView(datLop.this);
                    noClassMessage.setText("Không có lớp học nào phù hợp với bộ lọc của bạn.");
                    noClassMessage.setTextSize(16);
                    noClassMessage.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    classesContainer.addView(noClassMessage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(datLop.this, "Lỗi khi tải dữ liệu lớp học.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private boolean isTimeValid(String startTime) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH'h'mm", Locale.ENGLISH);
            Calendar currentTime = Calendar.getInstance(); // Thời gian hiện tại
            Calendar classTime = Calendar.getInstance();

            // Chuyển đổi startTime thành Calendar
            classTime.setTime(timeFormat.parse(startTime));

            // So sánh giờ và phút giữa thời gian hiện tại và thời gian lớp học
            return (currentTime.get(Calendar.HOUR_OF_DAY) < classTime.get(Calendar.HOUR_OF_DAY)) ||
                    (currentTime.get(Calendar.HOUR_OF_DAY) == classTime.get(Calendar.HOUR_OF_DAY) &&
                            currentTime.get(Calendar.MINUTE) < classTime.get(Calendar.MINUTE));
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Trả về true nếu gặp lỗi để không chặn lớp học
        }
    }




    private void addClassToUI(DatLop datLop, boolean isBooked, String selectedDate) {
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

        // Ảnh của lớp học
        ImageView classImageView = new ImageView(this);
        classImageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        classImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Sử dụng Glide để tải ảnh từ URL
        Glide.with(this)
                .load(datLop.getImageUrl()) // URL của ảnh từ Firebase
                .placeholder(R.drawable.placeholder_image) // Ảnh hiển thị khi đang tải
                .into(classImageView);

        classLayout.addView(classImageView);

        // Tên lớp học
        TextView classNameView = new TextView(this);
        classNameView.setText("Tên lớp: " + datLop.getTenLopHoc());
        classNameView.setTextSize(20);
        classNameView.setPadding(0, 0, 0, 10);
        classLayout.addView(classNameView);

        // Thời gian bắt đầu
        TextView startTimeView = new TextView(this);
        startTimeView.setText("Bắt đầu: " + datLop.getThoiGianBatDau());
        classLayout.addView(startTimeView);

        // Thời lượng
        TextView durationView = new TextView(this);
        durationView.setText("Thời gian: " + datLop.getThoiLuong());
        classLayout.addView(durationView);

        // Thành phố
        TextView cityView = new TextView(this);
        cityView.setText("Thành phố: " + datLop.getThanhPho()); // Giả sử `DatLop` có thuộc tính `ThanhPho`
        cityView.setTextSize(16);
        cityView.setPadding(0, 0, 0, 5);
        classLayout.addView(cityView);

        // Địa điểm
        TextView locationView = new TextView(this);
        locationView.setText("Địa điểm: " + datLop.getDiaDiem());
        classLayout.addView(locationView);

        // Khi lớp học đã được đặt
        if (isBooked) {
            // Truyền thông tin lớp học sang trangChu khi người dùng nhấn
            classLayout.setOnClickListener(v -> {
                Intent intent = new Intent(datLop.this, trangChu.class);
                intent.putExtra("className", datLop.getTenLopHoc());
                intent.putExtra("dateTime", datLop.getThoiGianBatDau());
                intent.putExtra("location", datLop.getDiaDiem());
                intent.putExtra("isBooked", true);
                startActivity(intent);
            });
        }


        // Thêm sự kiện khi click vào lớp học
        classLayout.setOnClickListener(v -> {
            Intent intent = new Intent(datLop.this, thongTinDatLop.class);
            intent.putExtra("className", datLop.getTenLopHoc());
            intent.putExtra("classCode", datLop.getLopHocId());
            intent.putExtra("dateTime", datLop.getThoiGianBatDau());
            intent.putExtra("duration", datLop.getThoiLuong());
            intent.putExtra("city", datLop.getThanhPho()); // Truyền thêm thông tin thành phố
            intent.putExtra("location", datLop.getDiaDiem());
            intent.putExtra("date", selectedDate); // Truyền ngày đã chọn
            intent.putExtra("isBooked", isBooked); // Truyền trạng thái đặt chỗ
            intent.putExtra("imageUrl", datLop.getImageUrl()); // Truyền URL ảnh
            startActivity(intent);
        });

        // Thêm lớp vào giao diện
        classesContainer.addView(classLayout);
    }

    // Cập nhật Spinner Địa điểm khi chọn thành phố
    private void updateDiaDiemSpinner(String city) {
        List<String> diaDiemList = diaDiemMap.get(city);
        diaDiemAdapter.clear();
        if (diaDiemList != null) {
            diaDiemAdapter.addAll(diaDiemList);
        }
        diaDiemAdapter.notifyDataSetChanged();
    }



    // Hiển thị hộp thoại lọc lớp học
    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bộ lọc lớp học");

        // Inflate layout cho bộ lọc
        View view = getLayoutInflater().inflate(R.layout.bolocdatlop, null);
        builder.setView(view);

        // Khai báo Spinner
        Spinner citySpinner = view.findViewById(R.id.city_spinner);
        Spinner diadiemSpinner = view.findViewById(R.id.diadiem_spinner);

        // Thiết lập Adapter cho Spinner Thành phố
        List<String> thanhPhoList = new ArrayList<>(diaDiemMap.keySet());
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, thanhPhoList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        // Thiết lập Adapter cho Spinner Địa điểm
        diaDiemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        diaDiemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diadiemSpinner.setAdapter(diaDiemAdapter);

        // Sự kiện khi chọn thành phố
        citySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = thanhPhoList.get(position);
                updateDiaDiemSpinner(selectedCity); // Cập nhật địa điểm tương ứng
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                diaDiemAdapter.clear();
            }
        });

        // Nút áp dụng lọc
        Button applyButton = view.findViewById(R.id.apply_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        AlertDialog dialog = builder.create();

        applyButton.setOnClickListener(v -> {
            // Lấy giá trị bộ lọc
            selectedCity = citySpinner.getSelectedItem() != null ? citySpinner.getSelectedItem().toString() : "";
            selectedDiaDiem = diadiemSpinner.getSelectedItem() != null ? diadiemSpinner.getSelectedItem().toString() : "";

            // Cập nhật danh sách lớp học với bộ lọc
            String selectedDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            renderClasses(selectedDay, selectedDate);
            dialog.dismiss();
        });

        // Nút hủy bộ lọc
        cancelButton.setOnClickListener(v -> {
            selectedCity = "";
            selectedDiaDiem = "";

            // Hiển thị tất cả lớp học
            String selectedDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            String selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Calendar.getInstance().getTime());

            renderClasses(selectedDay, selectedDate);
            dialog.dismiss();
        });

        dialog.show();
    }

}
