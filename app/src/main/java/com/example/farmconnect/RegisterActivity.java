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

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;

    ImageView profie_image;
    Uri image;
    EditText userName,mobile,email,password,confirm_password;
    Button registerButton;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profie_image=findViewById(R.id.profile_image);
        userName=findViewById(R.id.username);
        mobile=findViewById(R.id.phone);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirm_password=findViewById(R.id.confirm_password);
        registerButton=findViewById(R.id.RegisterButton);
        login=findViewById(R.id.LoginText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(RegisterActivity.this,OTPVerificationActivity.class);
                intent.putExtra("phone",mobile.getText().toString());
                intent.putExtra("profile_image",image);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Method called when the user clicks on the ImageView
    public void selectImage(View view) {
        // Check for camera and storage permissions
        if (checkCameraPermission()) {
            // If permissions are granted, show the image selection dialog
            showImageSelectionDialog();
        } else {
            // If permissions are not granted, request them
            requestCameraPermission();
        }
    }
    // Check if camera and storage permissions are granted
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED;
    }

    // Request camera and storage permissions
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CAMERA_PERMISSION);
    }

    // Handle the result after requesting permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // If permissions are granted, show the image selection dialog
                showImageSelectionDialog();
            } else {
                // If permissions are not granted, show a message to the user
                Toast.makeText(this, "Camera and storage permissions are required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Show a dialog to let the user choose between camera and gallery
    private void showImageSelectionDialog() {
        // You can use a library or create a custom dialog for a better user experience
        // For simplicity, using a basic AlertDialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image")
                .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Take Photo
                                dispatchTakePictureIntent();
                                break;
                            case 1:
                                // Choose from Gallery
                                openGallery();
                                break;
                        }
                    }
                })
                .show();
    }
    // Method to open the gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    // Method to dispatch the camera intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Handle the result after selecting an image from the gallery or taking a picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    // Image selected from gallery
                    handleImageSelection(data.getData());
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    // Image captured from camera
                    handleImageCapture(data);
                    break;
            }
        }
    }

    // Handle image selected from gallery
    private void handleImageSelection(Uri selectedImageUri) {
        ImageView imageView = findViewById(R.id.profile_image);
        imageView.setImageURI(selectedImageUri);
        image=selectedImageUri;
    }

    // Handle image captured from camera
    private void handleImageCapture(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        ImageView imageView = findViewById(R.id.profile_image);
        imageView.setImageBitmap(imageBitmap);
    }
}