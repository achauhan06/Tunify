package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
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


import edu.neu.madcourse.numadsp21finalproject.R;

import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class NotificationListActivity extends AppCompatActivity {
    private ArrayList<NotificationItem> notificationItemArrayList;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private String projectName = "";
    private String status = "";
    private String title = "";
    private String body = "";

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

                        NotificationItem item = new NotificationItem(documentSnapshot,context);
                        notificationItemArrayList.add(item);

                    }

                }else {
                    Toast.makeText(context, "failed to get notification history",Toast.LENGTH_SHORT).show();
                }
                createNotificationRecyclerView();


                // Toast.makeText(NotificationListActivity.this,"list: " + notificationItemArrayList.size(),Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void getFriendRequestStatus(String contentId, String senderName,Timestamp timestamp) {
        db.collection("friendRequests").document(contentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            status = task.getResult().get("status").toString();
                            title = "Friend Request";
                            body = senderName + " sent you a friend request.";


                            // Toast.makeText(NotificationListActivity.this,"status for " + contentId,Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(NotificationListActivity.this,"failed to get friend request status",Toast.LENGTH_SHORT).show();
                        }

                    }

                });
    }

    private void getProjectName(String contentId, String type, String senderName, Timestamp timestamp){
        db.collection("recordings").document(contentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            projectName = task.getResult().get("name").toString();
                            if(type.equals("like")){
                                title = "New Like";
                                body = senderName + " liked your project " + projectName;
                                /*
                                NotificationItem item = new NotificationItem(type,title,body,
                                        timestamp,status,projectName
                                        , NotificationListActivity.this);
                                notificationItemArrayList.add(item);

                                 */

                            }else if(type.equals("comment")) {
                                title = "New Comment";
                                body = senderName + " commented your project " + projectName;
                                /*
                                NotificationItem item = new NotificationItem(type,title,body,
                                        timestamp,status,projectName
                                        , NotificationListActivity.this);
                                notificationItemArrayList.add(item);

                                 */
                            }

                            // Toast.makeText(NotificationListActivity.this,"name of " + contentId,Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(NotificationListActivity.this,"failed to get project name",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createNotificationRecyclerView() {
        Toast.makeText(NotificationListActivity.this,"list: " + notificationItemArrayList.size(),Toast.LENGTH_SHORT).show();

        if(notificationItemArrayList.size() > 0) {
            Collections.sort(notificationItemArrayList, new Comparator<NotificationItem>() {
                @Override
                public int compare(NotificationItem o1, NotificationItem o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            });
        }
<<<<<<< HEAD
=======
         */
>>>>>>> f7b75dbfe17e2168b1d601826b3fe3d54f2b6cbb

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