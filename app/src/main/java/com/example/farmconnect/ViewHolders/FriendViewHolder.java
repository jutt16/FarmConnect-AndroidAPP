package com.example.farmconnect.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.R;

public class FriendViewHolder extends RecyclerView.ViewHolder {
    public ImageView friend_profile_image;
    public TextView friend_name,address,mobile;
    public FriendViewHolder(@NonNull View itemView) {
        super(itemView);
        friend_profile_image = itemView.findViewById(R.id.friend_profile_pic);
        friend_name = itemView.findViewById(R.id.user_name_text);
        address = itemView.findViewById(R.id.address_text);
        mobile = itemView.findViewById(R.id.mobile_text);
    }
}
