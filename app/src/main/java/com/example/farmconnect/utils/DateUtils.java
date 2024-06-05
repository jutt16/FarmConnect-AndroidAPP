package com.example.farmconnect.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
    };

    public static Date parseDate(String dateString) {
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set time zone to UTC for ISO formats
                return sdf.parse(dateString);
            } catch (ParseException e) {
                // Ignore and try the next format
            }
        }
        Log.e("Date Error", "Unparseable date: " + dateString);
        return null; // Return null if parsing fails for all formats
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "Date not available";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(date);
    }
}

