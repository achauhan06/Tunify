package edu.neu.madcourse.numadsp21finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Citation
// https://stackoverflow.com/questions/46538496/finish-activity-started-from-broadcast-receiver
public class NetworkActivity extends AppCompatActivity {
    public IntentFilter filter;

    BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context,"Internet is back. Enjoy!!",
                    Toast.LENGTH_SHORT).show();
            finish();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet);
        setFinishOnTouchOutside(false);

        filter=new IntentFilter();
        filter.addAction("com.android.internetConnectivityAction");
        registerReceiver(receiver,filter);

    }
}
