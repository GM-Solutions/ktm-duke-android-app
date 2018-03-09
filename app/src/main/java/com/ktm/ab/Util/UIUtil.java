package com.ktm.ab.Util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kapil.kale on 7/7/2015.
 */
public class UIUtil {

    private static ProgressDialog mProgressDialog;
    private static Object mObject = new Object();

    public static void startProgressDialog(Context context, String message) {
        try {
            synchronized (mObject) {
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(context, "", message);
                    mProgressDialog.setIndeterminate(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopProgressDialog(Context context) {
        try {
            synchronized (mObject) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    public static boolean isGPSON(final Context context) {
       /* ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();*/

        LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Check if enabled and if not send user to the GPS settings
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }


        return enabled;
    }

    public static String amountFormat(String value) {
        try {
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            float val = Float.parseFloat(value);
            return df.format(val);
        } catch (Exception e) {
            return value;
        }
    }

    public static boolean isTimeBetweenWorkHour() {
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY); // Get hour in 24 hour format
        int minute = now.get(Calendar.MINUTE);

        Date date = parseDate(hour + ":" + minute);
        Date dateCompareOne = parseDate("08:00");
        Date dateCompareTwo = parseDate("20:00");

        if (dateCompareOne.after(date) && dateCompareTwo.before(date)) {
            return true;
        }
        return false;
    }

    private static Date parseDate(String date) {

        final String inputFormat = "HH:mm";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

}
