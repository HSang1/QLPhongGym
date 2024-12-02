package com.example.qlphonggym;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.qlphonggym.CSDL.GioHang;
import com.example.qlphonggym.CSDL.SanPham;
import com.example.qlphonggym.CSDL.DanhMuc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class ChiTietSanPham extends AppCompatActivity {

    private ImageView imageSanPham1;
    private TextView tenDanhMuc;
    private TextView tenSanPham;
    private TextView giaSanPham;
    private TextView moTaSanPham;
    private TextView soLuongConLai;
    private Button buttonThemVaoGioHang;
    private TextView saoSanPham;
    private RatingBar ratingBarTrungBinh, ratingBarCuaBan;
    private ProgressBar progressBarSao1, progressBarSao2, progressBarSao3, progressBarSao4, progressBarSao5;
    private Button buttonGuiDanhGia;

    private String idSanPham;
    private SanPham currentSanPham;  // Biến toàn cục để lưu đối tượng SanPham

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String username;  // Lưu tên người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chitietsanpham);


        // Ánh xạ các view từ layout
        imageSanPham1 = findViewById(R.id.imageSanPham10);
        tenDanhMuc = findViewById(R.id.tenDanhMuc);
        tenSanPham = findViewById(R.id.tenSanPham);
        giaSanPham = findViewById(R.id.giaSanPham);
        moTaSanPham = findViewById(R.id.moTaSanPham);
        soLuongConLai = findViewById(R.id.soLuongConLai);
        buttonThemVaoGioHang = findViewById(R.id.buttonThemVaoGioHangSP);

        saoSanPham = findViewById(R.id.saoSanPham);
        ratingBarTrungBinh = findViewById(R.id.ratingTrungBinh);
        ratingBarCuaBan = findViewById(R.id.ratingBar);

        // Nút gửi đánh giá
        buttonGuiDanhGia = findViewById(R.id.buttonGuiDanhGia);

        // Các ProgressBar để hiển thị thanh tiến trình sao
        progressBarSao1 = findViewById(R.id.progressBarSao1);
        progressBarSao2 = findViewById(R.id.progressBarSao2);
        progressBarSao3 = findViewById(R.id.progressBarSao3);
        progressBarSao4 = findViewById(R.id.progressBarSao4);
        progressBarSao5 = findViewById(R.id.progressBarSao5);

        // Lấy thông tin người dùng hiện tại
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Lấy username từ Firebase Database
            getUsername(currentUser.getUid());
        } else {
            Toast.makeText(this, "Bạn cần đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        }

        // Nhận idSanPham từ Intent
        Intent intent = getIntent();
        idSanPham = intent.getStringExtra("idSanPham");

        if (idSanPham != null && !idSanPham.isEmpty()) {
            getSanPhamDetails(idSanPham);
        } else {
            Toast.makeText(this, "ID sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
        }

        buttonThemVaoGioHang.setOnClickListener(v -> {
            if (currentUser != null) {
                addToCart();
            } else {
                Toast.makeText(ChiTietSanPham.this, "Bạn cần đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
        buttonGuiDanhGia.setOnClickListener(v -> sendReview());
    }



    private void getUsername(String userId) {
        // Tạo tham chiếu đến Firebase để lấy thông tin người dùng
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Truy vấn thông tin người dùng theo userId
        mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username = dataSnapshot.child("username").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChiTietSanPham.this, "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSanPhamDetails(String idSanPham) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("SanPham");

        mDatabase.child(idSanPham).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentSanPham = dataSnapshot.getValue(SanPham.class);

                    if (currentSanPham != null) {
                        // Cập nhật giao diện với thông tin sản phẩm
                        tenSanPham.setText(currentSanPham.getTenSanPham());
                        giaSanPham.setText("Giá : " + formatPrice(currentSanPham.getGiaSanPham()) + " VNĐ");
                        moTaSanPham.setText(currentSanPham.getMoTaSanPham());
                        soLuongConLai.setText("Số lượng còn lại : " + currentSanPham.getSoLuongConLai());

                        // Tính toán và cập nhật phần đánh giá sao
                        updateRating(currentSanPham);

                        // Lấy thông tin danh mục
                        getDanhMucDetails(currentSanPham.getDanhMucId());

                        // Tải ảnh sản phẩm
                        Glide.with(ChiTietSanPham.this)
                                .load(currentSanPham.getImageUrl())
                                .into(imageSanPham1);

                        // Sau khi tải xong sản phẩm, gọi phương thức loadUserRating
                        loadUserRating();  // Gọi sau khi sản phẩm đã được tải xong
                    }
                } else {
                    Toast.makeText(ChiTietSanPham.this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChiTietSanPham.this, "Lỗi khi tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getDanhMucDetails(String danhMucId) {
        // Tạo tham chiếu đến Firebase để lấy thông tin danh mục
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("DanhMuc");

        // Truy vấn danh mục theo id
        mDatabase.child(danhMucId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DanhMuc danhMuc = dataSnapshot.getValue(DanhMuc.class);

                    if (danhMuc != null) {
                        // Cập nhật giao diện với tên danh mục
                        tenDanhMuc.setText(danhMuc.getTenDanhMuc());
                    }
                } else {
                    Toast.makeText(ChiTietSanPham.this, "Danh mục không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChiTietSanPham.this, "Lỗi khi tải thông tin danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm định dạng giá tiền
    private String formatPrice(String price) {
        try {
            long priceValue = Long.parseLong(price);  // Chuyển giá sang long để xử lý

            // Sử dụng NumberFormat để định dạng giá tiền
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
            String formatted = numberFormat.format(priceValue);

            return formatted.replace(',', ' ');  // Thay dấu phẩy bằng dấu cách
        } catch (NumberFormatException e) {
            return price;  // Nếu không thể chuyển đổi, giữ nguyên giá gốc
        }
    }
    private void updateRating(SanPham sanPham) {
        int totalRatings = sanPham.getMotSao() + sanPham.getHaiSao() + sanPham.getBaSao() + sanPham.getBonSao() + sanPham.getNamSao();

        if (totalRatings > 0) {

            double averageRating = (sanPham.getMotSao() * 1 + sanPham.getHaiSao() * 2 + sanPham.getBaSao() * 3 + sanPham.getBonSao() * 4 + sanPham.getNamSao() * 5) / (double) totalRatings;
            saoSanPham.setText("Đánh giá: " + String.format("%.1f", averageRating) + " / 5 " + "(" + totalRatings + ")");
            ratingBarTrungBinh.setRating((float) averageRating); // Cập nhật RatingBar trung bình


            int progress1 = (sanPham.getMotSao() * 100) / totalRatings;
            int progress2 = (sanPham.getHaiSao() * 100) / totalRatings;
            int progress3 = (sanPham.getBaSao() * 100) / totalRatings;
            int progress4 = (sanPham.getBonSao() * 100) / totalRatings;
            int progress5 = (sanPham.getNamSao() * 100) / totalRatings;


            progressBarSao1.setProgress(progress1);
            progressBarSao2.setProgress(progress2);
            progressBarSao3.setProgress(progress3);
            progressBarSao4.setProgress(progress4);
            progressBarSao5.setProgress(progress5);

        } else {
            progressBarSao1.setProgress(0);
            progressBarSao2.setProgress(0);
            progressBarSao3.setProgress(0);
            progressBarSao4.setProgress(0);
            progressBarSao5.setProgress(0);
        }
    }




    private void addToCart() {
        if (currentSanPham != null) {
            String tenSanPhamText = currentSanPham.getTenSanPham();
            String giaSanPhamText = currentSanPham.getGiaSanPham();
            String imageUrl = currentSanPham.getImageUrl();

            // Lấy idGioHang từ Firebase
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("GioHang");
            String gioHangId = mDatabase.push().getKey();  // Tạo ID duy nhất cho giỏ hàng

            if (gioHangId != null) {
                // Tạo đối tượng GioHang với idGioHang
                GioHang gioHang = new GioHang(idSanPham, tenSanPhamText, "1", giaSanPhamText, imageUrl, username, gioHangId);

                // Lưu vào Firebase
                mDatabase.child(gioHangId).setValue(gioHang)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ChiTietSanPham.this, "Đã thêm sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ChiTietSanPham.this, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(ChiTietSanPham.this, "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAverageRating(SanPham sanPham) {
        int totalRatings = sanPham.getMotSao() + sanPham.getHaiSao() + sanPham.getBaSao() + sanPham.getBonSao() + sanPham.getNamSao();
        if (totalRatings > 0) {
            double averageRating = (sanPham.getMotSao() * 1 + sanPham.getHaiSao() * 2 + sanPham.getBaSao() * 3 + sanPham.getBonSao() * 4 + sanPham.getNamSao() * 5) / (double) totalRatings;
            sanPham.setDiemTrungBinh(averageRating);
        } else {
            sanPham.setDiemTrungBinh(0);
        }
    }

    private void sendReview() {
        if (currentSanPham != null && currentUser != null) {
            int ratingValue = (int) ratingBarCuaBan.getRating(); // Lấy giá trị rating của người dùng

            if (ratingValue == 0) {
                Toast.makeText(ChiTietSanPham.this, "Vui lòng chọn mức đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem người dùng đã đánh giá sản phẩm này chưa
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("DanhGiaSanPham");
            String userId = currentUser.getUid();

            mDatabase.child(idSanPham).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Người dùng đã đánh giá, chỉ cho phép sửa đánh giá cũ
                        int oldRating = dataSnapshot.getValue(Integer.class);

                        // Cập nhật số lượng sao cho sản phẩm (cập nhật lại số lượng sao)
                        updateRatingForProduct(oldRating, ratingValue);

                        // Cập nhật lại đánh giá của người dùng
                        mDatabase.child(idSanPham).child(userId).setValue(ratingValue)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ChiTietSanPham.this, "Đánh giá đã được sửa", Toast.LENGTH_SHORT).show();
                                    updateAverageRating(currentSanPham);  // Cập nhật điểm trung bình sau khi người dùng sửa đánh giá
                                    updateRating(currentSanPham);  // Cập nhật lại phần hiển thị đánh giá
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ChiTietSanPham.this, "Lỗi khi sửa đánh giá", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Người dùng chưa đánh giá, cho phép đánh giá mới
                        updateRatingForProduct(0, ratingValue);  // 0 là chưa đánh giá

                        // Lưu đánh giá của người dùng vào Firebase
                        mDatabase.child(idSanPham).child(userId).setValue(ratingValue)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ChiTietSanPham.this, "Đánh giá đã được gửi", Toast.LENGTH_SHORT).show();
                                    updateAverageRating(currentSanPham);  // Cập nhật điểm trung bình sau khi người dùng đánh giá
                                    updateRating(currentSanPham);  // Cập nhật lại phần hiển thị đánh giá
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ChiTietSanPham.this, "Lỗi khi gửi đánh giá", Toast.LENGTH_SHORT).show();
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ChiTietSanPham.this, "Lỗi khi kiểm tra đánh giá", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ChiTietSanPham.this, "Bạn cần đăng nhập để gửi đánh giá", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRatingForProduct(int oldRating, int newRating) {
        if (oldRating > 0) {
            // Nếu người dùng đã đánh giá, giảm số lượng sao cũ
            decreaseRating(oldRating);
        }

        // Cập nhật số lượng sao cho sản phẩm
        switch (newRating) {
            case 1:
                currentSanPham.setMotSao(currentSanPham.getMotSao() + 1);
                break;
            case 2:
                currentSanPham.setHaiSao(currentSanPham.getHaiSao() + 1);
                break;
            case 3:
                currentSanPham.setBaSao(currentSanPham.getBaSao() + 1);
                break;
            case 4:
                currentSanPham.setBonSao(currentSanPham.getBonSao() + 1);
                break;
            case 5:
                currentSanPham.setNamSao(currentSanPham.getNamSao() + 1);
                break;
        }

        // Cập nhật vào Firebase
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("SanPham");
        mDatabase.child(idSanPham).setValue(currentSanPham);
    }

    private void decreaseRating(int oldRating) {
        switch (oldRating) {
            case 1:
                currentSanPham.setMotSao(currentSanPham.getMotSao() - 1);
                break;
            case 2:
                currentSanPham.setHaiSao(currentSanPham.getHaiSao() - 1);
                break;
            case 3:
                currentSanPham.setBaSao(currentSanPham.getBaSao() - 1);
                break;
            case 4:
                currentSanPham.setBonSao(currentSanPham.getBonSao() - 1);
                break;
            case 5:
                currentSanPham.setNamSao(currentSanPham.getNamSao() - 1);
                break;
        }
    }
    private void loadUserRating() {
        // Kiểm tra xem người dùng có đăng nhập chưa
        if (currentUser != null && currentSanPham != null) {
            String idSanPham = currentSanPham.getIdSanPham();  // Lấy idSanPham từ currentSanPham đã được tải

            // Lấy tham chiếu đến Firebase
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("DanhGiaSanPham");

            // Kiểm tra xem người dùng đã đánh giá sản phẩm này chưa
            String userId = currentUser.getUid();
            mDatabase.child(idSanPham).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Nếu người dùng đã đánh giá, lấy giá trị đánh giá
                        int ratingValue = dataSnapshot.getValue(Integer.class);

                        // Cập nhật RatingBar với giá trị đã đánh giá
                        ratingBarCuaBan.setRating(ratingValue);
                    } else {
                        // Nếu người dùng chưa đánh giá, mặc định không có sao nào
                        ratingBarCuaBan.setRating(0);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi nếu có
                    Toast.makeText(ChiTietSanPham.this, "Lỗi khi tải đánh giá của bạn", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




}


