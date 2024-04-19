package com.example.farmconnect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmconnect.Adapters.UsersFragmentRecyclerAdapter;
import com.example.farmconnect.Models.UsersFragmentModel;
import com.example.farmconnect.utils.AndroidUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UsersFragment extends Fragment {
    private RecyclerView userRecyclerView;
    private UsersFragmentRecyclerAdapter userAdapter;
    private OkHttpClient client;

    public UsersFragment() {
        client = new OkHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        userRecyclerView = view.findViewById(R.id.users_recycler_view);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Make API call to fetch user data
        fetchUserData();

        return view;
    }

    private void fetchUserData() {
        Request request = new Request.Builder()
                .url(getContext().getResources().getString(R.string.api_base_url)+":8000/api/usersList")
                .addHeader("Authorization", "Bearer 61|Cr4LAZvWPDQdEQp9lWmqnSKlEIuJjco2tzPmssKG3eba823c")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                // Handle failure, such as showing an error message
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    List<UsersFragmentModel> userList = parseUserData(responseData);
                    // Update RecyclerView adapter with the retrieved data
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userAdapter = new UsersFragmentRecyclerAdapter(userList,getContext());
                            userRecyclerView.setAdapter(userAdapter);
//                            AndroidUtil.showToast(getContext(),"Success"+userList.toString());
                            // Assuming this loop is part of a method in your Fragment or Activity
//                            Log.d("Data",responseData);
//                            for (UsersFragmentModel item : userList) {
//                                // Update UI elements or perform other operations
//                                Log.d(item.getName(),item.getProfileImagePath());// Custom method to update the UI
//                            }
                        }
                    });
                } else {
                    // Handle unsuccessful response
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtil.showToast(getContext(),"failed to load users!");
                        }
                    });
                }
            }
        });
    }

    private List<UsersFragmentModel> parseUserData(String responseData) {
        List<UsersFragmentModel> userList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray dataArray = jsonObject.getJSONArray("data"); // Ensure your JSON starts with a "data" array
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject userObject = dataArray.getJSONObject(i);
                UsersFragmentModel user = new UsersFragmentModel();
                user.setId(userObject.getInt("id"));
                user.setUserId(userObject.getString("user_id"));
                user.setName(userObject.getString("name"));
                user.setAddress(userObject.optString("address")); // Handles null automatically
                JSONArray rolesArray = userObject.getJSONArray("roles");
                List<String> roles = new ArrayList<>();
                for (int j = 0; j < rolesArray.length(); j++) {
                    roles.add(rolesArray.getString(j));
                }
                user.setRoles(roles);
                String profileimageurl = ":8000"+userObject.getString("profile_image_path");
                user.setProfileImagePath(profileimageurl);
                userList.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON parsing error, consider handling this more gracefully
            return null; // or return an empty list, depending on your error handling strategy
        }
        return userList;
    }

}
