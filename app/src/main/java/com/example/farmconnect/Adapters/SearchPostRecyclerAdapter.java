package com.example.farmconnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
        import android.widget.VideoView;
        import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
        import com.bumptech.glide.Glide;
        import com.example.farmconnect.Models.Post;
import com.example.farmconnect.Models.PostLikes;
import com.example.farmconnect.PostCommentActivity;
import com.example.farmconnect.R;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.PostApiCalls;
import com.example.farmconnect.utils.UserProfileApi;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
        import java.util.List;

public class SearchPostRecyclerAdapter extends RecyclerView.Adapter<SearchPostRecyclerAdapter.SearchPostViewHolder> {

    private List<Post> postList;
    private Context context;

    public SearchPostRecyclerAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    public void setPosts(List<Post> posts) {
        this.postList = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_layout, parent, false);
        return new SearchPostRecyclerAdapter.SearchPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPostViewHolder holder, int position) {
        Post post = postList.get(position);

        UserProfileApi.getCurrentUserProfile(context, new UserProfileApi.UserProfileCallback() {
            int currentUserId;

            @Override
            public void onSuccess(String response) {
                // Handle successful response
                Log.d("UserProfile", "Success: " + response);

                try {
                    // Parse the response JSON
                    JSONObject jsonObject = new JSONObject(response);
                    currentUserId = jsonObject.getInt("id");

                    // Use the id as needed
                    Log.d("UserProfile", "User ID: " + currentUserId);

                    // Now that we have the currentUserId, we can fetch the likes
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
                                        if (userId == currentUserId) {
                                            // Set a new icon
                                            Log.d("user id",String.valueOf(userId));
                                            Log.d("current_user id",String.valueOf(currentUserId));
                                            holder.button_like.setIcon(context.getDrawable(R.drawable.baseline_thumb_up_alt_24));
                                            holder.button_like.setIconTint(context.getResources().getColorStateList(R.color.red));
                                            // To reset the icon to none
                                            // holder.button_like.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);  // Uncomment if you want to reset the icon
                                        }
                                        else {
                                            holder.button_like.setIcon(context.getDrawable(R.drawable.ic_reaction));
                                            holder.button_like.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.darker_gray)));
                                        }

                                        String userProfileImagePath = postObject.getString("user_profile_image_path");
                                        String userName = postObject.getString("user_name");  // You need to fetch this from the JSON if it is available
                                        // Create Post object
                                        PostLikes postLikes = new PostLikes(id, userId, userName, userProfileImagePath);
                                        postLikesList.add(postLikes);
                                    }
                                } else {
                                    // Handle API error or no posts found
                                    AndroidUtil.showToast(context, "Failed to load likes!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("PostLikes", "Failed to get likes: " + e.getMessage(), e);
                        }
                    });
                } catch (Exception e) {
                    Log.e("UserProfile", "Error parsing JSON: " + e.getMessage(), e);
                }
            }

            @Override
            public void onFailure(IOException e) {
                // Handle failure
                Log.e("UserProfile", "Failure: " + e.getMessage(), e);
            }
        });

        //like or unlike event
        holder.button_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.button_like.getIconTint().equals(context.getResources().getColorStateList(R.color.red))) {
                    //call unlike api
                    // Call the dislikePost method with a callback
                    PostApiCalls.dislikePost(context, String.valueOf(post.getId()), new PostApiCalls.disLikePostCallback() {
                        @Override
                        public void onSuccess() {
                            // Update UI on the main thread
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.button_like.setIcon(context.getDrawable(R.drawable.ic_reaction));
                                    holder.button_like.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.darker_gray)));
                                    AndroidUtil.showToast(context,"Post disliked successfully");
                                    // Handle success
                                    Log.d("PostApiCalls", "Post disliked successfully");
                                }
                            });
                        }

                        @Override
                        public void onFailure(int errorCode) {
                            // Handle failure
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    AndroidUtil.showToast(context,"Post disliked failed");
                                }
                            });
                            Log.e("PostApiCalls", "Failed to dislike post: " + errorCode);
                        }
                    });
                } else {
                    //call like api
                    // Call the likePost method with a callback
                    PostApiCalls.likePost(context, String.valueOf(post.getId()), new PostApiCalls.LikePostCallback() {
                        @Override
                        public void onSuccess() {
                            // Update UI on the main thread
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    holder.button_like.setIcon(context.getDrawable(R.drawable.baseline_thumb_up_alt_24));
                                    holder.button_like.setIconTint(context.getResources().getColorStateList(R.color.red));
                                    AndroidUtil.showToast(context,"Post liked successfully");
                                    // Handle success
                                    Log.d("PostApiCalls", "Post liked successfully");
                                }
                            });
                        }

                        @Override
                        public void onFailure(int errorCode) {
                            // Handle failure
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    AndroidUtil.showToast(context,"Post liked failed");
                                }
                            });
                            Log.e("PostApiCalls", "Failed to like post: " + errorCode);
                        }
                    });

                }
            }
        });

        //comment button onclick
        holder.button_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostCommentActivity.class);
                intent.putExtra("post_id",post.getId());
                context.startActivity(intent);
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
        AndroidUtil.setProfilePicByUrl(context,context.getResources().getString(R.string.api_base_url)+":8000/"+post.getUser_profile_image_path(),holder.userProfileImageView);

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

    public static class SearchPostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImageView;
        TextView userNameTextView;
        TextView postTimeTextView;
        ImageView postImageView;
        VideoView postVideoView;
        TextView createdDateTextView;
        TextView likes_textView,comments_textView,description_textView;
        MaterialButton button_like,button_comment;
        public SearchPostViewHolder(@NonNull View itemView) {
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
            button_comment = itemView.findViewById(R.id.button_comment);
        }
    }
}
