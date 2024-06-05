package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.farmconnect.Adapters.StoryViewRecyclerAdapter;
import com.example.farmconnect.Models.StoryModel;
import com.example.farmconnect.utils.StoryApiCalls;
import com.example.farmconnect.utils.UserProfileApi;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoryViewActivity extends AppCompatActivity {

    int userId = 0;
    RecyclerView stories_recycler_view;
    StoryViewRecyclerAdapter adapter;
    List<StoryModel> storyModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);

        stories_recycler_view = findViewById(R.id.stories_recycler_view);
        stories_recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        getUserProfileId();

        storyModelList = new ArrayList<>();
        adapter = new StoryViewRecyclerAdapter(getApplicationContext(), storyModelList);
        stories_recycler_view.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StoryApiCalls.getCurrentUserStories(getApplicationContext(), userId, new StoryApiCalls.CurrentUserStoriesCallback() {
                    @Override
                    public void onSuccess(final ArrayList<StoryModel> stories) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Handle the success scenario, update the UI or do whatever with the stories
                                for (StoryModel story : stories) {
                                    Log.d("GetStoriesSuccess", "Story ID: " + story.id + " \nDescription: " + story.discription);
                                    // Additional logic to display stories in the UI
                                    storyModelList.add(story);
                                }
                                adapter.setStories(stories);
                            }
                        });
                    }

                    @Override
                    public void onFailure(final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Handle the failure scenario, show error message to the user
                                Log.e("GetStoriesFailure", "Failed to fetch stories: " + e.getMessage());
                            }
                        });
                    }
                });
            }
        }, 2000);
    }

    private void getUserProfileId() {
        UserProfileApi.getCurrentUserProfile(getApplicationContext(), new UserProfileApi.UserProfileCallback() {
            @Override
            public void onSuccess(String response) {
                // Handle successful response
                Log.d("UserProfile", "Success: " + response);

                try {
                    // Parse the response JSON
                    JSONObject jsonObject = new JSONObject(response);
                    userId = jsonObject.getInt("id");

                    // Use the id as needed
                    Log.d("UserProfile", "User ID: " + userId);
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
}
