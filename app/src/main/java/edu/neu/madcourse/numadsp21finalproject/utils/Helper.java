package edu.neu.madcourse.numadsp21finalproject.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class Helper {
    public static final String NO_INTERNET = "No internet connection";
    public static String SERVER_KEY = "AAAAf-Efg5c:APA91bH5jUEnPT04fw-qdgAMl5ghx_ZAgNL6x4cMKoxz9MRoMEXYS2g4UHOEEuDs2Eb2ysEFfgtp48D8oxMpCiUt7ir6ezA09tv0FjvyH1mQ6jUtLkO_4_xadmzKlgQXfmNVmXgGoarB";
    public static String[] CATEGORY_LIST = {"Rock", "Pop", "Hip Hop", "Blues", "Jazz", "Reggae", "Folk", "Country", "Classical",
            "Soul", "R&B", "Heavy Metal"};

    private final static String EMAIL = "email";
    private final static String PASSWORD = "password";
    private final static String LOGGED_IN = "loggedIn";
    private final static String USER_TOKEN = "userToken";

    //citation: https://stackoverflow.com/a/51070246
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //citation : https://stackoverflow.com/a/12744408
    public static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
    }

    public static void setEmailPassword(Context ctx, String userName, String password)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx.getApplicationContext()).edit();
        editor.putString(EMAIL, userName);
        editor.putString(PASSWORD, password);
        editor.putBoolean(LOGGED_IN, true);
        editor.apply();
    }


    public static void setUserToken(Context ctx, String userToken) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx.getApplicationContext()).edit();
        editor.putString(USER_TOKEN, userToken);
        editor.apply();
    }

    public static String getEmail(Context ctx)
    {
        return getSharedPreferences(ctx.getApplicationContext()).getString(EMAIL, "");
    }

    public static String getPassword(Context ctx)
    {
        return getSharedPreferences(ctx.getApplicationContext()).getString(PASSWORD, "");
    }

    public static Boolean getLoggedIn(Context ctx)
    {
        return getSharedPreferences(ctx.getApplicationContext()).getBoolean(LOGGED_IN, false);
    }

    public static void clearLoggedInName(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx.getApplicationContext()).edit();
        editor.clear(); //clear all stored data
        editor.apply();
    }

    public static String getUserToken(Context ctx) {
        return getSharedPreferences(ctx.getApplicationContext()).getString(USER_TOKEN, "");
    }


}
