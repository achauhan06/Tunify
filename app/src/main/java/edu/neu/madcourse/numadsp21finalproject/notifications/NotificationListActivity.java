package edu.neu.madcourse.numadsp21finalproject.notifications;

<<<<<<< Updated upstream
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
=======
import android.content.Context;
>>>>>>> Stashed changes
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< Updated upstream
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.categoryview.CategoryViewListener;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsAdapter;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsItem;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsViewListener;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class NotificationListActivity extends AppCompatActivity {
    private ArrayList<NotificationItem> notificationItemArrayList;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    private BroadcastReceiver myBroadcastReceiver = null;


=======
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.UserListActivity;
import edu.neu.madcourse.numadsp21finalproject.users.UserAdapter;
import edu.neu.madcourse.numadsp21finalproject.users.UserItem;
import edu.neu.madcourse.numadsp21finalproject.users.UserViewListener;

public class NotificationListActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private ArrayList<NotificationItem> notificationItemList;
    private FirebaseFirestore db;
>>>>>>> Stashed changes
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
<<<<<<< Updated upstream
        Toolbar toolbar = findViewById(R.id.toolbar_notifications);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        notificationItemArrayList = new ArrayList<>();
        NotificationItem item = new NotificationItem();
        notificationItemArrayList.add(item);
        notificationItemArrayList.add(item);
        createNotificationRecyclerView();
=======
        Toolbar toolbar = findViewById(R.id.toolbar_song);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        notificationItemList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("notifications").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        String notificationItem = d.getString("type");
                        NotificationItem notification = new NotificationItem(notificationItem);
                        notificationItemList.add(notification);
                    }
                } else {
                    Toast.makeText(NotificationListActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
                createRecyclerView();
            }
        });
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
>>>>>>> Stashed changes
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
