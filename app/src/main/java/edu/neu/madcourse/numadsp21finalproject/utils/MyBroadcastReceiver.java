package edu.neu.madcourse.numadsp21finalproject.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;

public class MyBroadcastReceiver extends BroadcastReceiver {
    Dialog dialog;
    TextView nettext;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String status = NetworkUtil.getConnectivityStatusString(context);
        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.no_internet);
        Button restartapp = (Button)dialog.findViewById(R.id.restart_btn);
        nettext =(TextView)dialog.findViewById(R.id.no_internet_text);

        restartapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
                Log.d("clickedbutton","yes");
                // Intent i = new Intent(context, MainActivity.class);
                // context.startActivity(i);
                context.startActivity(intent);

            }
        });
        Log.d("network",status);
        if(status.isEmpty()||status.equals("No internet is available")||status.equals("No Internet Connection")) {
            status="No Internet Connection";
            dialog.show();
        }
        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }
}
