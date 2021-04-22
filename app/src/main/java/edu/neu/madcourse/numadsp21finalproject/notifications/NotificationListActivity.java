package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.categoryview.CategoryViewListener;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsAdapter;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsItem;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsViewListener;

public class NotificationListActivity extends AppCompatActivity {
    private ArrayList<NotificationItem> notificationItemArrayList;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);
        Toolbar toolbar = findViewById(R.id.toolbar_notifications);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        notificationItemArrayList = new ArrayList<>();
        NotificationItem item = new NotificationItem();
        notificationItemArrayList.add(item);
        notificationItemArrayList.add(item);
        createNotificationRecyclerView();
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
}
