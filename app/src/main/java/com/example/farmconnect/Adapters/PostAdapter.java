package com.example.farmconnect.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Models.Post;
import com.example.farmconnect.Models.PostLikes;
import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.R;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.DateUtils;
import com.example.farmconnect.utils.FirebaseUtil;
import com.example.farmconnect.utils.PostApiCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> postList;

    public void setPosts(List<Post> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }


    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);

        //get likes
        PostApiCalls.getPostsLikes(context, String.valueOf(post.getId()), new PostApiCalls.PostCallback() {
            @Override
            public void onSuccess(String response) {
                // Handle successful API response
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getBoolean("success")) {
                        JSONArray postsLikeArray = responseObject.getJSONArray("posts");
                        List<PostLikes> postLikesList = new ArrayList<>();
                        for (int i = 0; i < postsLikeArray.length(); i++) {
                            JSONObject postObject = postsLikeArray.getJSONObject(i);
                            // Parse post data from JSON
                            int id = postObject.getInt("id");
                            int userId = postObject.getInt("user_id");
                            // Parse other attributes similarly
                            String userName = postObject.getString("user_name");

//                            if(userName.equals(FirebaseUtil.currentUserName())) {
//                                // Set a new icon
//                                Drawable newIcon = ContextCompat.getDrawable(context, R.drawable.baseline_thumb_up_alt_24);  // Replace with your new icon
//                                holder.button_like.setCompoundDrawablesWithIntrinsicBounds(newIcon, null, null, null);
//
//                                // To reset the icon to none
//                                holder.button_like.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//                            }

                            String userProfileImagePath = postObject.getString("user_profile_image_path");
                            // Create Post object
                            PostLikes postLikes = new PostLikes(id, userId, userName,userProfileImagePath);
                            postLikesList.add(postLikes);
                        }
                    } else {
                        // Handle API error or no posts found
                        AndroidUtil.showToast(context,"Failed to load likes!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        // Bind data to views
        holder.userNameTextView.setText(post.getUser_name());
        holder.postTimeTextView.setText(post.getCreated_at().toString());
        holder.createdDateTextView.setText("created at "+post.getCreated_at().toString());
        holder.likes_textView.setText(post.getLikes()+" likes");
        holder.comments_textView.setText(post.getComments()+" comments");
//        Log.d("Description",post.getDiscription().toString());
        if(post.getDiscription()!="null") {
            holder.description_textView.setVisibility(View.VISIBLE);
            holder.description_textView.setText(post.getDiscription().toString());
        }
        else {
            holder.description_textView.setVisibility(View.GONE);
        }
        // Load user profile image using Glide
        AndroidUtil.setProfilePicByUrl(context,context.getResources().getString(R.string.api_base_url)+":8000"+post.getUser_profile_image_path(),holder.userProfileImageView);

        // Determine whether to display image or video
        if (post.getFile_type().equals(".mp4")) {
            MediaController mediaController;
            // Create MediaController
            mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.postVideoView); // Set anchor view to VideoView
            // Display video view and hide image view
            holder.postImageView.setVisibility(View.GONE);
            holder.postVideoView.setVisibility(View.VISIBLE);
            // Set the video URI to the VideoView
            holder.postVideoView.setVideoPath(context.getResources().getString(R.string.api_base_url)+":8000"+post.getFile_path());
            // Set MediaController to VideoView
            holder.postVideoView.setMediaController(mediaController);
            // Start playing the video
            holder.postVideoView.start();
        } else {
            // Display image view and hide video view
            holder.postImageView.setVisibility(View.VISIBLE);
            holder.postVideoView.setVisibility(View.GONE);
            AndroidUtil.displayPostByUrl(context,context.getResources().getString(R.string.api_base_url)+":8000"+post.getFile_path(),holder.postImageView);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Define views from post_layout.xml
        ImageView userProfileImageView;
        TextView userNameTextView;
        TextView postTimeTextView;
        ImageView postImageView;
        VideoView postVideoView;
        TextView createdDateTextView;
        TextView likes_textView,comments_textView,description_textView;
        Button button_like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            userProfileImageView = itemView.findViewById(R.id.poster_profile_pic);
            userNameTextView = itemView.findViewById(R.id.poster_username_textView);
            postTimeTextView = itemView.findViewById(R.id.posting_time_textView);
            postImageView = itemView.findViewById(R.id.post_image);
            postVideoView = itemView.findViewById(R.id.post_video);
            createdDateTextView = itemView.findViewById(R.id.posting_time_textView);
            likes_textView = itemView.findViewById(R.id.likes_textView);
            comments_textView = itemView.findViewById(R.id.comments_textView);
            description_textView = itemView.findViewById(R.id.description_textView);
            button_like = itemView.findViewById(R.id.button_like);
        }
    }
}
