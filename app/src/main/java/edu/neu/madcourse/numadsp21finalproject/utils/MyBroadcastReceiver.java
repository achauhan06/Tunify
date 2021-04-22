package edu.neu.madcourse.numadsp21finalproject.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.neu.madcourse.numadsp21finalproject.NetworkActivity;

// Citation
// https://stackoverflow.com/questions/46538496/finish-activity-started-from-broadcast-receiver
// https://protocoderspoint.com/how-to-check-internet-connection-in-android/
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);

        if(status.isEmpty() ||
                status.equals("No internet is available") ||
                status.equals("No Internet Connection")) {

            Intent networkIntent = new Intent(context, NetworkActivity.class);
            networkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(networkIntent);

        } else {
            Intent networkIntent = new Intent();
            networkIntent.setAction("com.android.internetConnectivityAction");
            context.sendBroadcast(networkIntent);
        }

    }
}
