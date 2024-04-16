package com.example.farmconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

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
                    registerAPI(userModel);
                    proceedToNextActivity(userModel);
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
                    registerAPI(userModel);
                    proceedToNextActivity(userModel);
                } else {
                    AndroidUtil.showToast(getApplicationContext(), "Failed to update user document: " + task.getException().getMessage());
                }
            }
        });
    }

    private void proceedToNextActivity(UserModel userModel) {
        Intent intent = new Intent(OTPVerificationActivity.this, LoginActivity.class);
        intent.putExtra("user", userModel);
        AndroidUtil.showToast(getApplicationContext(), "OTP verified successfully");
        startActivity(intent);
    }

    private void registerAPI(UserModel userModel) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        // Convert the Bitmap to a File
        File imageFile = FileUtils.createImageFileFromBitmap(getApplicationContext(), ImageManager.getInstance().getImageBitmap());
        if (imageFile == null) {
            // Handle error creating file
            return;
        }

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
                .url(getApplicationContext().getResources().getString(R.string.api_base_url) + ":8000/api/user/store")
                .method("POST", body)
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
                } else {
                    // Handle failure response
                    String responseBody = response.body().string();
                    Log.d("Failure1: ", responseBody);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure to connect to the server
                e.printStackTrace();
                Log.d("Failure2:", e.getMessage());
                AndroidUtil.showToast(getApplicationContext(), "Failure: " + e.getMessage());
            }
        });
    }

}
