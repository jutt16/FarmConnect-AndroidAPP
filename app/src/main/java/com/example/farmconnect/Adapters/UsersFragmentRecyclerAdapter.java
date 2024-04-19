package com.example.farmconnect.Adapters;

import android.content.Context;
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
import com.example.farmconnect.Models.UsersFragmentModel;
import com.example.farmconnect.R;
import com.example.farmconnect.utils.AndroidUtil;

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
        AndroidUtil.showToast(context,context.getResources().getString(R.string.api_base_url)+user.getProfileImagePath());
        Glide.with(context) // Provide a context
                .load(context.getResources().getString(R.string.api_base_url)+user.getProfileImagePath()) // Set the image URL
                .apply(RequestOptions.circleCropTransform()) // Apply circle crop transformation
                .into(holder.profilePic); // Specify the ImageView to load the image into
        // Set other views here
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

