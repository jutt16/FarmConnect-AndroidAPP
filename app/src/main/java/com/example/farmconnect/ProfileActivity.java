package com.example.farmconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfileActivity extends AppCompatActivity {
    ImageView profileMenu;
    ImageView profilePic;
    TextView name_textview;
    TextView mobile_textview;
    TextView email_textview;
    UserModel currentUserModel;
    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileMenu = findViewById(R.id.profile_menu);
        profilePic = findViewById(R.id.profile_imageView);
        name_textview =findViewById(R.id.name_textView);
        mobile_textview = findViewById(R.id.phone_textView);
        email_textview = findViewById(R.id.email_textView);
        btn_back = findViewById(R.id.back_btn);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        //get profile data from firebase
        setProfile();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),FullScreenImageActivity.class);
                intent.putExtra("currentUser",true);
                startActivity(intent);
            }
        });

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.edit_profile) {
                // Handle edit profile action
//                AndroidUtil.showToast(ProfileActivity.this, "Edit Profile clicked");
                Intent intent = new Intent(getApplicationContext(),ProfileUpdateActivity.class);
                startActivity(intent);
                return true;
            }
            if(item.getItemId() == R.id.goto_home) {
                // Handle go to home action
//                AndroidUtil.showToast(ProfileActivity.this, "Go to Home clicked");
                onBackPressed();
                return true;
            }
            if(item.getItemId() == R.id.logout_btn) {
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
            }
            return false;
        });
        popupMenu.show();
    }

    //get data
    public void setProfile(){
        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(getApplicationContext(),uri,profilePic);
                    }
                });

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            currentUserModel = task.getResult().toObject(UserModel.class);
            name_textview.setText(currentUserModel.getUserName());
            mobile_textview.setText(currentUserModel.getMobile());
            email_textview.setText(currentUserModel.getEmail());
//            AndroidUtil.showToast(getApplicationContext(), currentUserModel.getUserName()+"\n"+currentUserModel.getMobile()+"\n"+currentUserModel.getEmail());

        });
    }
}
