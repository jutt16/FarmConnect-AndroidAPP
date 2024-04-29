package com.example.farmconnect;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.FirebaseUtil;
import java.io.File;

public class FullScreenImageActivity extends AppCompatActivity {
    SubsamplingScaleImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        photoView = findViewById(R.id.photo_view);

        if (getIntent().hasExtra("otherUser")) {
            UserModel model = getIntent().getParcelableExtra("otherUser");
            FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri uri = task.getResult();
                            loadImage(uri);
                        }
                    });
        }
        if(getIntent().hasExtra("currentUser")) {
            FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Uri uri = task.getResult();
                            loadImage(uri);
                        }
                    });
        }
        if(getIntent().hasExtra("url")) {
            loadImageByUrl(getIntent().getStringExtra("url"));
        }
    }
    public void loadImageByUrl(String url) {
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(url)
                .apply(RequestOptions.fitCenterTransform())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        photoView.setImage(ImageSource.bitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        // No implementation needed
                    }
                });
    }
    public void loadImage(Uri uri) {
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(uri)
                .apply(RequestOptions.fitCenterTransform())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        photoView.setImage(ImageSource.bitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        // No implementation needed
                    }
                });
    }
}
