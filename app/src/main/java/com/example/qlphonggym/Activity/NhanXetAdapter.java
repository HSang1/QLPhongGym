package com.example.qlphonggym.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.CSDL.CSDL_NhanXet;
import com.example.qlphonggym.R;

import java.util.List;

public class NhanXetAdapter extends RecyclerView.Adapter<NhanXetAdapter.NhanXetViewHolder> {

    private List<CSDL_NhanXet> nhanXetList;

    public NhanXetAdapter(List<CSDL_NhanXet> nhanXetList) {
        this.nhanXetList = nhanXetList;
    }

    @NonNull
    @Override
    public NhanXetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nhanxet_item, parent, false);
        return new NhanXetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NhanXetViewHolder holder, int position) {
        CSDL_NhanXet nhanXet = nhanXetList.get(position);
        holder.tvUsername.setText(nhanXet.getUsername());
        holder.tvMoTa.setText(nhanXet.getMoTa());
        holder.tvGopY.setText("Đã góp ý: ");

        // Hiển thị ngày giờ góp ý
        holder.tvNgayGio.setText(nhanXet.getNgayGioGopY());
    }

    @Override
    public int getItemCount() {
        return nhanXetList.size();
    }

    public static class NhanXetViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername, tvGopY, tvMoTa, tvNgayGio;  // Thêm tvNgayGio
        ImageView deleteButton;

        public NhanXetViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvGopY = itemView.findViewById(R.id.tvGopY);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            tvNgayGio = itemView.findViewById(R.id.tvNgayGio);  // Ánh xạ TextView mới
        }
    }
}
