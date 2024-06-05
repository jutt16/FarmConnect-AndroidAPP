package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.farmconnect.Adapters.PostAdapter;
import com.example.farmconnect.Adapters.SearchPostRecyclerAdapter;
import com.example.farmconnect.Models.Post;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.DateUtils;
import com.example.farmconnect.utils.PostApiCalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchPostActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton searchBtn;
    RecyclerView recyclerView;
    SearchPostRecyclerAdapter postAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_post);

        //initialization
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchInput = findViewById(R.id.search_post_description_btn);
        searchBtn = findViewById(R.id.search_post_btn);
        recyclerView = findViewById(R.id.search_post_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new SearchPostRecyclerAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(postAdapter);

        searchInput.requestFocus();

        searchBtn.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();
            if(searchTerm.isEmpty()){
                searchInput.setError("Invalid search");
                return;
            }
            searchForPosts(searchTerm);
            // Handle back button press on the toolbar
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        });
    }
    private void searchForPosts(String description) {
        PostApiCalls.searchPost(getApplicationContext(), description, new PostApiCalls.SearchPostCallback() {
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
                        // Update RecyclerView adapter with fetched posts
                        postAdapter.setPosts(postList);
                    } else {
                        // Handle API error or no posts found
                        AndroidUtil.showToast(getApplicationContext(),"Failed to search posts!");
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