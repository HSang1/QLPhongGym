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
        PT pT = pTList.get(position);
        holder.tvTenPT.setText(pT.getTenPT());

        // Nút Sửa danh mục
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, SuaPT_admin.class);
            intent.putExtra("PT_ID", pT.getId()); // Truyền ID của danh mục
            context.startActivity(intent);
        });

        // Nút Xóa danh mục
        holder.btnDelete.setOnClickListener(v -> {
            pTRef.child(pT.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        pTList.remove(position);
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
