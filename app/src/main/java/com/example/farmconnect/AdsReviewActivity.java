package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.farmconnect.Adapters.AdsReviewRecyclerAdapter;
import com.example.farmconnect.Models.AllReview;
import com.example.farmconnect.utils.AndroidUtil;

import java.util.ArrayList;

public class AdsReviewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AdsReviewRecyclerAdapter adapter;
    ArrayList<AllReview> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_review);

        recyclerView = findViewById(R.id.ad_review_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new AdsReviewRecyclerAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);

        // Get the Intent that started this activity and extract the data
        Intent intent = getIntent();
        reviews = intent.getParcelableArrayListExtra("reviews");

        // Check if reviews is not null
        if (reviews != null) {
            // Now you can use the reviews list
            // For example, display the size of the list as a Toast message
            AndroidUtil.showToast(this, "Number of reviews: " + reviews.size());
            adapter.setAdsReviews(reviews);

            // Handle the reviews list (e.g., display in a RecyclerView)
        } else {
            // Handle the case when reviews is null
            AndroidUtil.showToast(this, "No reviews found");
        }
    }
}