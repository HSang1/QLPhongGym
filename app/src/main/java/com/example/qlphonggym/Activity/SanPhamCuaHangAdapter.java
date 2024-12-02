package com.example.qlphonggym.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qlphonggym.CSDL.SanPham;
import com.example.qlphonggym.R;
import com.example.qlphonggym.ChiTietSanPham;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SanPhamCuaHangAdapter extends RecyclerView.Adapter<SanPhamCuaHangAdapter.SanPhamViewHolder> {

    private Context context;
    private List<SanPham> sanPhamList;

    public SanPhamCuaHangAdapter(Context context, List<SanPham> sanPhamList) {
        this.context = context;
        this.sanPhamList = sanPhamList;
    }

    @Override
    public SanPhamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sanphamcuahang_item, parent, false);
        return new SanPhamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SanPhamViewHolder holder, int position) {

        SanPham sanPham = sanPhamList.get(position);
        holder.tvTenSanPham.setText(sanPham.getTenSanPham());

        String gia = sanPham.getGiaSanPham();
        String formattedPrice = formatPrice(gia);
        holder.tvGiaSanPham.setText("Giá: " + formattedPrice + " VND");

        // Kiểm tra số lượng còn lại
        int soLuongConLai = sanPham.getSoLuongConLai();
        if (soLuongConLai == 0) {
            holder.tvStatus.setVisibility(View.VISIBLE); // Hiển thị thông báo "Hết hàng"
            holder.tvStatus.setText("Hết hàng");
        } else {
            holder.tvStatus.setVisibility(View.GONE); // Ẩn thông báo "Hết hàng"
        }

        Glide.with(context)
                .load(sanPham.getImageUrl())
                .into(holder.imgSanPham);

        holder.itemView.setOnClickListener(v -> {
            // Nếu sản phẩm còn hàng, cho phép người dùng chuyển sang chi tiết sản phẩm
            if (soLuongConLai > 0) {
                Intent intent = new Intent(context, ChiTietSanPham.class);
                intent.putExtra("idSanPham", sanPham.getIdSanPham());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sanPhamList.size();
    }

    private String formatPrice(String price) {
        try {
            // Xóa tất cả các dấu phẩy và khoảng trắng, rồi chia giá thành các phần
            long priceValue = Long.parseLong(price);  // Chuyển giá sang long để xử lý

            // Sử dụng NumberFormat để định dạng giá tiền
            NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
            String formatted = numberFormat.format(priceValue);
            return formatted.replace(',', ' ');
        } catch (NumberFormatException e) {
            return price;  // Nếu không thể chuyển đổi, giữ nguyên giá gốc
        }
    }

    public static class SanPhamViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenSanPham, tvGiaSanPham, tvStatus;  // Thêm tvStatus để hiển thị thông báo "Hết hàng"
        ImageView imgSanPham;

        public SanPhamViewHolder(View itemView) {
            super(itemView);
            tvTenSanPham = itemView.findViewById(R.id.tenSanPham);
            tvGiaSanPham = itemView.findViewById(R.id.giaSanPham);
            tvStatus = itemView.findViewById(R.id.tvStatus);  // Tạo TextView cho thông báo "Hết hàng"
            imgSanPham = itemView.findViewById(R.id.imgSanPham);
        }
    }
}
