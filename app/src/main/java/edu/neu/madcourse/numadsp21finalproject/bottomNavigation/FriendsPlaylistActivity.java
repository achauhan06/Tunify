package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.neu.madcourse.numadsp21finalproject.HomeActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.service.FirebaseInstanceMessagingService;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;


public class FriendsPlaylistActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private FriendsPlaylistAdapter playlistAdapter;
    private ArrayList<FriendsPlaylistItem> friendsPlaylistItemsList;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private String userId;
    private String friendId;
    private String friendName;
    FirebaseFirestore firebaseFirestore;
    private int currentSelectedSong = -1;
    FirebaseInstanceMessagingService firebaseInstanceMessagingService;

    private BroadcastReceiver myBroadcastReceiver = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_library);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        friendsPlaylistItemsList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        friendId = getIntent().getStringExtra("friendId");
        friendName = getIntent().getStringExtra("friendName");
        toolbar.setTitle(friendName + "'s Recordings");
        getLibraryItems();
    }

    private void getLibraryItems() {
        firebaseFirestore.getInstance().collection("recordings")
                .whereEqualTo("owner", friendId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                FriendsPlaylistItem friendsPlaylistItem = new FriendsPlaylistItem(documentSnapshot,
                                        FriendsPlaylistActivity.this, userId);
                                friendsPlaylistItemsList.add(friendsPlaylistItem);
                                // Toast.makeText(LibraryActivity.this, projectName,Toast.LENGTH_SHORT).show();

                            }
                            createRecyclerView();

                        } else {
                            Log.d("firebase", "Error getting library items", task.getException());
                        }
                    }
                });
    }

    private void createRecyclerView() {
        if(friendsPlaylistItemsList.size() > 0) {
            Collections.sort(friendsPlaylistItemsList, new Comparator<FriendsPlaylistItem>() {
                @Override
                public int compare(FriendsPlaylistItem o1, FriendsPlaylistItem o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            });
        }
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.library_recycler_view);
        recyclerView.setHasFixedSize(true);
        PlaylistListener libraryViewClickListener = new PlaylistListener() {
            @Override
            public void onItemClick(int position) {
                currentSelectedSong = position;
                friendsPlaylistItemsList.get(position).onItemClick(position);
            }

            @Override
            public void onPauseClick(int position) {
                currentSelectedSong = position;
                friendsPlaylistItemsList.get(position).onPauseClick(position);
            }

            @Override
            public void onStopClick(int position) {
                currentSelectedSong = -1;
                friendsPlaylistItemsList.get(position).onStopClick(position);
            }
            @Override
            public void onCommentClick(int position) {
                friendsPlaylistItemsList.get(position).onCommentClick(position);
            }
            @Override
            public void onLikeClick(int position) {
                friendsPlaylistItemsList.get(position).onLikeClick(position);
                // String santoshToken = "dfDcGn0gT2yrKMGczlpidf:APA91bHT4spTXm33MgQ6ufTt_fiEY-Dy8Q4xM9dqE2OGdIhnJ7NLzcUpkxxNlAZgQvzKnPqrTR2LTC-vmhRDAhRWBpjUmPFq6wXBimVfoz3CZMadVOYdcJCmhTs_BG2RtNMIUOR-AA-i";
                // firebaseInstanceMessagingService.test("Test", LibraryActivity.this);
                // sendMessageToDeviceLib(userToken, "test");
            }
        };
        playlistAdapter = new FriendsPlaylistAdapter(friendsPlaylistItemsList,
                libraryViewClickListener,
                this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(playlistAdapter);

    }

    @Override
    public void onBackPressed() {
        createBackAlert();
    }

    private void createBackAlert() {
        if (currentSelectedSong == -1) {
            this.finish();
        } else {
            friendsPlaylistItemsList.get(currentSelectedSong).getMediaPlayer().pause();
            AlertDialog.Builder songCloseAlert = new AlertDialog
                    .Builder(FriendsPlaylistActivity.this);
            songCloseAlert.setMessage("Going back will stop playing the song. "
                    + "Are you sure you want to leave?");
            songCloseAlert.setTitle("Song paused");
            songCloseAlert.setCancelable(false);
            songCloseAlert.setPositiveButton("Yes", (dialog, which) -> {
                friendsPlaylistItemsList.get(currentSelectedSong).getMediaPlayer().stop();
                this.finish();
            });

            songCloseAlert.setNegativeButton("No", (dialog, which) -> {
                friendsPlaylistItemsList.get(currentSelectedSong).getMediaPlayer().start();
                dialog.cancel();
            });
            AlertDialog alertDialog = songCloseAlert.create();
            alertDialog.show();

        }
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
