package com.example.farmconnect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.farmconnect.utils.AndroidUtil;

public class CreatePostActivity extends AppCompatActivity {
    private static final int REQUEST_MEDIA_PICK = 1;

    private ImageView imageView,user_image;
    private VideoView videoView;
    private MediaController mediaController;
    Button reselect;
    Uri postUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //initializing view items
        imageView = findViewById(R.id.postImage);
        videoView = findViewById(R.id.postVideo);
        reselect = findViewById(R.id.reselect);
        user_image = findViewById(R.id.user_image);

        // Create MediaController
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView); // Set anchor view to VideoView

        reselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        // Trigger media selection when a button is clicked or any other UI interaction
        // For demonstration purposes, you can trigger media selection immediately in onCreate
        selectMedia();
    }

    private void selectMedia() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*"); // Allow selection of all types of files
        startActivityForResult(intent, REQUEST_MEDIA_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PICK && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedMediaUri = data.getData();
                String mediaUrl = selectedMediaUri.toString();
                loadMedia(mediaUrl);
            } else {
                // No media selected
                AndroidUtil.showToast(this, "No media selected.");
            }
        }
    }

    private void loadMedia(String mediaUrl) {
        // Determine the MIME type of the media using ContentResolver
        String mimeType = getContentResolver().getType(Uri.parse(mediaUrl));
        postUri = Uri.parse(mediaUrl);

        // Handle the media based on its MIME type
        if (mimeType != null) {
            if (mimeType.startsWith("image")) {
                // It's an image
                imageView.setVisibility(ImageView.VISIBLE);
                videoView.setVisibility(VideoView.GONE);
                Glide.with(this)
                        .load(mediaUrl)
                        .into(imageView);
            } else if (mimeType.startsWith("video")) {
                // It's a video
                imageView.setVisibility(ImageView.GONE);
                videoView.setVisibility(VideoView.VISIBLE);
                // Set the video URI to the VideoView
                videoView.setVideoURI(Uri.parse(mediaUrl));
                // Set MediaController to VideoView
                videoView.setMediaController(mediaController);
                // Start playing the video
                videoView.start();
            } else {
                // Unsupported media type
                AndroidUtil.showToast(this, "Unsupported media type.");
            }
        } else {
            // Unable to determine media type
            AndroidUtil.showToast(this, "Unable to determine media type.");
        }
    }
}