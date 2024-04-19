package com.example.farmconnect.utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ImageDownloader {

    public interface OnImageDownloadedListener {
        void onImageDownloaded(byte[] imageData);
        void onDownloadFailed(IOException e);
    }

    public static void downloadImage(String imageUrl, OnImageDownloadedListener listener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    listener.onDownloadFailed(new IOException("Unexpected code " + response));
                    return;
                }

                // Read the image data from the response body
                byte[] imageData = response.body().bytes();
                listener.onImageDownloaded(imageData);
            }
        });
    }
}
