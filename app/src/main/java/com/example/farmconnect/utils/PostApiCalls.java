package com.example.farmconnect.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.example.farmconnect.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostApiCalls {
    public static void createPost(Context context, Uri fileUri, String fileType, String description, final PostCallback callback) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        // Get the file path from the Uri
        String filePath = AndroidUtil.getRealPathFromURI(fileUri, context);

        // Create a file object from the file path
        File file = new File(filePath);

        // Determine media type based on the file type
        MediaType mediaType = MediaType.parse("application/octet-stream");
        if (fileType.equals(".jpg") || fileType.equals(".jpeg")) {
            mediaType = MediaType.parse("image/jpeg");
        } else if (fileType.equals(".png")) {
            mediaType = MediaType.parse("image/png");
        } else if (fileType.equals(".mp4")) {
            mediaType = MediaType.parse("video/mp4");
        } else {
            // Unsupported file type, handle accordingly
        }

        // Build the request body
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("content", "media" + fileType, RequestBody.create(mediaType, file))
                .addFormDataPart("file_type", fileType)
                .addFormDataPart("description", description)
                .build();

        // Build the request
        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/post")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                Log.e("Error", "Failed to make request: " + e.getMessage());
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle response
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onSuccess(responseBody);
                } else {
                    // Handle unsuccessful response
                    Log.e("Error", "Unsuccessful response: " + response.code());
                    callback.onFailure(new IOException("Unsuccessful response: " + response.code()));
                }
            }
        });
    }

    public interface PostCallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }


}