package com.example.qlphonggym.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.qlphonggym.CSDL.CSDL_Users;
import com.example.qlphonggym.R;
import com.example.qlphonggym.SuaThongTinUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<CSDL_Users> userList;
    private DatabaseReference usersRef;

    public UserAdapter(Context context, List<CSDL_Users> userList) {
        this.context = context;
        this.userList = userList;
        this.usersRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        CSDL_Users user = userList.get(position);
        holder.tvFullName.setText(user.getFullName());
        holder.tvUsername.setText(user.getUsername());

        // Nút Sửa thông tin
        holder.btnEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(context, SuaThongTinUser.class);
            editIntent.putExtra("USER_ID", user.getUsername()); // Truyền ID người dùng để sửa
            context.startActivity(editIntent);
        });

        // Nút Xóa người dùng
        holder.btnDelete.setOnClickListener(v -> {
            usersRef.child(user.getUsername()).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        userList.remove(position);
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý lỗi nếu có
                    });
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvUsername;
        Button btnEdit, btnDelete;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
