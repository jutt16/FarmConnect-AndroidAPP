package com.example.farmconnect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.StoryApiCalls;

import java.util.ArrayList;
import java.util.List;

public class CreateStoryActivity extends AppCompatActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button addFilesButton;
    private Button uploadPostButton;
    private CardView sliderCardView;

    ImageSlider imageSlider;

    private List<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        // Initialize views
        titleEditText = findViewById(R.id.title_text);
        descriptionEditText = findViewById(R.id.description_text);
        addFilesButton = findViewById(R.id.add_files_button);
        uploadPostButton = findViewById(R.id.upload_post_btn);
        imageSlider = findViewById(R.id.image_slider);
        sliderCardView = findViewById(R.id.slider);

        // Set up the Add Files button
        addFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Set up the Upload Post button
        uploadPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitStory();
            }
        });

    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // Multiple images selected
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                // Single image selected
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
            }

            // Show the image slider with selected images
            showImageSlider();
        }
    }

    private void showImageSlider() {
        List<SlideModel> slideModels = new ArrayList<>();
        for (Uri uri : imageUris) {
            slideModels.add(new SlideModel(uri.toString(),ScaleTypes.FIT));
        }
        imageSlider.setImageList(slideModels);
        sliderCardView.setVisibility(View.VISIBLE);
    }

    private void submitStory() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        // Validate inputs
        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            titleEditText.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            descriptionEditText.setError("Description is required");
            descriptionEditText.requestFocus();
            return;
        }

        if (imageUris.isEmpty()) {
            // Show some error message
            AndroidUtil.showToast(getApplicationContext(),"At least 1 image is required");
            return;
        }

        // TODO: Implement the logic to submit the story, e.g., upload to server
        StoryApiCalls.createStory(getApplicationContext(), description, imageUris, new StoryApiCalls.CreateStoryApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AndroidUtil.showToast(getApplicationContext(),"story created successfully!");
                        Log.d("Story Response",response);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AndroidUtil.showToast(getApplicationContext(),"story create failed!");
                        Log.d("Story Error",e.getMessage());
                    }
                });
            }
        });

        // For now, just display a message
        // Toast.makeText(this, "Story submitted", Toast.LENGTH_SHORT).show();
    }
}