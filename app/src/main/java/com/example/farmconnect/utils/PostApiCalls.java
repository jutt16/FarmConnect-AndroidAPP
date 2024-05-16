package com.example.farmconnect.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.farmconnect.R;

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
                .addFormDataPart("discription", description)
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
    // New method for fetching posts
    public static void getPosts(Context context, final PostCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder().build();

                    // Build the request
                    Request request = new Request.Builder()
                            .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/getPosts")
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                            .build();

                    // Execute the request
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        return response.body().string();
                    } else {
                        throw new IOException("Unsuccessful response: " + response.code());
                    }
                } catch (IOException e) {
                    // Handle failure
                    Log.e("Error", "Failed to make request: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String responseBody) {
                if (responseBody != null) {
                    callback.onSuccess(responseBody);
                } else {
                    // Handle unsuccessful response
                    Log.e("Error", "Response body is null");
                    callback.onFailure(new IOException("Response body is null"));
                }
            }
        }.execute();
    }

    // New method for fetching likes
    public static void getPostsLikes(Context context,String id, final PostCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder().build();

                    //MediaType mediaType = MediaType.parse("application/json");
                    //RequestBody body = RequestBody.create(mediaType, "{\r\n    \"post_id\": "+id+"\r\n}");

                    // Build the request
                    Request request = new Request.Builder()
                            .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/postLikes?post_id="+id)
                            .method("GET", null)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                            .build();

                    // Execute the request
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        Log.d("Likes",response.body().toString());
                        return response.body().string();
                    } else {
                        throw new IOException("Unsuccessful response: " + response.code());
                    }
                } catch (IOException e) {
                    // Handle failure
                    Log.e("Error", "Failed to make request: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String responseBody) {
                if (responseBody != null) {
                    callback.onSuccess(responseBody);
                } else {
                    // Handle unsuccessful response
                    Log.e("Error", "Response body is null");
                    callback.onFailure(new IOException("Response body is null"));
                }
            }
        }.execute();
    }
    public interface LikePostCallback {
        void onSuccess();
        void onFailure(int errorCode);
    }

    public static void likePost(Context context, String postId, LikePostCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // Define the request body
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("post_id", postId)
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.api_base_url)+":8000/api/likePost")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                .build();

        // Asynchronously execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle the response
                if (response.isSuccessful()) {
                    // Request successful
                    callback.onSuccess();
                } else {
                    // Request not successful
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
                Log.e("PostApiCalls", "Failed to make API call: " + e.getMessage());
                callback.onFailure(0); // Pass an error code, or handle differently
            }
        });
    }

    public interface disLikePostCallback {
        void onSuccess();
        void onFailure(int errorCode);
    }
    public static void dislikePost(Context context, String postId, disLikePostCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // Define the request body
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("post_id", postId)
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.api_base_url)+":8000/api/dislikePost")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                .build();

        // Asynchronously execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle the response
                if (response.isSuccessful()) {
                    // Request successful
                    callback.onSuccess();
                } else {
                    // Request not successful
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
                Log.e("PostApiCalls", "Failed to make API call: " + e.getMessage());
                callback.onFailure(0); // Pass an error code, or handle differently
            }
        });
    }
}