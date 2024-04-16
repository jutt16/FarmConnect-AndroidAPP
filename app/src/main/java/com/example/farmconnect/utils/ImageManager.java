package com.example.farmconnect.utils;

import android.graphics.Bitmap;

public class ImageManager {
    private static ImageManager instance;
    private Bitmap imageBitmap;

    // Private constructor prevents instantiation from other classes
    private ImageManager() {}

    public static synchronized ImageManager getInstance() {
        if (instance == null) {
            instance = new ImageManager();
        }
        return instance;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}

