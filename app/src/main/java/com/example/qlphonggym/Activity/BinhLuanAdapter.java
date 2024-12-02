package com.example.qlphonggym.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.qlphonggym.CSDL.BinhLuan;
import com.example.qlphonggym.R;
import java.util.List;

public class BinhLuanAdapter extends RecyclerView.Adapter<BinhLuanAdapter.BinhLuanViewHolder> {

    private Context context;
    private List<BinhLuan> binhLuanList;

    public BinhLuanAdapter(Context context, List<BinhLuan> binhLuanList) {
        this.context = context;
        this.binhLuanList = binhLuanList;
    }

    @Override
    public BinhLuanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.binhluan_item, parent, false);
        return new BinhLuanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BinhLuanViewHolder holder, int position) {
        BinhLuan binhLuan = binhLuanList.get(position);

        // Set tên người dùng
        holder.textViewUsername.setText(binhLuan.getUsername());
        // Set nội dung bình luận
        holder.textViewMoTa.setText(binhLuan.getMoTa());
        // Set rating sao
        holder.ratingBar.setRating(binhLuan.getRating());
    }

    @Override
    public int getItemCount() {
        return binhLuanList.size();
    }

    public static class BinhLuanViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewMoTa;
        RatingBar ratingBar;

        public BinhLuanViewHolder(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.username);
            textViewMoTa = itemView.findViewById(R.id.moTa);
            ratingBar = itemView.findViewById(R.id.soSao);
        }
    }
}
