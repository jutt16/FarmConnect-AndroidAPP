package com.example.farmconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Models.PostComments;
import com.example.farmconnect.R;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.DateUtils;

import java.util.List;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.CommentViewHolder>{
    private Context context;
    private List<PostComments> postCommentsList;
    public PostCommentAdapter(Context context) {
        this.context = context;
    }

    public void setPostComments(List<PostComments> postCommentsList) {
        this.postCommentsList = postCommentsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_comment_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        if (postCommentsList != null && position < postCommentsList.size()) {
            PostComments postComments = postCommentsList.get(position);
            AndroidUtil.setProfilePicByUrl(context,context.getResources().getString(R.string.api_base_url)+":8000"+postComments.getUser_profile_image_path(),holder.comment_user_imageView);
            holder.user_name.setText(postComments.getUser_name());
            holder.comment_text.setText(postComments.getComment());
            holder.comment_time.setText("created at"+DateUtils.formatDate(postComments.getCreated_at()));
        }
    }

    @Override
    public int getItemCount() {
        return postCommentsList != null ? postCommentsList.size() : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        // Define views from post_comment_layout.xml
        ImageView comment_user_imageView;
        TextView user_name,comment_text,comment_time;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            comment_user_imageView = itemView.findViewById(R.id.comment_user_imageView);
            user_name = itemView.findViewById(R.id.user_name);
            comment_text = itemView.findViewById(R.id.comment_text);
            comment_time = itemView.findViewById(R.id.comment_time);
        }
    }
}
