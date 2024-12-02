package com.example.qlphonggym.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.CSDL.LopHoc;
import com.example.qlphonggym.R;
import com.example.qlphonggym.SuaLopHoc_admin;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LopHocAdapter extends RecyclerView.Adapter<LopHocAdapter.LopHocViewHolder> {

    private Context context;
    private List<LopHoc> lopHocList;
    private DatabaseReference lopHocRef;

    public LopHocAdapter(Context context, List<LopHoc> lopHocList) {
        this.context = context;
        this.lopHocList = lopHocList;
        this.lopHocRef = FirebaseDatabase.getInstance().getReference("LopHoc");
    }

    @Override
    public LopHocViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lophoc_item, parent, false);
        return new LopHocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LopHocViewHolder holder, int position) {
        LopHoc lopHoc = lopHocList.get(position);
        holder.tvTenLopHoc.setText(lopHoc.getTenLopHoc());

        // Nút Sửa danh mục
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, SuaLopHoc_admin.class);
            intent.putExtra("LOPHOC_ID", lopHoc.getId()); // Truyền ID của danh mục
            context.startActivity(intent);
        });

        // Nút Xóa danh mục
        holder.btnDelete.setOnClickListener(v -> {
            lopHocRef.child(lopHoc.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        lopHocList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Xóa danh mục thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi khi xóa danh mục: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return lopHocList.size();
    }

    public static class LopHocViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenLopHoc;
        Button btnEdit, btnDelete;

        public LopHocViewHolder(View itemView) {
            super(itemView);
            tvTenLopHoc = itemView.findViewById(R.id.tenLopHoc);
            btnEdit = itemView.findViewById(R.id.btSuaLopHoc);
            btnDelete = itemView.findViewById(R.id.btXoaLopHoc);
        }
    }
}

