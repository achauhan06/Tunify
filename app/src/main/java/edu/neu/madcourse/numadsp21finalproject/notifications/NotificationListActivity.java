package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


import edu.neu.madcourse.numadsp21finalproject.R;

import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class NotificationListActivity extends AppCompatActivity {
    private ArrayList<NotificationItem> notificationItemArrayList;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    private BroadcastReceiver myBroadcastReceiver = null;

    private ArrayList<NotificationItem> notificationItemList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        Toolbar toolbar = findViewById(R.id.toolbar_notifications);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        notificationItemArrayList = new ArrayList<>();
        NotificationItem item = new NotificationItem("test");

        notificationItemArrayList.add(item);
        notificationItemArrayList.add(item);
        createNotificationRecyclerView();

        notificationItemList = new ArrayList<>();
    }
    private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.notifications_recycler_view);
        recyclerView.setHasFixedSize(true);
        NotificationViewListener notificationViewListener = new NotificationViewListener() {
            @Override
            public void onItemClick(int position, Context context) {
                notificationItemList.get(position).onItemClick(position,context);
            }
        };
        notificationAdapter = new NotificationAdapter(notificationItemList, notificationViewListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(notificationAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createNotificationRecyclerView() {
        /*
        if(notificationItemArrayList.size() > 0) {
            Collections.sort(notificationItemArrayList, new Comparator<NotificationItem>() {
                @Override
                public int compare(NotificationItem o1, NotificationItem o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            });
        }
         */

        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.notifications_recycler_view);
        recyclerView.setHasFixedSize(true);
        NotificationViewListener notificationViewListener = new NotificationViewListener() {
            @Override
            public void onItemClick(int position, Context context) {
                notificationItemArrayList.get(position).onItemClick(position,context);
            }
        };

        notificationAdapter = new NotificationAdapter(notificationItemArrayList, notificationViewListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(notificationAdapter);
    }


    public void broadcastIntent() {
        registerReceiver(myBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            //Register or UnRegister your broadcast receiver here
            unregisterReceiver(myBroadcastReceiver);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}