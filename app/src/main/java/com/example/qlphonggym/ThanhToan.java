package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qlphonggym.Api.CreateOrder;
import com.example.qlphonggym.CSDL.GioHang;
import com.example.qlphonggym.CSDL.SanPham;
import com.example.qlphonggym.CSDL.TheTap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ThanhToan extends AppCompatActivity {

    private String loaiThe;
    private Button btnThanhToan;
    private DatabaseReference gioHangRef;
    private DatabaseReference sanPhamRef;
    private DatabaseReference theTapRef;
    private DatabaseReference doanhThu;
    private String username; // Lưu tên người dùng
    private String currentUserId; // UID của người dùng đăng nhập



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thanhtoan);

        btnThanhToan = findViewById(R.id.btnThanhToan);
        gioHangRef = FirebaseDatabase.getInstance().getReference("GioHang");
        sanPhamRef = FirebaseDatabase.getInstance().getReference("SanPham");
        theTapRef = FirebaseDatabase.getInstance().getReference("TheTap");
        doanhThu = FirebaseDatabase.getInstance().getReference("DoanhThu");

        Intent intent = getIntent();
        int total = intent.getIntExtra("total", 0);
        String totalString = String.format("%d", total);
        TextView tvTongTien = findViewById(R.id.tvTongTien);
        tvTongTien.setText(totalString + " VND");  // Hiển thị tổng tiền








        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);


        // Lấy UID của người dùng hiện tại
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Kiểm tra UID hợp lệ
        if (currentUserId != null) {
            getUsername(currentUserId);
        }



        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateOrder orderApi = new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(totalString);
                    String code = data.getString("return_code");

                    if (code.equals("1")) {
                        String token = data.getString("zp_trans_token");
                        ZaloPaySDK.getInstance().payOrder(ThanhToan.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                Intent intent1 = new Intent(ThanhToan.this, trangChu.class);
                                intent1.putExtra("res","Thanh toan thanh cong");
                                Toast.makeText(ThanhToan.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                                int tongTien = Integer.parseInt(totalString);
                                capNhatDoanhThu(tongTien);
                                xuLyThanhToan();
                                startActivity(intent1);
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                Intent intent1 = new Intent(ThanhToan.this, GioHangActivity.class);
                                intent1.putExtra("res","Huy thanh toan");
                                Toast.makeText(ThanhToan.this, "Đã hủy thanh toán", Toast.LENGTH_SHORT).show();
                                startActivity(intent1);
                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                                Intent intent1 = new Intent(ThanhToan.this, GioHangActivity.class);
                                Toast.makeText(ThanhToan.this, "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                                intent1.putExtra("res","Thanh toan that bai");
                                startActivity(intent1);
                            }

                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                }

        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }



    // Lấy thông tin username từ Firebase
    private void getUsername(String userId) {
        // Tạo tham chiếu tới bảng người dùng trong Firebase (có thể là "Users")
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Lấy thông tin username
                if (task.getResult().exists()) {
                    username = task.getResult().child("username").getValue(String.class);
                    // Kiểm tra xem đã lấy thành công username chưa
                    if (username != null) {
                        // Bây giờ bạn đã có username và có thể tiếp tục xử lý thanh toán
                    } else {
                        Toast.makeText(ThanhToan.this, "Không tìm thấy username trong dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ThanhToan.this, "Không có người dùng trong Firebase", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ThanhToan.this, "Lỗi khi lấy username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Xử lý thanh toán và xóa giỏ hàng
    private void xuLyThanhToan() {
        if (username == null) {
            Toast.makeText(ThanhToan.this, "Lỗi: Không tìm thấy username của bạn.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi phương thức để xóa toàn bộ sản phẩm trong giỏ hàng của người dùng
        filterGioHangByUsername(username);
    }

    // Phương thức lọc giỏ hàng theo username và xử lý thanh toán
    private void filterGioHangByUsername(String currentUsername) {
        gioHangRef.orderByChild("username").equalTo(currentUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Kiểm tra nếu có sản phẩm trong giỏ hàng
                        if (dataSnapshot.exists()) {
                            // Duyệt qua tất cả sản phẩm trong giỏ hàng và xử lý
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                GioHang gioHang = snapshot.getValue(GioHang.class);
                                if (gioHang != null) {
                                    // Lấy id sản phẩm và số lượng đã mua từ giỏ hàng
                                    String idSanPham = gioHang.getIdSanPham();
                                    int soLuongDaMua = Integer.parseInt(gioHang.getSoLuong());

                                    // Kiểm tra xem sản phẩm có phải là thẻ không
                                    if (isThe(idSanPham)) {
                                        // Nếu là thẻ, thêm thẻ tập vào CSDL
                                        addTheTapToDatabase(idSanPham);
                                    }

                                    // Cập nhật lại số lượng còn lại trong kho
                                    updateSoLuongConLai(idSanPham, soLuongDaMua);

                                    // Xóa sản phẩm khỏi giỏ hàng
                                    gioHangRef.child(snapshot.getKey()).removeValue();
                                }
                            }

                            // Thông báo thanh toán thành công
                            Toast.makeText(ThanhToan.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                            // Chuyển về trang chủ sau khi thanh toán thành công
                            Intent intent = new Intent(ThanhToan.this, trangChu.class);
                            startActivity(intent);
                            finish();  // Đóng trang hiện tại
                        } else {
                            // Nếu không có sản phẩm trong giỏ hàng
                            Toast.makeText(ThanhToan.this, "Giỏ hàng của bạn trống hoặc không có sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Xử lý khi có lỗi xảy ra trong việc đọc dữ liệu từ Firebase
                        Toast.makeText(ThanhToan.this, "Lỗi khi truy vấn dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Phương thức kiểm tra sản phẩm có phải là thẻ không
    private boolean isThe(String idSanPham) {
        // Danh sách các mã sản phẩm là thẻ
        String[] validIds = {
                "-OCXRMOWYFKSCmoJbyiu", // 1 tuần (Thẻ loại 1)
                "-OCXRyD1G_suqAvJIPUv", // 3 tháng (Thẻ loại 2)
                "-OCXS9LMU8zE-bTO7ru8", // 1 năm (Thẻ loại 2)
                "-OCXRfEuixyqrjjvLnc7", // 1 tháng (Thẻ loại 2)
                "-OCXSNolf1_toUiaRDjX", // 3 tháng (Thẻ loại 3)
                "-OCXT6dXlSchI_no9rtw", // 6 tháng (Thẻ loại 3)
                "-OCXTLUBsYqL6zf7GzGx", // 1 năm (Thẻ loại 3)
                "-OCXTXqNjY7lmxBc4suk", // 6 tháng loại 4
                "-OCXTfBqV1BlsR5MmWa1", // 12 tháng (Thẻ loại 4)
                "-OCXUV1kowrQThbXz2dT"  // 24 tháng (Thẻ loại 4)
        };

        // Kiểm tra xem idSanPham có nằm trong danh sách các id sản phẩm hợp lệ hay không
        for (String validId : validIds) {
            if (idSanPham.equals(validId)) {
                return true;
            }
        }
        return false;  // Nếu không phải thẻ hợp lệ, trả về false
    }

    // Thêm thẻ tập vào cơ sở dữ liệu
    private void addTheTapToDatabase(String idSanPham) {
        // Tính toán ngày bắt đầu là hôm nay
        Calendar calendar = Calendar.getInstance();
        String ngayBatDau = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());  // Lấy ngày hiện tại

        // Tính toán ngày kết thúc dựa trên idSanPham
        String[] ngayKetThuc = calculateNgayKetThuc(idSanPham);

        // Tạo đối tượng thẻ tập
        TheTap theTap = new TheTap(username,loaiThe, ngayBatDau, ngayKetThuc[0] );

        // Lưu thông tin thẻ tập vào Firebase
        theTapRef.child(currentUserId).setValue(theTap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ThanhToan.this, "Thẻ tập đã được thêm thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ThanhToan.this, "Lỗi khi thêm thẻ tập: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String[] calculateNgayKetThuc(String idSanPham) {
        Calendar calendar = Calendar.getInstance();  // Lấy thời gian hiện tại

        switch (idSanPham) {
            case "-OCXRMOWYFKSCmoJbyiu":
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                loaiThe = "1";
                break;
            case "-OCXRyD1G_suqAvJIPUv":
                calendar.add(Calendar.MONTH, 3);
                loaiThe = "2";
                break;
            case "-OCXS9LMU8zE-bTO7ru8":
                calendar.add(Calendar.YEAR, 1);
                loaiThe = "2";
                break;
            case "-OCXRfEuixyqrjjvLnc7":
                calendar.add(Calendar.MONTH, 1);
                loaiThe = "2";
                break;
            case "-OCXSNolf1_toUiaRDjX":
                calendar.add(Calendar.MONTH, 3);
                loaiThe = "3";
                break;
            case "-OCXT6dXlSchI_no9rtw":
                calendar.add(Calendar.MONTH, 6);
                loaiThe = "3";
                break;
            case "-OCXTLUBsYqL6zf7GzGx":
                calendar.add(Calendar.YEAR, 1);
                loaiThe = "3";
                break;
            case "-OCXTXqNjY7lmxBc4suk":
                calendar.add(Calendar.MONTH, 6);
                loaiThe = "4";
                break;
            case "-OCXTfBqV1BlsR5MmWa1":
                calendar.add(Calendar.YEAR, 1);
                loaiThe = "4";
                break;
            case "-OCXUV1kowrQThbXz2dT":
                calendar.add(Calendar.YEAR, 2);
                loaiThe = "4";
                break;
            default:
                loaiThe = "Loại thẻ không xác định";  // Nếu ID không hợp lệ
                break;
        }


        String ngayKetThuc = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        return new String[]{ngayKetThuc, loaiThe};
    }


    // Phương thức cập nhật số lượng sản phẩm còn lại trong kho
    private void updateSoLuongConLai(String idSanPham, int soLuongDaMua) {
        sanPhamRef.child(idSanPham).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                SanPham sanPham = task.getResult().getValue(SanPham.class);
                if (sanPham != null) {
                    int soLuongConLai = sanPham.getSoLuongConLai() - soLuongDaMua;
                    sanPhamRef.child(idSanPham).child("soLuongConLai").setValue(soLuongConLai);
                }
            }
        });
    }

    private void capNhatDoanhThu(int tongTien) {
        doanhThu.child("doanhthu").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long currentDoanhThu = mutableData.getValue(Long.class);
                if (currentDoanhThu == null) {
                    mutableData.setValue(tongTien);
                } else {
                    mutableData.setValue(currentDoanhThu + tongTien);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Toast.makeText(ThanhToan.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ThanhToan.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




}
