package com.example.qlphonggym.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qlphonggym.CSDL.GioHang;
import com.example.qlphonggym.CSDL.SanPham;
import com.example.qlphonggym.GioHangActivity;
import com.example.qlphonggym.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.GioHangViewHolder> {

    private Context context;
    private List<GioHang> gioHangList;
    private DatabaseReference gioHangRef;

    public GioHangAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
        this.gioHangRef = FirebaseDatabase.getInstance().getReference("GioHang");
    }

    @Override
    public GioHangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.giohang_item, parent, false);
        return new GioHangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GioHangViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);

        holder.tvTenSanPham.setText(gioHang.getTenSP());
        holder.tvGiaSanPham.setText("Giá: " + gioHang.getGiaSP() + " VND");
        holder.tvSoLuong.setText(gioHang.getSoLuong());

        int totalPrice = Integer.parseInt(gioHang.getGiaSP()) * Integer.parseInt(gioHang.getSoLuong());
        holder.tvTongGia.setText("Tổng: " + totalPrice + " VND");

        Glide.with(context).load(gioHang.getImageUrl()).into(holder.imgSanPham);

        // Logic xử lý sự kiện bấm nút + (Tăng số lượng)
        holder.btnCong.setOnClickListener(v -> {
            // Kiểm tra số lượng còn lại của sản phẩm từ Firebase
            checkAndUpdateQuantity(gioHang, holder);
        });

        // Logic xử lý sự kiện bấm nút - (Giảm số lượng)
        holder.btnTru.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(gioHang.getSoLuong());
            int newQuantity = currentQuantity - 1;

            if (newQuantity > 0) {
                updateQuantity(gioHang, newQuantity, holder);
            } else {
                Toast.makeText(context, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            }
        });

        // Logic xử lý sự kiện xóa sản phẩm khỏi giỏ hàng
        holder.btnDelete.setOnClickListener(v -> {
            gioHangRef.child(gioHang.getIdGioHang()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        gioHangList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Sản phẩm đã được xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();

                        // Cập nhật lại tổng tiền trong GioHangActivity
                        if (context instanceof GioHangActivity) {
                            ((GioHangActivity) context).updateTongTien();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi khi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    // Phương thức kiểm tra và cập nhật số lượng
    private void checkAndUpdateQuantity(GioHang gioHang, GioHangViewHolder holder) {
        // Truy vấn Firebase để lấy thông tin sản phẩm theo idSanPham
        DatabaseReference sanPhamRef = FirebaseDatabase.getInstance().getReference("SanPham");
        sanPhamRef.child(gioHang.getIdSanPham()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Lấy thông tin sản phẩm
                        SanPham sanPham = task.getResult().getValue(SanPham.class);
                        if (sanPham != null) {
                            // Lấy số lượng còn lại
                            int availableQuantity = sanPham.getSoLuongConLai();
                            int currentQuantity = Integer.parseInt(gioHang.getSoLuong());

                            // Kiểm tra nếu số lượng cộng vào không vượt quá số lượng còn lại của sản phẩm
                            if (currentQuantity < availableQuantity) {
                                int newQuantity = currentQuantity + 1;
                                updateQuantity(gioHang, newQuantity, holder);
                            } else {
                                Toast.makeText(context, "Không thể mua thêm, sản phẩm đã hết hàng!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(context, "Lỗi khi lấy thông tin sản phẩm: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Phương thức cập nhật số lượng
    private void updateQuantity(GioHang gioHang, int newQuantity, GioHangViewHolder holder) {
        gioHangRef.child(gioHang.getIdGioHang()).child("soLuong").setValue(String.valueOf(newQuantity))
                .addOnSuccessListener(aVoid -> {
                    gioHang.setSoLuong(String.valueOf(newQuantity));
                    holder.tvSoLuong.setText(gioHang.getSoLuong());
                    int totalPrice = Integer.parseInt(gioHang.getGiaSP()) * Integer.parseInt(gioHang.getSoLuong());
                    holder.tvTongGia.setText("Tổng: " + totalPrice + " VND");

                    // Cập nhật lại tổng tiền trong GioHangActivity
                    if (context instanceof GioHangActivity) {
                        ((GioHangActivity) context).updateTongTien();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class GioHangViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenSanPham, tvGiaSanPham, tvSoLuong, tvTongGia;
        Button btnDelete;
        ImageView btnCong, btnTru;
        ImageView imgSanPham;

        public GioHangViewHolder(View itemView) {
            super(itemView);
            tvTenSanPham = itemView.findViewById(R.id.tenSanPhamGioHang);
            tvGiaSanPham = itemView.findViewById(R.id.giaSanPhamGioHang);
            tvSoLuong = itemView.findViewById(R.id.soLuongSanPhamGioHang);
            imgSanPham = itemView.findViewById(R.id.imageSanPhamGioHang);
            btnCong = itemView.findViewById(R.id.btnTangSoLuongSanPhamGioHang);
            btnTru = itemView.findViewById(R.id.btnGiamSoLuongSanPhamGioHang);
            btnDelete = itemView.findViewById(R.id.btXoaSPGioHang);
            tvTongGia = itemView.findViewById(R.id.tongGiaSanPham);
        }
    }
}
