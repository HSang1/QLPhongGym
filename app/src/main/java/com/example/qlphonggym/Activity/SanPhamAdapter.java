package com.example.qlphonggym.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qlphonggym.CSDL.SanPham;
import com.example.qlphonggym.R;
import com.example.qlphonggym.SuaSanPham_admin;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SanPhamAdapter extends RecyclerView.Adapter<SanPhamAdapter.SanPhamViewHolder> {

    private Context context;
    private List<SanPham> sanPhamList;
    private DatabaseReference sanPhamRef;

    public SanPhamAdapter(Context context, List<SanPham> sanPhamList) {
        this.context = context;
        this.sanPhamList = sanPhamList;
        this.sanPhamRef = FirebaseDatabase.getInstance().getReference("SanPham");
    }

    @Override
    public SanPhamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item của RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.sanpham_item, parent, false);
        return new SanPhamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SanPhamViewHolder holder, int position) {
        // Lấy dữ liệu của sản phẩm
        SanPham sanPham = sanPhamList.get(position);

        // Hiển thị dữ liệu vào các thành phần giao diện
        holder.tvTenSanPham.setText(sanPham.getTenSanPham());
        holder.tvGiaSanPham.setText(sanPham.getGiaSanPham());
        holder.tvMoTaSanPham.setText(sanPham.getMoTaSanPham());

        // Hiển thị ảnh sản phẩm bằng Glide
        Glide.with(context)
                .load(sanPham.getImageUrl()) // Dùng link ảnh từ Firebase Storage
                .into(holder.imgSanPham);

        // Nút Sửa sản phẩm
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, SuaSanPham_admin.class);
            intent.putExtra("SANPHAM_ID", sanPham.getIdSanPham()); // Truyền ID sản phẩm
            context.startActivity(intent);
        });

        // Nút Xóa sản phẩm
        holder.btnDelete.setOnClickListener(v -> {
            sanPhamRef.child(sanPham.getIdSanPham()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        sanPhamList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi khi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return sanPhamList.size();
    }

    public static class SanPhamViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenSanPham, tvGiaSanPham, tvMoTaSanPham;
        Button btnEdit, btnDelete;
        ImageView imgSanPham;

        public SanPhamViewHolder(View itemView) {
            super(itemView);
            tvTenSanPham = itemView.findViewById(R.id.tenSanPham);
            tvGiaSanPham = itemView.findViewById(R.id.giaSanPham);
            tvMoTaSanPham = itemView.findViewById(R.id.moTaSanPham);
            imgSanPham = itemView.findViewById(R.id.imgSanPham);
            btnEdit = itemView.findViewById(R.id.btSuaSanPham);
            btnDelete = itemView.findViewById(R.id.btXoaSanPham);
        }
    }
}
