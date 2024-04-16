package com.example.farmconnect.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    public static File createImageFileFromBitmap(Context context, Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            saveBitmapToFile(bitmap, imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    private static void saveBitmapToFile(Bitmap bitmap, File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(CompressFormat.JPEG, 90, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
