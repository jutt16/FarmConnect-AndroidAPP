package com.example.farmconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmconnect.Adapters.CurrentUserPostsAdapter;
import com.example.farmconnect.Adapters.PostAdapter;
import com.example.farmconnect.Models.Post;
import com.example.farmconnect.Models.PostLikes;
import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.ApiCalls;
import com.example.farmconnect.utils.DateUtils;
import com.example.farmconnect.utils.FirebaseUtil;
import com.example.farmconnect.utils.PostApiCalls;
import com.example.farmconnect.utils.UserProfileApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    ImageView profileMenu;
    ImageView profilePic;
    TextView name_textview;
    TextView mobile_textview;
    TextView email_textview;
    UserModel currentUserModel;
    ImageButton btn_back;
    RecyclerView friends_list_recycler_view,posts_list_recycler_view;
    Button create_story_btn,view_story_btn;
    CurrentUserPostsAdapter postAdapter;
    int currentUserId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getUserProfileId();

        profileMenu = findViewById(R.id.profile_menu);
        profilePic = findViewById(R.id.profile_imageView);
        name_textview =findViewById(R.id.name_textView);
        mobile_textview = findViewById(R.id.phone_textView);
        email_textview = findViewById(R.id.email_textView);
        btn_back = findViewById(R.id.back_btn);
        friends_list_recycler_view = findViewById(R.id.friends_list_recycler_view);
        posts_list_recycler_view = findViewById(R.id.posts_list_recycler_view);
        create_story_btn = findViewById(R.id.create_story_btn);
        view_story_btn = findViewById(R.id.view_story_btn);

        //view story btn onclick
        view_story_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),StoryViewActivity.class);
                startActivity(intent);
            }
        });

        //create story btn onclick
        create_story_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CreateStoryActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        friends_list_recycler_view.setLayoutManager(layoutManager);
        posts_list_recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        postAdapter = new CurrentUserPostsAdapter(new ArrayList<>(),getApplicationContext());

        // Call the API to fetch posts
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                fetchPosts();
//            }
//        },1000);

        posts_list_recycler_view.setAdapter(postAdapter);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //get profile data from firebase
        setProfile();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),FullScreenImageActivity.class);
                intent.putExtra("currentUser",true);
                startActivity(intent);
            }
        });

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        ApiCalls.fetchFriends(friends_list_recycler_view,getApplicationContext());
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.edit_profile) {
                // Handle edit profile action
//                AndroidUtil.showToast(ProfileActivity.this, "Edit Profile clicked");
                Intent intent = new Intent(getApplicationContext(),ProfileUpdateActivity.class);
                startActivity(intent);
                return true;
            }
            if(item.getItemId() == R.id.goto_home) {
                // Handle go to home action
//                AndroidUtil.showToast(ProfileActivity.this, "Go to Home clicked");
                onBackPressed();
                return true;
            }
            if(item.getItemId() == R.id.logout_btn) {
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseUtil.logout();
                            Intent intent = new Intent(getApplicationContext(),SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
            return false;
        });
        popupMenu.show();
    }

    //get data
    public void setProfile(){
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getApplicationContext(),uri,profilePic);
                    }
                });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            name_textview.setText(currentUserModel.getUserName());
            mobile_textview.setText(currentUserModel.getMobile());
            email_textview.setText(currentUserModel.getEmail());
//            AndroidUtil.showToast(getApplicationContext(), currentUserModel.getUserName()+"\n"+currentUserModel.getMobile()+"\n"+currentUserModel.getEmail());

        });
    }
    private void getUserProfileId(){
        UserProfileApi.getCurrentUserProfile(getApplicationContext(), new UserProfileApi.UserProfileCallback() {

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
    }
    private void fetchPosts() {
        // Use the id as needed
        Log.d("UserProfile", "User ID in fetch post: " + currentUserId);
        PostApiCalls.getCurrentUsersPosts(getApplicationContext(), String.valueOf(currentUserId), new PostApiCalls.PostCallback() {
            @Override
            public void onSuccess(String response) {
                // Handle successful API response
                try {
                    JSONObject responseObject = new JSONObject(response);
                    if (responseObject.getBoolean("success")) {
                        JSONArray postsArray = responseObject.getJSONArray("posts");
                        List<Post> postList = new ArrayList<>();
                        for (int i = 0; i < postsArray.length(); i++) {
                            JSONObject postObject = postsArray.getJSONObject(i);
                            // Parse post data from JSON
                            int id = postObject.getInt("id");
                            int userId = postObject.getInt("user_id");
                            // Parse other attributes similarly
                            String filePath = postObject.getString("file_path");
                            String fileType = postObject.getString("file_type");
                            int likes = postObject.getInt("likes");
                            int comments = postObject.getInt("comments");
                            String description = postObject.getString("discription");
                            String userName = postObject.getString("user_name");
                            String userProfileImagePath = postObject.getString("user_profile_image_path");
                            // Convert created_at string to Date
                            Date createdAt = DateUtils.parseDate(postObject.getString("created_at"));
                            //Date createdAt = new Date();
                            // Create Post object
                            Post post = new Post(id, userId, null, filePath, fileType, likes, comments, description, createdAt, userProfileImagePath, userName);
                            postList.add(post);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update RecyclerView adapter with fetched posts
                                postAdapter.setPosts(postList);
                                postAdapter.notifyDataSetChanged();
                                // Delay scroll to position 0 to allow layout process to complete
                                posts_list_recycler_view.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Scroll to the top after layout is complete
                                        posts_list_recycler_view.scrollToPosition(0);
                                    }
                                }, 1000);
                            }
                        });


                    } else {
                        // Handle API error or no posts found
                        AndroidUtil.showToast(getApplicationContext(),"Failed to load posts!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle API call failure
                AndroidUtil.showToast(getApplicationContext(),"something went wrong!\nfailed to load posts!");
            }
        });
    }
}
