package com.example.qlphonggym.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.CSDL.DanhMuc;
import com.example.qlphonggym.R;

import java.util.List;

public class DanhMuc_CuaHang_Adapter extends RecyclerView.Adapter<DanhMuc_CuaHang_Adapter.DanhMucViewHolder> {

    private List<DanhMuc> danhMucList;
    private int selectedPosition = -1; // Vị trí của danh mục đã được chọn
    private Context context;
    private OnItemClickListener onItemClickListener;

    // Constructor nhận vào danh sách các danh mục và listener để xử lý khi người dùng click vào danh mục
    public DanhMuc_CuaHang_Adapter(Context context, List<DanhMuc> danhMucList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.danhMucList = danhMucList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public DanhMucViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Tạo view cho mỗi item trong RecyclerView
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.danhmuc_item_cuahang, parent, false);
        return new DanhMucViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DanhMucViewHolder holder, int position) {
        // Lấy danh mục từ danh sách
        DanhMuc danhMuc = danhMucList.get(position);

        // Hiển thị tên danh mục
        holder.tenDanhMuc.setText(danhMuc.getTenDanhMuc());

        // Đổi màu nền của item nếu nó được chọn
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.selected_item_color)); // Màu dành cho item được chọn
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.default_item_color)); // Màu mặc định
        }

        // Thêm sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            // Kiểm tra nếu danh mục đã được chọn, bỏ chọn
            if (selectedPosition == position) {
                selectedPosition = -1; // Bỏ chọn danh mục hiện tại
                onItemClickListener.onItemClick(null); // Gửi null khi không có danh mục nào được chọn
            } else {
                selectedPosition = position; // Cập nhật vị trí danh mục được chọn
                onItemClickListener.onItemClick(danhMucList.get(selectedPosition)); // Gửi danh mục được chọn
            }

            // Cập nhật lại RecyclerView để hiển thị màu mới
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return danhMucList.size();
    }

    // Interface để giao tiếp với Activity/Fragment khi người dùng click vào danh mục
    public interface OnItemClickListener {
        void onItemClick(DanhMuc danhMuc);
    }

    public static class DanhMucViewHolder extends RecyclerView.ViewHolder {
        public TextView tenDanhMuc;

        public DanhMucViewHolder(View view) {
            super(view);
            tenDanhMuc = view.findViewById(R.id.tenDanhMuc); // Ánh xạ TextView
        }
    }
}
