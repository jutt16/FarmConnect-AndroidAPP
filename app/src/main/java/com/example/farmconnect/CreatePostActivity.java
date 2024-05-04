package com.example.farmconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.FirebaseUtil;
import com.example.farmconnect.utils.PostApiCalls;

public class CreatePostActivity extends AppCompatActivity {
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_MEDIA_PICK = 1;

    private ImageView imageView,user_image;
    private VideoView videoView;
    private MediaController mediaController;
    ProgressBar sendProgress;
    EditText description_edit_text;
    Button reselect,upload_post_btn;
    Uri postUri;
    Bitmap postBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //initializing view items
        imageView = findViewById(R.id.postImage);
        videoView = findViewById(R.id.postVideo);
        reselect = findViewById(R.id.reselect);
        user_image = findViewById(R.id.user_image);
        upload_post_btn = findViewById(R.id.upload_post_btn);
        description_edit_text = findViewById(R.id.description_edit_text);
        sendProgress = findViewById(R.id.sendProgress);

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getApplicationContext(),uri,user_image);
                    }
                });

        upload_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postUri != null) {
                    setProgressBar(true);
                    PostApiCalls.createPost(getApplicationContext(), postUri, AndroidUtil.getFileExtensionFromUri(getApplicationContext(), postUri), description_edit_text.getText().toString(), new PostApiCalls.PostCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // Handle success
                            Log.d("PostApiCalls", "Post created successfully: " + response);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setProgressBar(false);
                                    AndroidUtil.showToast(getApplicationContext(),"post created successfully!");
                                    navigateToHomePage();
                                }
                            });
                            // Optionally, perform any actions upon successful post creation
                        }

                        @Override
                        public void onFailure(Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setProgressBar(false);
                                    AndroidUtil.showToast(getApplicationContext(),"post create failed!");
                                    navigateToHomePage();
                                }
                            });
                            // Handle failure
                            Log.e("PostApiCalls", "Failed to create post: " + e.getMessage());
                            // Optionally, display an error message or perform any other failure actions
                        }
                    });
                } else {
                    // Handle the case where postBitmap is null
                    Log.e("PostApiCalls", "postBitmap is null");
                    // Optionally, display an error message or perform any other actions
                }
            }
        });

        // Create MediaController
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView); // Set anchor view to VideoView

        reselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        // Check if permission is granted
        if (isStoragePermissionGranted()) {
            // Permission is already granted, you can now access external storage
            // Do whatever you need to do here
            selectMedia();
        } else {
            // Permission is not granted, request it from the user
            requestStoragePermission();
        }
    }
    private void navigateToHomePage() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void setProgressBar(Boolean visible) {
        if(visible) {
            sendProgress.setVisibility(View.VISIBLE);
        } else {
            sendProgress.setVisibility(View.GONE);
        }
    }
    // Check if the permission is granted
    private boolean isStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    // Request the permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_STORAGE_PERMISSION);
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now access external storage
                // Do whatever you need to do here
                selectMedia();
            } else {
                // Permission denied
                // You may want to show a message to the user indicating why the permission is needed
            }
        }
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
        postBitmap = AndroidUtil.uriToBitmap(getApplicationContext(),postUri);

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