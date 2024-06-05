package com.example.farmconnect.utils;

import android.content.Context;
import android.net.Uri;

import com.example.farmconnect.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.*;

public class UserProfileApi {
    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();

    public interface UpdateProfileCallback {
        void onSuccess(String response);
        void onFailure(IOException e);
    }

    public static void updateProfile(Context context, String name, String mobile, String email, UpdateProfileCallback callback) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("mobile", mobile)
                .addFormDataPart("email", email)
                .build();

        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.api_base_url)+":8000/api/editProfile")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    public interface UpdateProfilePicCallback {
        void onSuccess(String response);
        void onFailure(IOException e);
    }

    public static void updateProfilePic(Context context, Uri imageUri, UpdateProfilePicCallback callback) {
        try {
            // Convert URI to StoryFile
            File file = uriToFile(context, imageUri);
            if (file == null) {
                callback.onFailure(new IOException("Failed to create file from URI"));
                return;
            }

            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("profile_image", file.getName(), fileBody)
                    .build();

            Request request = new Request.Builder()
                    .url(context.getResources().getString(R.string.api_base_url)+":8000/api/editProfilePic")
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        callback.onSuccess(response.body().string());
                    } else {
                        callback.onFailure(new IOException("Unexpected code " + response));
                    }
                }
            });
        } catch (IOException e) {
            callback.onFailure(e);
        }
    }

    private static File uriToFile(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream == null) return null;

        File tempFile = File.createTempFile("profile_pic", null, context.getCacheDir());
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }
    public interface UserProfileCallback {
        void onSuccess(String response);
        void onFailure(IOException e);
    }

    public static void getCurrentUserProfile(Context context, UserProfileCallback callback) {
        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.api_base_url)+":8000/api/user")
                .get()
                .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }
}
