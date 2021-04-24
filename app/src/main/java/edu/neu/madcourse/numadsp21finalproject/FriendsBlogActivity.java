package edu.neu.madcourse.numadsp21finalproject;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.blogs.BlogAdapter;
import edu.neu.madcourse.numadsp21finalproject.blogs.BlogItem;
import edu.neu.madcourse.numadsp21finalproject.blogs.BlogViewListener;
import edu.neu.madcourse.numadsp21finalproject.utils.CustomToast;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class FriendsBlogActivity extends AppCompatActivity {

    private BroadcastReceiver myBroadcastReceiver = null;
    private FloatingActionButton floatingActionButton;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private BlogAdapter blogAdapter;
    private List<BlogItem> blogItemList;
    private String friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_blog);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(FriendsBlogActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        floatingActionButton = findViewById(R.id.new_blog_button);
        floatingActionButton.setVisibility(View.INVISIBLE);
        blogItemList = new ArrayList<>();
        friendId = getIntent().getStringExtra("friendId");

        getBlogList();
    }

    private void getBlogList() {

        Helper.db.collection("blogs").whereEqualTo("owner", friendId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documents = value.getDocuments();
                        blogItemList.clear();
                        for (DocumentSnapshot documentSnapshot : documents) {
                            BlogItem blogItem = new BlogItem(documentSnapshot,
                                    FriendsBlogActivity.this,
                                    friendId);
                            blogItemList.add(blogItem);
                            createRecyclerView();
                        }
                        if (blogItemList.isEmpty()) {
                            CustomToast toast = new CustomToast(FriendsBlogActivity.this,
                                    "There are no blogs published yet!", Snackbar.LENGTH_SHORT);
                            toast.makeCustomToast(Gravity.CENTER);
                        }
                    }
                });
    }

    private void createRecyclerView() {
        if(blogItemList.size() > 0) {
            Collections.sort(blogItemList, (o1, o2) ->
                    o2.getBlogTimeInTimeFormat().compareTo(o1.getBlogTimeInTimeFormat()));
        }

        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.blog_recycler_view);
        recyclerView.setHasFixedSize(true);
        BlogViewListener blogViewListener = new BlogViewListener() {
            @Override
            public void onViewClicked(int position, Boolean isFriend) {
                blogItemList.get(position).onViewClicked(position, isFriend);
            }
        };
        blogAdapter = new BlogAdapter(blogItemList, FriendsBlogActivity.this,
                true,
                blogViewListener);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(blogAdapter);
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
