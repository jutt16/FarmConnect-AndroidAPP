package com.example.farmconnect.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

//    public static Date parseDate(String dateString) {
//        try {
//            String yearString = dateString.substring(0,4);
//            int year = Integer.parseInt(yearString);
//            String monthString = dateString.substring(5,7);
//            int month = Integer.parseInt(monthString);
//            String dayString = dateString.substring(8,10);
//            int day = Integer.parseInt(dayString);
//            String hourString = dateString.substring(11,13);
//            int hour = Integer.parseInt(hourString);
//
//            Log.d("Date check",dayString+"\n"+monthString+"\n"+yearString);
//
//            return new Date(year,month,day);
//        } catch (Exception e) {
//            // Handle parsing exception
//            e.printStackTrace();
//            return null; // Return null if parsing fails
//        }
//    }
public static Date parseDate(String dateString) {
    try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Set time zone to UTC
        Log.d("Date",sdf.toString());
        return sdf.parse(dateString);
    } catch (ParseException e) {
        e.printStackTrace();
        Log.e("Date Error",e.getMessage());
        return null; // Return null if parsing fails
    }
}

}

