package com.example.farmconnect.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.farmconnect.FullScreenImageActivity;
import com.example.farmconnect.Models.UsersFragmentModel;
import com.example.farmconnect.R;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.ApiCalls;

import java.util.List;

public class UsersFragmentRecyclerAdapter extends RecyclerView.Adapter<UsersFragmentRecyclerAdapter.UsersViewHolder> {
    private List<UsersFragmentModel> userList;
    Context context;

    public UsersFragmentRecyclerAdapter(List<UsersFragmentModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usersfragment_layout, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        UsersFragmentModel user = userList.get(position);
        holder.usernameText.setText(user.getName());
        holder.address.setText(user.getAddress());
        String url = context.getResources().getString(R.string.api_base_url) + user.getProfileImagePath();
        AndroidUtil.setImageByUrl(context, url, holder.profilePic);

        holder.profilePic.setOnClickListener(view -> {
            Intent intent = new Intent(context, FullScreenImageActivity.class);
            intent.putExtra("url", url);
            context.startActivity(intent);
        });

        holder.friend_btn.setOnClickListener(view -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                ApiCalls.sendFriendRequest(context, userList.get(adapterPosition).getId(), holder.friend_btn, success -> {
                    if (success) {
                        // Handle successful friend request logic
                        // Remove the item from the data set
                        userList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                        notifyItemRangeChanged(adapterPosition, userList.size());
                    } else {
                        // Handle failure logic
                        // Optionally, you can display an error message here
                        AndroidUtil.showToast(context, "Failed to send friend request.");
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView address;
        Button friend_btn;
        ImageView profilePic;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            address = itemView.findViewById(R.id.address_text);
            friend_btn = itemView.findViewById(R.id.add_friend_btn);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}

