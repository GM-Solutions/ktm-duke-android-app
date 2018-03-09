package com.ktm.ab.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nikhil on 08-03-2017.
 */

public class SharedDataUtils {
    public static final String PREFS_ID = "AOP_PREFS";

    public static int getIntFields(Context context, String key) {
        SharedPreferences settings;
        int value;
        settings = context.getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE);
        value = settings.getInt(key, 0);
        return value;
    }

    public static void addIntFields(Context context, String key, int value) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putInt(key, value); //3

        editor.commit(); //4
    }

    public static String getStringFields(Context context, String key) {
        SharedPreferences settings;
        String value;
        settings = context.getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE);
        value = settings.getString(key, "");
        return value;
    }

    public static void addStringFields(Context context, String key, String value) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putString(key, value); //3

        editor.commit(); //4
    }

    public static boolean getBooleanFields(Context context, String key) {
        SharedPreferences settings;
        boolean value;
        settings = context.getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE);
        value = settings.getBoolean(key, false);
        return value;
    }

    public static void addBooleanFields(Context context, String key, boolean value) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2
        editor.putBoolean(key, value); //3

        editor.commit(); //4
    }

    public static void clearData(Context context) {
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_ID, Context.MODE_PRIVATE); //1
        settings.edit().clear().commit();
    }

}
