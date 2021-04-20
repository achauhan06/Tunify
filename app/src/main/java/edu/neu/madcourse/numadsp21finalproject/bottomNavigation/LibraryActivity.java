package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.HomeActivity;
import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.commentview.CommentItem;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_library);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        }
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.library_recycler_view);
        recyclerView.setHasFixedSize(true);
        LibraryViewClickListener libraryViewClickListener = new LibraryViewClickListener() {
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
                currentSelectedSong = position;
                libraryList.get(position).onCommentClick(position);
            }
            @Override
            public void onLikeClick(int position) {
                currentSelectedSong = position;
                libraryList.get(position).onLikeClick(position);
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
                                // Toast.makeText(LibraryActivity.this, projectName,Toast.LENGTH_SHORT).show();

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
}
