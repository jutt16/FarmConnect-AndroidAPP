package com.example.farmconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.FileUtils;
import com.example.farmconnect.utils.FirebaseUtil;
import com.example.farmconnect.utils.ImageManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OTPVerificationActivity extends AppCompatActivity {

    String phoneNumber;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    EditText otpInput;
    Button btnVerifyOTP;
    ProgressBar progressBar;
    TextView resendOTPTextView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    UserModel userModel;
    Boolean register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        register = true;
        if(getIntent().hasExtra("register")) {
            register = getIntent().getBooleanExtra("register",false);
        }

        otpInput = findViewById(R.id.otpCode);
        btnVerifyOTP = findViewById(R.id.OTPVerificationButton);
        progressBar = findViewById(R.id.OTPProgressBar);
        resendOTPTextView = findViewById(R.id.ResendOTPText);

        userModel = getIntent().getParcelableExtra("user");
        userModel.setUserId(FirebaseUtil.currentUserId());
        userModel.setCreatedTimestamp(Timestamp.now());
        phoneNumber = userModel.getMobile();
        sendOtp(phoneNumber, false);

        btnVerifyOTP.setOnClickListener(view -> {
            String enteredOTP = otpInput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOTP);
            register(credential, userModel);
        });

        resendOTPTextView.setOnClickListener(view -> {
            sendOtp(phoneNumber, true);
        });
    }

    void sendOtp(String phoneNumber, Boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                register(phoneAuthCredential, userModel);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                AndroidUtil.showToast(getApplicationContext(), "OTP verification failed");
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtil.showToast(getApplicationContext(), "OTP sent successfully");
                                setInProgress(false);
                            }
                        });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void startResendTimer() {
        resendOTPTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    timeoutSeconds--;
                    resendOTPTextView.setText("Resend OTP in " + timeoutSeconds + " seconds");
                    if (timeoutSeconds <= 0) {
                        timeoutSeconds = 60L;
                        timer.cancel();
                        resendOTPTextView.setEnabled(true);
                    }
                });
            }
        }, 0, 1000);
    }

    void setInProgress(Boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnVerifyOTP.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnVerifyOTP.setVisibility(View.VISIBLE);
        }
    }

    void register(PhoneAuthCredential phoneAuthCredential, UserModel userModel) {
        // Register and go to the next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    // Fetch existing user data
                    FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if(!register) {
                                    proceedToNextActivity(userModel);
                                    return;
                                }
                                if (document.exists()) {
                                    // Document exists, update the data
                                    updateUserDocument(document, userModel);
                                } else {
                                    // Document doesn't exist, create it
                                    createNewUserDocument(userModel);
                                }
                            } else {
                                AndroidUtil.showToast(getApplicationContext(), "Failed to retrieve user data: " + task.getException().getMessage());
                            }
                        }
                    });
                } else {
                    AndroidUtil.showToast(getApplicationContext(), "OTP verification failed");
                }
            }
        });
    }

    private void createNewUserDocument(UserModel userModel) {
        setInProgress(true);
        // Set timestamp and user ID
        userModel.setCreatedTimestamp(Timestamp.now());
        userModel.setUserId(FirebaseUtil.currentUserId());

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    if(register){
                        storeImage();
                        registerAPI(userModel,getApplicationContext());
                    }
                } else {
                    AndroidUtil.showToast(getApplicationContext(), "Failed to create user document: " + task.getException().getMessage());
                }
            }
        });
    }

    private void updateUserDocument(DocumentSnapshot document, UserModel userModel) {
        setInProgress(true);
        // Set timestamp and user ID
        userModel.setCreatedTimestamp(Timestamp.now());
        userModel.setUserId(FirebaseUtil.currentUserId());

        document.getReference().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    if(register)
                    {
                        storeImage();
                        registerAPI(userModel,getApplicationContext());
                    }
                } else {
                    AndroidUtil.showToast(getApplicationContext(), "Failed to update user document: " + task.getException().getMessage());
                }
            }
        });
    }

    public void storeImage() {
        // Convert Bitmap to StoryFile
        File imageFile = AndroidUtil.convertBitmapToFile(ImageManager.getInstance().getImageBitmap(), getApplicationContext());

        // Upload StoryFile to Firebase Storage
        StorageReference imageRef = FirebaseUtil.getCurrentProfilePicStorageRef();

        UploadTask uploadTask = imageRef.putFile(Uri.fromFile(imageFile));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Image uploaded successfully
                // Handle success
                AndroidUtil.showToast(getApplicationContext(),"Image Uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful upload
                AndroidUtil.showToast(getApplicationContext(),"Image Upload Failed");
            }
        });

    }

    private void proceedToNextActivity(UserModel userModel) {
        Intent intent = new Intent(OTPVerificationActivity.this, MainActivity.class);
        intent.putExtra("user", userModel);
        AndroidUtil.showToast(getApplicationContext(), "OTP verified successfully");
        startActivity(intent);
    }

    private void registerAPI(UserModel userModel, Context context) {
        // Get the bitmap from ImageManager
        Bitmap bitmap = ImageManager.getInstance().getImageBitmap();
        if (bitmap == null) {
            // Handle error: Bitmap is null
            Log.e("registerAPI", "Bitmap is null");
            return;
        }

        // Convert the Bitmap to a StoryFile
        File imageFile = FileUtils.createImageFileFromBitmap(getApplicationContext(), bitmap);
        if (imageFile == null) {
            // Handle error: Image file creation failed
            Log.e("registerAPI", "Failed to create image file");
            return;
        }

        // OkHttpClient setup
        OkHttpClient client = new OkHttpClient();

        // Prepare the request body
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("profile_image", imageFile.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), imageFile))
                .addFormDataPart("user_id", userModel.getUserId())
                .addFormDataPart("name", userModel.getUserName())
                .addFormDataPart("role", "android_user")
                .addFormDataPart("status", "1")
                .addFormDataPart("mobile", userModel.getMobile())
                .addFormDataPart("password", userModel.getPassword())
                .addFormDataPart("email", userModel.getEmail())
                .addFormDataPart("password_confirmation", userModel.getPassword())
                .build();

        // Prepare the request
        Request request = new Request.Builder()
                .url(getApplicationContext().getResources().getString(R.string.api_base_url) + ":8000/api/register")
                .post(body)
                .build();

        // Send the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Check if the response is successful (status code 200)
                if (response.isSuccessful()) {
                    // Handle success response
                    String responseBody = response.body().string();
                    Log.d("Success: ", responseBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtil.showToast(context, "Registered Successfully!");
                            AndroidUtil.parseAndSetToken(context,responseBody);
                            proceedToNextActivity(userModel);
                        }
                    });
                } else {
                    // Handle failure response
                    String responseBody = response.body().string();
                    Log.d("Failure1: ", responseBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtil.showToast(getApplicationContext(),"Registration Failed!"+responseBody);
                            gotoRegisterActivity();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure to connect to the server
                e.printStackTrace();
                Log.d("Failure2:", e.getMessage());
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtil.showToast(getApplicationContext(), "Internal Server Error\nRegistration Failed");
//                            OTPVerificationActivity.super.onBackPressed();
                            gotoRegisterActivity();
                        }
                    });
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    public void gotoRegisterActivity(){
        Intent intent = new Intent(OTPVerificationActivity.this,RegisterActivity.class);
        startActivity(intent);
    }


}
