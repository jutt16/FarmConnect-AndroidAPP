package com.example.farmconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.farmconnect.Models.UserModel;
import com.example.farmconnect.utils.AndroidUtil;
import com.example.farmconnect.utils.ImageDownloader;
import com.example.farmconnect.utils.ImageManager;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    EditText mobileEditText;
    EditText passwordEditText;
    TextView register;
    Button loginButton;
    UserModel userModel;
    String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        register = findViewById(R.id.RegisterText);
        mobileEditText = findViewById(R.id.mobile);
        passwordEditText = findViewById(R.id.password);

        userModel = new UserModel();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLoginAPI(mobileEditText.getText().toString(),passwordEditText.getText().toString(),getApplicationContext());
//                AndroidUtil.showToast(getApplicationContext(),userModel.getUserName());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void callLoginAPI(String mobile, String password, Context context) {
        OkHttpClient client = new OkHttpClient();

        // Prepare the JSON request body with dynamic values
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("mobile", mobile);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return; // Exit method if JSON creation fails
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(getApplicationContext().getResources().getString(R.string.api_base_url) + ":8000/api/login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle response
                if (response.isSuccessful()) {
                    // Successful response
                    String responseBody = response.body().string();
                    Log.d("Response: ", responseBody);
//                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                    startActivity(intent);
                    // Handle successful login response here
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtil.showToast(getApplicationContext(),responseBody);
                        }
                    });
                    try {
                        AndroidUtil.parseAndSetToken(context,responseBody);
                        JSONObject json = new JSONObject(responseBody);
                        JSONObject data = json.getJSONObject("data");
                        userModel.setUserId(data.getString("user_id"));
                        userModel.setUserName(data.getString("name"));
                        userModel.setMobile(data.getString("mobile"));
                        userModel.setEmail(data.getString("email"));
                        userModel.setPassword(password);
                        profileImageUrl = data.getString("profile_image_path");
                        // Inside onResponse method after getting the profileImageUrl
                        String profileImagePath = data.getString("profile_image_path");
                        // Remove the escape character from the profile image path
                        profileImagePath = profileImagePath.replace("\\", "");
                        String profileImageUrl = getApplicationContext().getResources().getString(R.string.api_base_url) + ":8000" + profileImagePath;

                        Log.d("ImgURL","image url: "+profileImageUrl);

                        // Download the image using ImageDownloader
                        ImageDownloader.downloadImage(profileImageUrl, new ImageDownloader.OnImageDownloadedListener() {
                            @Override
                            public void onImageDownloaded(byte[] imageData) {
                                if (imageData != null) {
                                    // Convert byte array to Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                                    if (bitmap != null) {
                                        // Use the bitmap as needed
                                        // For example, set it to an ImageView
                                        // imageView.setImageBitmap(bitmap);
                                        ImageManager.getInstance().setImageBitmap(bitmap);
                                        Log.d("Image Downloaded", "Bitmap created successfully");
                                    } else {
                                        Log.e("Image Downloaded", "Failed to create bitmap");
                                    }
                                } else {
                                    Log.e("Image Downloaded", "Image data is null");
                                }
                            }

                            @Override
                            public void onDownloadFailed(IOException e) {
                                Log.e("Image Downloaded", "Failed to download image: " + e.getMessage());
                            }
                        });


                        Intent intent = new Intent(LoginActivity.this,OTPVerificationActivity.class);
                        intent.putExtra("user", userModel);
                        intent.putExtra("register",false);
                        startActivity(intent);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // Unsuccessful response
                    String errorBody = response.body().string();
                    Log.d("Error: ", errorBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtil.showToast(getApplicationContext(),"Login Failed!\nMobile or Password is incorrect"+errorBody);
                        }
                    });
                    // Handle error response here
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
                Log.d("Failure:", e.getMessage());
                // Handle failure to connect to the server
            }
        });
    }

}