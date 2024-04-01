package com.example.farmconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    ImageView profileImage;
    Uri image;
    EditText userName, mobile, email, password, confirmPassword;
    Button registerButton;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profileImage = findViewById(R.id.profile_image);
        userName = findViewById(R.id.username);
        mobile = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.RegisterButton);
        login = findViewById(R.id.LoginText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameValue = userName.getText().toString();
                String mobileValue = mobile.getText().toString();
                String passwordValue = password.getText().toString();
                String confirmPasswordValue = confirmPassword.getText().toString();
                String emailValue = email.getText().toString();

                // Inside your onClick method for registerButton
                if (validateFields(usernameValue, mobileValue, passwordValue, confirmPasswordValue, emailValue)) {
                    UserModel user = new UserModel(usernameValue, mobileValue, emailValue, passwordValue, image);
                    Intent intent = new Intent(RegisterActivity.this, OTPVerificationActivity.class);
                    // Pass the UserModel instance to the next activity
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateFields(String username, String mobile, String password, String confirmPassword, String email) {
        if (username.isEmpty() || mobile.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || image == null) {
            AndroidUtil.showToast(getApplicationContext(), "All fields are required except email");
            return false;
        }

        if (password.length() < 8) {
            AndroidUtil.showToast(getApplicationContext(), "Password must be at least 8 characters");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            AndroidUtil.showToast(getApplicationContext(), "Passwords do not match");
            return false;
        }

        if (!email.isEmpty() && !AndroidUtil.isValidEmail(email)) {
            AndroidUtil.showToast(getApplicationContext(), "Please enter a valid email address");
            return false;
        }

        return true;
    }

    public void selectImage(View view) {
        if (checkCameraPermission()) {
            showImageSelectionDialog();
        } else {
            requestCameraPermission();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showImageSelectionDialog();
            } else {
                AndroidUtil.showToast(getApplicationContext(), "Camera and storage permissions are required");
            }
        }
    }

    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image")
                .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                dispatchTakePictureIntent();
                                break;
                            case 1:
                                openGallery();
                                break;
                        }
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    handleImageSelection(data.getData());
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    handleImageCapture(data);
                    break;
            }
        }
    }

    private void handleImageSelection(Uri selectedImageUri) {
        profileImage.setImageURI(selectedImageUri);
        image = selectedImageUri;
    }

    private void handleImageCapture(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        profileImage.setImageBitmap(imageBitmap);
    }
}
