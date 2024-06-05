package com.example.farmconnect.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.example.farmconnect.Models.StoryFile;
import com.example.farmconnect.Models.StoryModel;
import com.example.farmconnect.Models.StoryUser;
import com.example.farmconnect.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.*;

public class StoryApiCalls {

    public interface CreateStoryApiCallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }

    public static void createStory(Context context, String description, List<Uri> uris, CreateStoryApiCallback callback) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("discription", description) // corrected typo
                .addFormDataPart("file_type", "image");

        for (Uri uri : uris) {
            try {
                String fileName = getFileName(context, uri);
                String mimeType = context.getContentResolver().getType(uri);
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    byte[] bytes = getBytesFromInputStream(inputStream);
                    builder.addFormDataPart("content[]", fileName, RequestBody.create(MediaType.parse(mimeType), bytes));
                    Log.d("StoryFile check", "StoryFile " + fileName + " added successfully.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
                Log.e("StoryFile check", "Error adding file: " + e.getMessage());
                return;
            }
        }

        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/story") // corrected URL
                .post(body) // simplified method setting
                .addHeader("Authorization", "Bearer " + TokenManager.getToken(context)) // ensure Bearer token prefix
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
                    callback.onFailure(new IOException("Unexpected code " + response + " with body " + response.body().string()));
                }
            }
        });
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        result = cursor.getString(columnIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private static byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        byte[] bytes;
        try (ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytes = byteBuffer.toByteArray();
        }
        return bytes;
    }
    public interface CurrentUserStoriesCallback {
        void onSuccess(ArrayList<StoryModel> stories);
        void onFailure(Exception e);
    }

    public static void getCurrentUserStories(Context context, int userId, CurrentUserStoriesCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/getUsersStories?id=" + userId)
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
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        ArrayList<StoryModel> stories = new ArrayList<>();

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject storyJson = dataArray.getJSONObject(i);
                            StoryModel story = new StoryModel();
                            story.id = storyJson.getInt("id");
                            story.user_id = storyJson.getInt("user_id");
                            story.discription = storyJson.optString("discription");
                            story.likes = storyJson.getInt("likes");
                            story.created_at = DateUtils.parseDate(storyJson.getString("created_at"));
                            story.updated_at = DateUtils.parseDate(storyJson.getString("updated_at"));
                            story.files = new ArrayList<>();

                            JSONArray filesArray = storyJson.getJSONArray("files");
                            for (int j = 0; j < filesArray.length(); j++) {
                                JSONObject fileJson = filesArray.getJSONObject(j);
                                StoryFile file = new StoryFile();
                                file.id = fileJson.getInt("id");
                                file.story_id = fileJson.getInt("story_id");
                                file.file_path = fileJson.getString("file_path");
                                file.file_type = fileJson.getString("file_type");
                                file.created_at = DateUtils.parseDate(fileJson.getString("created_at"));
                                file.updated_at = DateUtils.parseDate(fileJson.getString("updated_at"));
                                story.files.add(file);
                            }

                            JSONObject userJson = storyJson.getJSONObject("user");
                            StoryUser user = new StoryUser();
                            user.id = userJson.getInt("id");
                            user.user_id = userJson.getString("user_id");
                            user.name = userJson.getString("name");
                            user.status = userJson.getInt("status");
                            user.mobile = userJson.getString("mobile");
                            user.profile_image_path = userJson.getString("profile_image_path");
                            user.created_at = DateUtils.parseDate(userJson.getString("created_at"));
                            user.updated_at = DateUtils.parseDate(userJson.getString("updated_at"));
                            story.user = user;

                            stories.add(story);
                        }

                        callback.onSuccess(stories);
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    }
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }
}
