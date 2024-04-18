package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.FirebaseUtil;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(FirebaseUtil.isLoggedIn()) {
            Bundle extras = getIntent().getExtras();
            if(extras != null && extras.containsKey("userId")) {
                // From notifications with a userId
                String userId = extras.getString("userId");
                if (!TextUtils.isEmpty(userId)) {
                    FirebaseUtil.allUserCollectionReference().document(userId).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    UserModel model = task.getResult().toObject(UserModel.class);

                                    Intent mainIntent = new Intent(this, MainActivity.class);
                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(mainIntent);

                                    Intent intent = new Intent(this, ChatActivity.class);
                                    AndroidUtil.passUserModelAsIntent(intent, model);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                } else {
                    redirectToMain();
                }
            } else {
                redirectToMain();
            }
        } else {
            // User is not logged in, redirect to Login
            new Handler().postDelayed(() -> {
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    private void redirectToMain() {
        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}
