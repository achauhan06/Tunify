package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import edu.neu.madcourse.numadsp21finalproject.CategoryListActivity;
import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;

import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class NotificationListActivity extends AppCompatActivity {
    private ArrayList<NotificationItem> notificationItemArrayList;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;


    private BroadcastReceiver myBroadcastReceiver = null;

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

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(NotificationListActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        notificationItemArrayList = new ArrayList<>();
        getNotificationHistory(FirebaseAuth.getInstance().getCurrentUser().getUid(), NotificationListActivity.this);

    }



    private void getNotificationHistory(String userId, Context context) {
        FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("receiverId", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()) {

                        NotificationItem item = new NotificationItem(documentSnapshot, context);
                        notificationItemArrayList.add(item);

                    }

                }else {
                    Toast.makeText(context, "Some error occurred. Failed to get notification history",Toast.LENGTH_SHORT).show();
                }
                createNotificationRecyclerView();

            }
        });
    }



    private void createNotificationRecyclerView() {

        if(notificationItemArrayList.size() > 0) {
            Collections.sort(notificationItemArrayList, new Comparator<NotificationItem>() {
                @Override
                public int compare(NotificationItem o1, NotificationItem o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            });
        }
        if(notificationItemArrayList.size() > 0) {
            Collections.sort(notificationItemArrayList, new Comparator<NotificationItem>() {
                @Override
                public int compare(NotificationItem o1, NotificationItem o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            });
        }


        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.notifications_recycler_view);
        recyclerView.setHasFixedSize(true);
        NotificationViewListener notificationViewListener = new NotificationViewListener() {
            @Override
            public void onLibraryClick(int position) {
                notificationItemArrayList.get(position).onLibraryClick(position);

            }

            @Override
            public void onAcceptClick(int position) {
                notificationItemArrayList.get(position).onAcceptClick(position);
            }

            @Override
            public void onDeclineClick(int position) {
                notificationItemArrayList.get(position).onDeclineClick(position);
            }

            @Override
            public void onProfileClick(int position) {
                notificationItemArrayList.get(position).onProfileClick(position);
            }
        };

        notificationAdapter = new NotificationAdapter(notificationItemArrayList, notificationViewListener, this);
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