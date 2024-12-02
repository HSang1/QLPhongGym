package com.example.qlphonggym.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.CSDL.SanPham;
import com.example.qlphonggym.R;

import java.util.List;

public class ThongKeAdapter extends RecyclerView.Adapter<ThongKeAdapter.ThongKeViewHolder> {

    private List<SanPham> sanPhamList;

    public ThongKeAdapter(List<SanPham> sanPhamList) {
        this.sanPhamList = sanPhamList;
    }

    @NonNull
    @Override
    public ThongKeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thongke_item, parent, false);
        return new ThongKeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThongKeViewHolder holder, int position) {
        SanPham sanPham = sanPhamList.get(position);
        holder.tenSanPham.setText(sanPham.getTenSanPham());
        holder.giaSanPham.setText(String.format("Giá: %d VND", sanPham.getGiaSanPhamInt()));
        holder.soLuongBanDuoc.setText(String.format("Số lượng bán được: %d", sanPham.getSoLuongBanDuoc()));
    }

    @Override
    public int getItemCount() {
        return sanPhamList.size();
    }

    public static class ThongKeViewHolder extends RecyclerView.ViewHolder {
        TextView tenSanPham, giaSanPham, soLuongBanDuoc;

        public ThongKeViewHolder(View itemView) {
            super(itemView);
            tenSanPham = itemView.findViewById(R.id.tenSanPham);
            giaSanPham = itemView.findViewById(R.id.giaSanPham);
            soLuongBanDuoc = itemView.findViewById(R.id.SoLuongBanDuoc);
        }
    }
}
