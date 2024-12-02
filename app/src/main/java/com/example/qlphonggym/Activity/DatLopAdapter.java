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
import com.example.qlphonggym.CSDL.DatLop;
import com.example.qlphonggym.R;
import com.example.qlphonggym.SuaSanPham_admin;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DatLopAdapter extends RecyclerView.Adapter<DatLopAdapter.DatLopViewHolder> {

    private Context context;
    private List<DatLop> datLopList;
    private DatabaseReference datLopRef;

    public DatLopAdapter(Context context, List<DatLop> datLopList) {
        this.context = context;
        this.datLopList = datLopList;
        this.datLopRef = FirebaseDatabase.getInstance().getReference("DatLop");
    }

    @Override
    public DatLopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item của RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.datlop_item, parent, false);
        return new DatLopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DatLopViewHolder holder, int position) {
        // Lấy dữ liệu của sản phẩm
        DatLop datLop = datLopList.get(position);

        // Hiển thị dữ liệu vào các thành phần giao diện
        holder.tvTenLopHoc.setText(datLop.getTenLopHoc());
        holder.tvThoiGianBatDau.setText("Bắt đầu: " + datLop.getThoiGianBatDau());
        holder.tvThoiLuong.setText("Thời gian: " + datLop.getThoiLuong());
        holder.tvThanhPho.setText(datLop.getThanhPho());
        holder.tvDiaDiem.setText("Địa điểm: " + datLop.getDiaDiem());
        holder.tvLopHoc.setText("Lớp Học: " + datLop.getLopHocId());  // Hiển thị danh mục

        // Hiển thị danh sách ngày
        if (datLop.getDays() != null && !datLop.getDays().isEmpty()) {
            holder.tvDays.setText("Ngày: " + String.join(", ", datLop.getDays()));
        } else {
            holder.tvDays.setText("Ngày: Không xác định");
        }

        // Hiển thị ảnh sản phẩm bằng Glide
        Glide.with(context)
                .load(datLop.getImageUrl()) // Dùng link ảnh từ Firebase Storage
                .into(holder.imgDatLop);

        // Nút Sửa sản phẩm
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, SuaSanPham_admin.class);
            intent.putExtra("DATLOP_ID", datLop.getIdDatLop()); // Truyền ID sản phẩm
            context.startActivity(intent);
        });

        // Nút Xóa sản phẩm
        holder.btnDelete.setOnClickListener(v -> {
            datLopRef.child(datLop.getIdDatLop()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        datLopList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Xóa lớp thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi khi xóa lớp: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return datLopList.size();
    }

    public static class DatLopViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenLopHoc, tvThoiGianBatDau, tvThoiLuong, tvThanhPho, tvDiaDiem, tvLopHoc, tvDays;
        Button btnEdit, btnDelete;
        ImageView imgDatLop;

        public DatLopViewHolder(View itemView) {
            super(itemView);
            tvTenLopHoc = itemView.findViewById(R.id.tenLopHoc);
            tvThoiGianBatDau = itemView.findViewById(R.id.thoiGianBatDau);
            tvThoiLuong = itemView.findViewById(R.id.thoiLuong);
            tvThanhPho = itemView.findViewById(R.id.thanhPho);
            tvDiaDiem = itemView.findViewById(R.id.diaDiem);
            tvLopHoc = itemView.findViewById(R.id.lopHocDatLop); // TextView cho danh mục
            tvDays = itemView.findViewById(R.id.days); // Thêm TextView cho ngày
            imgDatLop = itemView.findViewById(R.id.imgDatLop);
            btnEdit = itemView.findViewById(R.id.btSuaDatLop);
            btnDelete = itemView.findViewById(R.id.btXoaDatLop);
        }
    }
}

