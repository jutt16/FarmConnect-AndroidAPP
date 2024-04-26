package com.example.farmconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private static final String PREF_NAME = "TokenPrefs";
    private static final String KEY_ACCESS_TOKEN = "accessToken";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        return getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, null);
    }

    public static void clearToken(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.apply();
    }

    public static boolean hasToken(Context context) {
        return getSharedPreferences(context).contains(KEY_ACCESS_TOKEN);
    }
}

