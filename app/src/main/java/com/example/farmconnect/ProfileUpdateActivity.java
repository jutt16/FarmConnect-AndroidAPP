package com.example.farmconnect;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.FirebaseUtil;
import com.example.farmconnect.utils.UserProfileApi;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ProfileUpdateActivity extends AppCompatActivity {
    ImageView profilePic;
    EditText usernameInput;
    EditText phoneInput;
    EditText emailInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data!=null && data.getData()!=null){
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getApplicationContext(),selectedImageUri,profilePic);

                        }
                    }
                }

        );

        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        phoneInput = findViewById(R.id.profile_phone);
        emailInput = findViewById(R.id.profile_email);
        updateProfileBtn = findViewById(R.id.profile_update_btn);
        progressBar = findViewById(R.id.profile_progressbar);
        logoutBtn = findViewById(R.id.logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener((v -> {
            updateBtnClick();
        }));

        logoutBtn.setOnClickListener((v)->{

            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        FirebaseUtil.logout();
                        Intent intent = new Intent(getApplicationContext(),SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });

        });

        profilePic.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512,512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickLauncher.launch(intent);
                            return null;
                        }
                    });
        });
    }
    void updateBtnClick(){
        String newUsername = usernameInput.getText().toString();
        if(newUsername.isEmpty()){
            usernameInput.setError("Username length should not be empty!");
            return;
        }
        currentUserModel.setUserName(newUsername);
        currentUserModel.setEmail(emailInput.getText().toString());
        setInProgress(true);

        if (selectedImageUri!=null){
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        updatetoFirestore();
                        UserProfileApi.updateProfilePic(this, selectedImageUri, new UserProfileApi.UpdateProfilePicCallback() {
                            @Override
                            public void onSuccess(String response) {
                                // Handle successful response
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AndroidUtil.showToast(getApplicationContext(),"Profile pic updated successfully");
                                    }
                                });
                                Log.d("UpdateProfilePic", "Success: " + response);
                            }

                            @Override
                            public void onFailure(IOException e) {
                                // Handle failure
                                Log.e("UpdateProfilePic", "Failure: " + e.getMessage(), e);
                            }
                        });
                    });
        }else {
            updatetoFirestore();
        }
    }
    void updatetoFirestore(){
        FirebaseUtil.currentUserDetails().set(currentUserModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()){
                UserProfileApi.updateProfile(getApplicationContext(), currentUserModel.getUserName(), currentUserModel.getMobile(), currentUserModel.getEmail(), new UserProfileApi.UpdateProfileCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // Handle successful response
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil.showToast(getApplicationContext(),"Updated successfully");
                            }
                        });
                        Log.d("UpdateProfile", "Success: " + response);
                    }

                    @Override
                    public void onFailure(IOException e) {
                        // Handle failure
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil.showToast(getApplicationContext(),"Server Error!");
                            }
                        });
                        Log.e("UpdateProfile", "Failure: " + e.getMessage(), e);
                    }
                });
//                AndroidUtil.showToast(getApplicationContext(),"Updated successfully");
            }else {
                AndroidUtil.showToast(getApplicationContext(),"Updated failed");
            }

        });
    }
    void getUserData(){
        setInProgress(true);

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getApplicationContext(),uri,profilePic);
                    }
                });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            currentUserModel = task.getResult().toObject(UserModel.class);
            usernameInput.setText(currentUserModel.getUserName());
            phoneInput.setText(currentUserModel.getMobile());
            emailInput.setText(currentUserModel.getEmail());

        });

    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            updateProfileBtn.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            updateProfileBtn.setVisibility(View.VISIBLE);
        }
    }
}