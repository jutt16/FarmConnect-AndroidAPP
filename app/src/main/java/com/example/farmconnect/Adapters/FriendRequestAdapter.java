package com.example.farmconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.FullScreenImageActivity;
import com.example.farmconnect.Models.FriendRequestModel;
import com.example.farmconnect.R;
import com.example.farmconnect.ViewHolders.FriendRequestViewHolder;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.ApiCalls;

import java.util.ArrayList;
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestViewHolder> {
    private ArrayList<FriendRequestModel> requestsList;
    Context context;

    public FriendRequestAdapter(ArrayList<FriendRequestModel> requestsList,Context context) {
        this.requestsList = requestsList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_requests_layout, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        FriendRequestModel request = requestsList.get(position);
        holder.userNameText.setText(request.getSender_name());
        holder.addressText.setText(request.getSender_address());
        String url = context.getResources().getString(R.string.api_base_url)+request.getSender_profile_image_path();
        AndroidUtil.setImageByUrl(context,url,holder.profileImage);
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("url",url);
                context.startActivity(intent);
            }
        });
        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle reject button click
                ApiCalls.rejectFriendRequest(context, request.getId(), new ApiCalls.RejectFriendRequestCallback() {
                    @Override
                    public void onFriendRequestRejected(boolean success) {
                        if (success) {
                            // Handle successful rejection logic
                            // Remove the item from the data set
                            AndroidUtil.showToast(context,"You rejected the friend request!");
                            requestsList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(), requestsList.size());
                        } else {
                            // Handle failure logic
                            // Show error message or retry
                            AndroidUtil.showToast(context,"Internal Server Error!");
                        }
                    }
                });
            }
        });

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle accept button click
                ApiCalls.acceptFriendRequest(context, request.getId(), new ApiCalls.AcceptFriendRequestCallback() {
                    @Override
                    public void onFriendRequestAccepted(boolean success) {
                        if (success) {
                            // - Remove the item from the list
                            // - Notify the adapter of the change
                            AndroidUtil.showToast(context,"You are now friends!");
                            requestsList.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            notifyItemRangeChanged(holder.getAdapterPosition(), requestsList.size());
                        } else {
                            // Handle failed friend request acceptance
                            // Display an error message or retry logic
                            AndroidUtil.showToast(context,"Internal Server Error!");
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }
}
