package com.example.farmconnect.ViewHolders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.R;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
    public TextView userNameText, addressText;
    public Button rejectButton, acceptButton;
    public ImageView profileImage;

    public FriendRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        userNameText = itemView.findViewById(R.id.user_name_text);
        addressText = itemView.findViewById(R.id.address_text);
        profileImage = itemView.findViewById(R.id.profile_pic_image_view);
        rejectButton = itemView.findViewById(R.id.reject_request_btn);
        acceptButton = itemView.findViewById(R.id.accept_request_btn);
    }
}