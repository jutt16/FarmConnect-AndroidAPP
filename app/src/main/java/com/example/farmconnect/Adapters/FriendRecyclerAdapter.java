package com.example.farmconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.FullScreenImageActivity;
import com.example.farmconnect.Models.FriendsModel;
import com.example.farmconnect.OtherProfileActivity;
import com.example.farmconnect.R;
import com.example.farmconnect.ViewHolders.FriendViewHolder;
import com.example.farmconnect.utils.AndroidUtil;

import java.util.ArrayList;

public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendViewHolder> {
    private ArrayList<FriendsModel> friendsList;
    private Context context;

    public FriendRecyclerAdapter(ArrayList<FriendsModel> friendsList, Context context) {
        this.friendsList = friendsList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_layout, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        FriendsModel model = friendsList.get(position);

        // Bind data to views in the holder
        holder.friend_name.setText(model.getName());

        // Check if the address is not null before setting the text
        if (model.getAddress() != null) {
            holder.address.setText(model.getAddress().toString());
        } else {
            holder.address.setText("Address not available");
        }

        holder.mobile.setText(model.getMobile());

        // Load image from URL and set it to the ImageView
        String imageUrl = context.getResources().getString(R.string.api_base_url) + ":8000" + model.getProfile_image_path();
        AndroidUtil.setImageByUrlwithfixsize(context, imageUrl, holder.friend_profile_image);
        holder.friend_profile_image.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("url", imageUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                Log.d("Error",e.getMessage());
                throw new RuntimeException(e);
            }
        });
        // Set OnClickListener for the entire item view
        holder.itemView.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(context, OtherProfileActivity.class);
                intent.putExtra("friend_id", String.valueOf(model.getUser_id()));
                intent.putExtra("friend_name",model.getName());
                intent.putExtra("friend_mobile",model.getMobile());
                if (model.getAddress() != null) {
                    intent.putExtra("friend_address",model.getAddress().toString());
                } else {
                    intent.putExtra("friend_address","Address not available");
                }
                intent.putExtra("friend_profile_img",imageUrl);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                Log.d("Error",e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}
