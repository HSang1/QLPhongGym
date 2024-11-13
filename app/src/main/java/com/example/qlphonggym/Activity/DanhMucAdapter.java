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

import com.example.qlphonggym.CSDL.DanhMuc;
import com.example.qlphonggym.R;
import com.example.qlphonggym.SuaDanhMuc_admin;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DanhMucAdapter extends RecyclerView.Adapter<DanhMucAdapter.DanhMucViewHolder> {

    private Context context;
    private List<DanhMuc> danhMucList;
    private DatabaseReference danhMucRef;

    public DanhMucAdapter(Context context, List<DanhMuc> danhMucList) {
        this.context = context;
        this.danhMucList = danhMucList;
        this.danhMucRef = FirebaseDatabase.getInstance().getReference("DanhMuc");
    }

    @Override
    public DanhMucViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.danhmuc_item, parent, false);
        return new DanhMucViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DanhMucViewHolder holder, int position) {
        DanhMuc danhMuc = danhMucList.get(position);
        holder.tvTenDanhMuc.setText(danhMuc.getTenDanhMuc());

        // Nút Sửa danh mục
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, SuaDanhMuc_admin.class);
            intent.putExtra("DANHMUC_ID", danhMuc.getId()); // Truyền ID của danh mục
            context.startActivity(intent);
        });

        // Nút Xóa danh mục
        holder.btnDelete.setOnClickListener(v -> {
            danhMucRef.child(danhMuc.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        danhMucList.remove(position);
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
        return danhMucList.size();
    }

    public static class DanhMucViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenDanhMuc;
        Button btnEdit, btnDelete;

        public DanhMucViewHolder(View itemView) {
            super(itemView);
            tvTenDanhMuc = itemView.findViewById(R.id.tenDanhMuc);
            btnEdit = itemView.findViewById(R.id.btSuaDanhMuc);
            btnDelete = itemView.findViewById(R.id.btXoaDanhMuc);
        }
    }
}