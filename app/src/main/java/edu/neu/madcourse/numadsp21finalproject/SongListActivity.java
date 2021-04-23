package edu.neu.madcourse.numadsp21finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.songview.SongAdapter;
import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;
import edu.neu.madcourse.numadsp21finalproject.songview.SongViewListener;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class SongListActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private ArrayList<SongItem> songItemList;
    private BroadcastReceiver myBroadcastReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_main);
        Toolbar toolbar = findViewById(R.id.toolbar_song);
        setSupportActionBar(toolbar);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(SongListActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        songItemList = getIntent().getParcelableArrayListExtra("songs");
        createRecyclerView();
    }

    private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.song_recycler_view);
        recyclerView.setHasFixedSize(true);
        SongViewListener songViewListener = new SongViewListener() {
            @Override
            public void onItemClick(int position, Context context) {
                songItemList.get(position).onItemClick(position,context);
            }
        };
        songAdapter = new SongAdapter(songItemList, songViewListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(songAdapter);

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
