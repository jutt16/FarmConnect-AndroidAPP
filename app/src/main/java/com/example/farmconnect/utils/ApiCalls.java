package com.example.farmconnect.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Adapters.FriendRequestAdapter;
import com.example.farmconnect.Models.FriendRequestModel;
import com.example.farmconnect.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiCalls {
    // Method to send a friend request
    public interface FriendRequestCallback {
        void onResult(boolean success);
    }

    public static void sendFriendRequest(Context context, String userId, Button friend_btn, FriendRequestCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("id", userId)
                        .build();
                Request request = new Request.Builder()
                        .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/friendRequest")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    return response.isSuccessful();
                } catch (IOException e) {
                    Log.e("ApiCall", "Failed to send friend request: ", e);
                    return false;
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    AndroidUtil.showToast(context, "Friend request sent successfully!");
                    friend_btn.setBackgroundTintList(ColorStateList.valueOf(R.color.grey));
                    friend_btn.setText("Request Sent");
                } else {
                    AndroidUtil.showToast(context, "Failed to send friend request!");
                }
                callback.onResult(success);
            }
        }.execute();
    }

    public static void fetchRequests(RecyclerView recyclerView, Context context) {
        new AsyncTask<Void, Void, ArrayList<FriendRequestModel>>() {
            @Override
            protected ArrayList<FriendRequestModel> doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();

                try {
                    Request request = new Request.Builder()
                            .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/getRequests")
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                            .build();

                    Response response = client.newCall(request).execute();

                    // Check if the response is successful (status code 200)
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        return parseRequests(responseData);
                    } else {
                        // Handle error response (e.g., display error message)
                        Log.e("fetchRequests", "Server returned error response: " + response.code());
                        return null;
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.d("Fetch Request Error",e.getMessage());
                    return null; // Return null to indicate error
                }
            }

            @Override
            protected void onPostExecute(ArrayList<FriendRequestModel> requestsList) {
                if (requestsList != null) {
                    FriendRequestAdapter adapter = new FriendRequestAdapter(requestsList,context);
                    recyclerView.setAdapter(adapter);
                } else {
                    // Handle error or show error message
                    AndroidUtil.showToast(context, "Failed to fetch friend requests!");
                }
            }
        }.execute();
    }

    private static ArrayList<FriendRequestModel> parseRequests(String responseData) throws JSONException {
        ArrayList<FriendRequestModel> requestsList = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(responseData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            FriendRequestModel requestModel = new FriendRequestModel();
            requestModel.setId(jsonObject.getInt("id"));
            requestModel.setSender(jsonObject.getInt("sender"));
            requestModel.setReciever(jsonObject.getInt("reciever"));
            requestModel.setCreated_at(jsonObject.getString("created_at"));
            requestModel.setUpdated_at(jsonObject.getString("updated_at"));
            requestModel.setSender_id(jsonObject.getInt("sender_id"));
            requestModel.setSender_name(jsonObject.getString("sender_name"));
            requestModel.setSender_mobile(jsonObject.getString("sender_mobile"));
            requestModel.setSender_address(jsonObject.optString("sender_address", null));
            String profileImagePath = ":8000"+jsonObject.getString("sender_profile_image_path");
            requestModel.setSender_profile_image_path(profileImagePath);

            requestsList.add(requestModel);
        }

        return requestsList;
    }

    public static void acceptFriendRequest(Context context, int friendId, AcceptFriendRequestCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient.Builder().build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("id", String.valueOf(friendId))
                        .build();
                Request request = new Request.Builder()
                        .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/accepted")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    boolean success = response.isSuccessful();
                    return success;
                } catch (IOException e) {
                    Log.e("ApiCall", "Failed to send friend acceptance: ", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (callback != null) {
                    callback.onFriendRequestAccepted(success);
                }
            }
        }.execute();
    }
    public interface AcceptFriendRequestCallback {
        void onFriendRequestAccepted(boolean success);
    }
    public static void rejectFriendRequest(Context context, int friendId, RejectFriendRequestCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient.Builder().build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("id", String.valueOf(friendId))
                        .build();
                Request request = new Request.Builder()
                        .url(context.getResources().getString(R.string.api_base_url) + ":8000/api/rejected")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + TokenManager.getToken(context))
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    boolean success = response.isSuccessful();
                    return success;
                } catch (IOException e) {
                    Log.e("ApiCall", "Failed to send friend rejection: ", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (callback != null) {
                    callback.onFriendRequestRejected(success);
                }
            }
        }.execute();
    }
    public interface RejectFriendRequestCallback {
        void onFriendRequestRejected(boolean success);
    }

}
