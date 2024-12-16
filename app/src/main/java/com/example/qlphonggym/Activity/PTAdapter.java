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

import com.example.qlphonggym.CSDL.PT;
import com.example.qlphonggym.R;
import com.example.qlphonggym.SuaPT_admin;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PTAdapter extends RecyclerView.Adapter<PTAdapter.PTViewHolder> {

    private Context context;
    private List<PT> pTList;
    private DatabaseReference pTRef;

    public PTAdapter(Context context, List<PT> pTList) {
        this.context = context;
        this.pTList = pTList;
        this.pTRef = FirebaseDatabase.getInstance().getReference("PT");
    }

    @Override
    public PTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pt_item, parent, false);
        return new PTViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PTViewHolder holder, int position) {
        PT pt = pTList.get(+position);
        if (pt != null && pt.getTenPT() != null) {
            holder.tvTenPT.setText(pt.getTenPT()); // Hiển thị tên PT
        } else {
            holder.tvTenPT.setText("Tên PT không có");
        }

        // Nút Sửa PT
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, SuaPT_admin.class);
            intent.putExtra("PT_ID", pt.getId()); // Truyền ID của PT
            context.startActivity(intent);
        });

        // Nút Xóa PT
        holder.btnDelete.setOnClickListener(v -> {
            pTRef.child(pt.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        pTList.remove(position);
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
        return pTList.size();
    }

    public static class PTViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenPT;
        Button btnEdit, btnDelete;

        public PTViewHolder(View itemView) {
            super(itemView);
            tvTenPT = itemView.findViewById(R.id.tenPT);
            btnEdit = itemView.findViewById(R.id.btSuaPT);
            btnDelete = itemView.findViewById(R.id.btXoaPT);
        }
    }
}
