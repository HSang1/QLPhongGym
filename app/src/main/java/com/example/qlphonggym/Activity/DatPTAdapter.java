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
import com.example.qlphonggym.CSDL.DatPT;
import com.example.qlphonggym.R;
import com.example.qlphonggym.SuaSanPham_admin;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class DatPTAdapter extends RecyclerView.Adapter<DatPTAdapter.DatPTViewHolder> {

    private Context context;
    private List<DatPT> datPTList;
    private DatabaseReference datPTRef;

    public DatPTAdapter(Context context, List<DatPT> datPTList) {
        this.context = context;
        this.datPTList = datPTList;
        this.datPTRef = FirebaseDatabase.getInstance().getReference("DatPT");
    }

    @Override
    public DatPTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item của RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.datpt_item, parent, false);
        return new DatPTViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DatPTViewHolder holder, int position) {
        // Lấy dữ liệu của PT
        DatPT datPT = datPTList.get(position);

        // Hiển thị dữ liệu vào các thành phần giao diện
        holder.tvPTId.setText("PT ID: " + datPT.getPTId());
        holder.tvThanhPho.setText("Thành phố: " + datPT.getThanhPho());
        holder.tvDiaDiem.setText("Địa điểm: " + datPT.getDiaDiem());

        // Hiển thị danh sách ngày
        if (datPT.getDays() != null && !datPT.getDays().isEmpty()) {
            holder.tvDays.setText("Ngày: " + String.join(", ", datPT.getDays()));
        } else {
            holder.tvDays.setText("Ngày: Không xác định");
        }

        // Hiển thị ảnh bằng Glide
        Glide.with(context)
                .load(datPT.getImageUrl()) // Link ảnh từ Firebase Storage
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imgDatPT);

        // Nút Sửa PT
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, SuaSanPham_admin.class);
            intent.putExtra("DATPT_ID", datPT.getIdDatPT()); // Truyền ID PT
            context.startActivity(intent);
        });

        // Nút Xóa PT
        holder.btnDelete.setOnClickListener(v -> {
            datPTRef.child(datPT.getIdDatPT()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        datPTList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Xóa PT thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Lỗi khi xóa PT: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return datPTList.size();
    }

    public static class DatPTViewHolder extends RecyclerView.ViewHolder {
        TextView tvPTId, tvThanhPho, tvDiaDiem, tvDays;
        Button btnEdit, btnDelete;
        ImageView imgDatPT;

        public DatPTViewHolder(View itemView) {
            super(itemView);
            tvPTId = itemView.findViewById(R.id.pTId); // ID PT
            tvThanhPho = itemView.findViewById(R.id.thanhPho); // Thành phố
            tvDiaDiem = itemView.findViewById(R.id.diaDiem); // Địa điểm
            tvDays = itemView.findViewById(R.id.days); // Ngày
            imgDatPT = itemView.findViewById(R.id.imgDatPT);
            btnEdit = itemView.findViewById(R.id.btSuaDatPT);
            btnDelete = itemView.findViewById(R.id.btXoaDatPT);
        }
    }
}
