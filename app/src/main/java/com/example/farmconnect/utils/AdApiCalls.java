package com.example.farmconnect.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;

import com.example.farmconnect.Models.AdsFile;
import com.example.farmconnect.Models.AdsModel;
import com.example.farmconnect.Models.AllLike;
import com.example.farmconnect.Models.AllReview;
import com.example.farmconnect.Models.StoryUser;
import com.example.farmconnect.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class AdApiCalls {

    public interface CreateAdApiCallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }

    public static void createAd(Context context, String title, String quantity, String price, String description, List<Uri> uris, CreateAdApiCallback callback) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("title", title)
                .addFormDataPart("quantity", quantity)
                .addFormDataPart("price", price)
                .addFormDataPart("discription", description); // corrected typo

        for (Uri uri : uris) {
            try {
                String fileName = getFileName(context, uri);
                String mimeType = context.getContentResolver().getType(uri);
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    byte[] bytes = getBytesFromInputStream(inputStream);
                    builder.addFormDataPart("content[]", fileName, RequestBody.create(MediaType.parse(mimeType), bytes));
                    Log.d("File check", "File " + fileName + " added successfully.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
                Log.e("File check", "Error adding file: " + e.getMessage());
                return;
            }
        }

        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/storeAd") // Ensure the URL is correct
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
    public interface GetAdsCallback {
        void onSuccess(ArrayList<AdsModel> ads);
        void onFailure(Exception e);
    }

    public static void getAllAds(Context context, GetAdsCallback callback) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        Request request = new Request.Builder()
                .url(context.getResources().getString(R.string.api_base_url)+":8000/api/getAllAds")
                .method("GET", null)
                .addHeader("Authorization", "Bearer "+TokenManager.getToken(context))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray data = jsonResponse.getJSONArray("data");

                        ArrayList<AdsModel> ads = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject adJson = data.getJSONObject(i);
                            AdsModel ad = new AdsModel();
                            ad.setId(adJson.getInt("id"));
                            ad.setSeller_id(adJson.getInt("seller_id"));
                            ad.setReviews(adJson.getInt("reviews"));
                            ad.setQuantity(adJson.getString("quantity"));
                            ad.setPrice(adJson.getString("price"));
                            ad.setDiscription(adJson.getString("discription"));
                            ad.setLikes(adJson.getInt("likes"));
                            ad.setDislikes(adJson.getInt("dislikes"));
                            ad.setCurrent_user_has_reaction(adJson.get("current_user_has_reaction"));
                            ad.setCreated_at(adJson.getString("created_at"));
                            ad.setUser_profile_image_path(adJson.getString("user_profile_image_path"));
                            ad.setUser_name(adJson.getString("user_name"));

                            JSONArray filesJson = adJson.getJSONArray("files");
                            ad.setFiles(new ArrayList<>());
                            for (int j = 0; j < filesJson.length(); j++) {
                                JSONObject fileJson = filesJson.getJSONObject(j);
                                AdsFile file = new AdsFile();
                                file.setFile_path(fileJson.getString("file_path"));
                                file.setFile_type(fileJson.getString("file_type"));
                                ad.getFiles().add(file);
                            }

                            JSONArray reviewsJson = adJson.getJSONArray("allReviews");
                            ad.setAllReviews(new ArrayList<>());
                            for (int j = 0; j < reviewsJson.length(); j++) {
                                JSONObject reviewJson = reviewsJson.getJSONObject(j);
                                AllReview review = new AllReview();
                                review.setId(reviewJson.getInt("id"));
                                review.setAd_id(reviewJson.getInt("ad_id"));
                                review.setUser_id(reviewJson.getInt("user_id"));
                                review.setComment(reviewJson.getString("comment"));
                                review.setCreated_at(DateUtils.parseDate(reviewJson.getString("created_at")));
                                review.setUpdated_at(DateUtils.parseDate(reviewJson.getString("updated_at")));

                                JSONObject userJson = reviewJson.getJSONObject("user");
                                StoryUser user = new StoryUser();
                                user.id = userJson.getInt("id");
                                user.user_id = userJson.getString("user_id");
                                user.name = userJson.getString("name");
                                user.email = userJson.getString("email");
                                user.status = userJson.getInt("status");
                                user.mobile = userJson.getString("mobile");
                                user.address = userJson.getString("address");
                                user.profile_image_path = userJson.getString("profile_image_path");
                                user.email_verified_at = userJson.getString("email_verified_at");
                                user.created_at = DateUtils.parseDate(userJson.getString("created_at"));
                                user.updated_at = DateUtils.parseDate(userJson.getString("updated_at"));

                                review.setUser(user);
                                ad.getAllReviews().add(review);
                            }

                            JSONArray likesJson = adJson.getJSONArray("allLikes");
                            ad.setAllLikes(new ArrayList<>());
                            for (int j = 0; j < likesJson.length(); j++) {
                                JSONObject likeJson = likesJson.getJSONObject(j);
                                AllLike like = new AllLike();
                                like.setId(likeJson.getInt("id"));
                                like.setAd_id(likeJson.getInt("ad_id"));
                                like.setUser_id(likeJson.getInt("user_id"));
                                like.setType(likeJson.getString("type"));
                                like.setCreated_at(DateUtils.parseDate(likeJson.getString("created_at")));
                                like.setUpdated_at(DateUtils.parseDate(likeJson.getString("updated_at")));

                                JSONObject userJson = likeJson.getJSONObject("user");
                                StoryUser user = new StoryUser();
                                user.id = userJson.getInt("id");
                                user.user_id = userJson.getString("user_id");
                                user.name = userJson.getString("name");
                                user.email = userJson.getString("email");
                                user.status = userJson.getInt("status");
                                user.mobile = userJson.getString("mobile");
                                user.address = userJson.getString("address");
                                user.profile_image_path = userJson.getString("profile_image_path");
                                user.email_verified_at = userJson.getString("email_verified_at");
                                user.created_at = DateUtils.parseDate(userJson.getString("created_at"));
                                user.updated_at = DateUtils.parseDate(userJson.getString("updated_at"));

                                like.setUser(user);
                                ad.getAllLikes().add(like);
                            }

                            // Process dislikes if needed

                            ads.add(ad);
                        }

                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(ads));
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(e));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure(new IOException("Unexpected code " + response)));
                }
            }
        });
    }
}
