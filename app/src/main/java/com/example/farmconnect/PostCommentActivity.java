package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.farmconnect.Adapters.PostCommentAdapter;
import com.example.farmconnect.Models.PostComments;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.DateUtils;
import com.example.farmconnect.utils.PostApiCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostCommentActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PostCommentAdapter adapter;
    EditText comment_EditText;
    ImageButton comment_send_btn,back_btn;
    int postId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        //initialize recycler view
        recyclerView = findViewById(R.id.comments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostCommentAdapter(this);
        recyclerView.setAdapter(adapter);

        //initialize Views
        comment_EditText = findViewById(R.id.comment_EditText);
        comment_send_btn = findViewById(R.id.comment_send_btn);
        back_btn = findViewById(R.id.back_btn);

        //back event listener
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Check if post_id extra is passed
        if (getIntent().hasExtra("post_id")) {
            postId = getIntent().getIntExtra("post_id", 0);
            Log.d("Post Id", String.valueOf(postId));

            // Call API method to fetch post comments
            fetchPostComments(postId);
        }

        comment_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the commentOnPost method with a callback
                String comment = comment_EditText.getText().toString();
                if(comment.length()<1) {
                    AndroidUtil.showToast(getApplicationContext(),"empty comment is not allowed!");
                } else {
                    PostApiCalls.commentOnPost(getApplicationContext(), String.valueOf(postId), comment, new PostApiCalls.CommentOnPostCallback() {
                        @Override
                        public void onSuccess() {
                            // Handle success
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    comment_EditText.setText("");
                                    recreate();
                                    AndroidUtil.showToast(getApplicationContext(),"comment successfully!");
                                }
                            });
                            Log.d("PostApiCalls", "Comment posted successfully");
                            // Optionally, you can update your UI or perform any other actions here
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // Handle failure
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AndroidUtil.showToast(getApplicationContext(),"comment failed!");
                                }
                            });
                            Log.e("PostApiCalls", "Failed to post comment: " + e.getMessage());
                            // Optionally, you can show an error message or retry logic here
                        }
                    });

                }
            }
        });

    }
    // Method to fetch post comments from API
    private void fetchPostComments(int postId) {
        // Call the API method to get post comments
        PostApiCalls.getPostComments(this, String.valueOf(postId), new PostApiCalls.GetPostCommentsCallback() {
            @Override
            public void onSuccess(JSONArray comments) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Handle successful response
                        List<PostComments> postCommentsList = new ArrayList<>();

                        // Parse each comment object from the JSON array and create PostComments model objects
                        for (int i = 0; i < comments.length(); i++) {
                            try {
                                JSONObject commentObject = comments.getJSONObject(i);
                                int id = commentObject.getInt("id");
                                String comment = commentObject.getString("comment");
                                int userId = commentObject.getInt("user_id");
                                String userProfileImagePath = commentObject.getString("user_profile_image_path");
                                String userName = commentObject.getString("user_name");
                                Date created_at = DateUtils.parseDate(commentObject.getString("created_at"));

                                // Create a new PostComments object
                                PostComments postComments = new PostComments(id, comment, userId, userProfileImagePath, userName, created_at);
                                postCommentsList.add(postComments);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Update the adapter with the list of post comments
                        adapter.setPostComments(postCommentsList);
                    }
                });
            }


            @Override
            public void onFailure(Exception e) {
                // Handle API call failure
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}