package com.example.farmconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Models.AllReview;
import com.example.farmconnect.Models.PostComments;
import com.example.farmconnect.R;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.DateUtils;

import java.util.List;

public class AdsReviewRecyclerAdapter extends RecyclerView.Adapter<AdsReviewRecyclerAdapter.AdsReviewViewHolder>{
    private Context context;
    private List<AllReview> allReviewList;
    public AdsReviewRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setAdsReviews(List<AllReview> allReviewList) {
        this.allReviewList = allReviewList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdsReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_comment_layout, parent, false);
        return new AdsReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdsReviewViewHolder holder, int position) {
        if (allReviewList != null && position < allReviewList.size()) {
            AllReview review = allReviewList.get(position);
            AndroidUtil.setProfilePicByUrl(context,context.getResources().getString(R.string.api_base_url)+":8000"+review.getUser().getProfile_image_path(),holder.comment_user_imageView);
            holder.user_name.setText(review.getUser().getName());
            holder.comment_text.setText(review.getComment());
            holder.comment_time.setText("created at "+ DateUtils.formatDate(review.getCreated_at()));
        }
    }

    @Override
    public int getItemCount() {
        return allReviewList != null ? allReviewList.size() : 0;
    }

    public static class AdsReviewViewHolder extends RecyclerView.ViewHolder {
        // Define views from post_comment_layout.xml
        ImageView comment_user_imageView;
        TextView user_name,comment_text,comment_time;

        public AdsReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            comment_user_imageView = itemView.findViewById(R.id.comment_user_imageView);
            user_name = itemView.findViewById(R.id.user_name);
            comment_text = itemView.findViewById(R.id.comment_text);
            comment_time = itemView.findViewById(R.id.comment_time);
        }
    }
}
