package com.example.farmconnect;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.farmconnect.Adapters.PostAdapter;
import com.example.farmconnect.Models.Post;
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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    Button create_post_btn;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        create_post_btn = view.findViewById(R.id.create_post_btn);
        recyclerView = view.findViewById(R.id.posts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), new ArrayList<>());

        // Call the API to fetch posts
        fetchPosts();

        recyclerView.setAdapter(postAdapter);

        create_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CreatePostActivity.class);
                getContext().startActivity(intent);
            }
        });
        return view;
    }

    private void fetchPosts() {
        PostApiCalls.getPosts(getContext(),new PostApiCalls.PostCallback() {
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
                        AndroidUtil.showToast(getContext(),"Failed to load posts!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle API call failure
                AndroidUtil.showToast(getContext(),"something went wrong!\nfailed to load posts!");
            }
        });
    }
}
