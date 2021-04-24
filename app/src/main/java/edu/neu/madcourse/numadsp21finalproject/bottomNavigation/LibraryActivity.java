package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


import edu.neu.madcourse.numadsp21finalproject.utils.CustomToast;
import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private LibraryAdapter libraryAdapter;
    private ArrayList<LibraryItem> libraryList;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private String userId;
    FirebaseFirestore firebaseFirestore;
    private int currentSelectedSong = -1;
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

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(LibraryActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        libraryList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        getLibraryItems();
    }

    private void createRecyclerView() {
        if(libraryList.size() > 0) {
            Collections.sort(libraryList, new Comparator<LibraryItem>() {
                @Override
                public int compare(LibraryItem o1, LibraryItem o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            });
        } else {
            CustomToast toast = new CustomToast(LibraryActivity.this,
                    "You do not have any recordings!", Snackbar.LENGTH_SHORT);
            toast.makeCustomToast(Gravity.CENTER);
        }
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.library_recycler_view);
        recyclerView.setHasFixedSize(true);
        PlaylistListener libraryViewClickListener = new PlaylistListener() {
            @Override
            public void onItemClick(int position) {
                currentSelectedSong = position;
                libraryList.get(position).onItemClick(position);
            }

            @Override
            public void onPauseClick(int position) {
                currentSelectedSong = position;
                libraryList.get(position).onPauseClick(position);
            }

            @Override
            public void onStopClick(int position) {
                currentSelectedSong = -1;
                libraryList.get(position).onStopClick(position);
            }
            @Override
            public void onCommentClick(int position) {
                libraryList.get(position).onCommentClick(position);
            }
            @Override
            public void onLikeClick(int position) {
                libraryList.get(position).onLikeClick(position);
                // String santoshToken = "dfDcGn0gT2yrKMGczlpidf:APA91bHT4spTXm33MgQ6ufTt_fiEY-Dy8Q4xM9dqE2OGdIhnJ7NLzcUpkxxNlAZgQvzKnPqrTR2LTC-vmhRDAhRWBpjUmPFq6wXBimVfoz3CZMadVOYdcJCmhTs_BG2RtNMIUOR-AA-i";
                // firebaseInstanceMessagingService.test("Test", LibraryActivity.this);
                // sendMessageToDeviceLib(userToken, "test");
            }
        };
        libraryAdapter = new LibraryAdapter(libraryList, libraryViewClickListener, this);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteLibrary(libraryAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(libraryAdapter);

    }

    private void getLibraryItems() {
        firebaseFirestore.getInstance().collection("recordings")
                .whereEqualTo("owner", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                LibraryItem item = new LibraryItem(documentSnapshot, LibraryActivity.this, userId);
                                libraryList.add(item);
                            }
                            createRecyclerView();

                        } else {
                            Log.d("firebase", "Error getting library items", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        createBackAlert();
    }

    private void createBackAlert() {
        if (currentSelectedSong == -1) {
            this.finish();
        } else {
            libraryList.get(currentSelectedSong).getMediaPlayer().pause();
            AlertDialog.Builder songCloseAlert = new AlertDialog
                    .Builder(LibraryActivity.this);
            songCloseAlert.setMessage("Going back will stop playing the song. "
                    + "Are you sure you want to leave?");
            songCloseAlert.setTitle("Song paused");
            songCloseAlert.setCancelable(false);
            songCloseAlert.setPositiveButton("Yes", (dialog, which) -> {
                libraryList.get(currentSelectedSong).getMediaPlayer().stop();
                this.finish();
            });

            songCloseAlert.setNegativeButton("No", (dialog, which) -> {
                libraryList.get(currentSelectedSong).getMediaPlayer().start();
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
