package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.ApiCalls;

public class OtherProfileActivity extends AppCompatActivity {
    ImageView profilePic;
    TextView name_textview;
    TextView mobile_textview;
    TextView address_textview;
    Button unfriendButton;
    ImageButton back_btn;
    RecyclerView friends_list_recycler_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        // getting extras from intent
        String id = getIntent().getStringExtra("friend_id");
        String name = getIntent().getStringExtra("friend_name");
        String mobile = getIntent().getStringExtra("friend_mobile");
        String address = getIntent().getStringExtra("friend_address");
        String profile_image_url = getIntent().getStringExtra("friend_profile_img");

        // initialization
        profilePic = findViewById(R.id.profile_imageView);
        name_textview =findViewById(R.id.name_textView);
        mobile_textview = findViewById(R.id.phone_textView);
        address_textview = findViewById(R.id.address_textView);
        unfriendButton = findViewById(R.id.unfriend_btn);
        back_btn = findViewById(R.id.back_btn);
        friends_list_recycler_view = findViewById(R.id.friends_list_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        friends_list_recycler_view.setLayoutManager(layoutManager);

        // calling for getting friends
        ApiCalls.fetchFriendsofFriend(friends_list_recycler_view, id, getApplicationContext());

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Log.d("Profile URl", profile_image_url);
        AndroidUtil.setProfilePicByUrl(getApplicationContext(),profile_image_url,profilePic);
        name_textview.setText(name);
        mobile_textview.setText(mobile);
        address_textview.setText(address);

        profilePic.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(getApplicationContext(), FullScreenImageActivity.class);
                intent.putExtra("url", profile_image_url);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {
                Log.d("Error",e.getMessage());
                throw new RuntimeException(e);
            }
        });
        unfriendButton.setOnClickListener(view -> {
            ApiCalls.sendUnFriendRequest(getApplicationContext(), id, unfriendButton, success -> {
                if (success) {
//                    AndroidUtil.showToast(getApplicationContext(), "You are no longer friends!");
                } else {
                    // Handle failure logic
                    // Optionally, you can display an error message here
                    AndroidUtil.showToast(getApplicationContext(), "Internal Server Error!");
                }
            });
        });
    }
}