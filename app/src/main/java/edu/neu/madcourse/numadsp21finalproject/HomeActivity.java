package edu.neu.madcourse.numadsp21finalproject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsActivity;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryActivity;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsAdapter;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsItem;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsViewListener;
import edu.neu.madcourse.numadsp21finalproject.navigation.ProfileActivity;
import edu.neu.madcourse.numadsp21finalproject.notifications.NotificationListActivity;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private ImageButton searchButton;
    private EditText searchTextBox;
    private String currentEmail;
    private FirebaseUser user;
    private String userId;

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private FeedsAdapter feedsAdapter;
    private ArrayList<FeedsItem> feedsItemArrayList;
    private ArrayList<String> friendsList;
    FirebaseFirestore firebaseFirestore;

    private BottomNavigationView bottomNavigationView;
    private BroadcastReceiver myBroadcastReceiver = null;
    private Dialog aboutDialog;

    private Dialog levelDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(HomeActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        userId = user.getUid();

        currentEmail = user.getEmail();
        feedsItemArrayList = new ArrayList<>();
        friendsList = new ArrayList<>();


        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setNavigationListener();
        setBottomNavigationListener();
        setSearchComponent();
        FeedsRunnable feedsRunnable = new FeedsRunnable();
        new Thread(feedsRunnable).start();
        createLevelDialog();
        createDialogForAbout();
    }

    private void createLevelDialog() {
        levelDialog = new Dialog(HomeActivity.this);
        levelDialog.setContentView(R.layout.level_section_layout);
        levelDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        levelDialog.setCancelable(true);
        Button closeButton = levelDialog.findViewById(R.id.close_level_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelDialog.dismiss();
            }
        });
        createScoreBoard();

    }

    private void createScoreBoard() {
        Helper.db.collection("users").document(userId)
                .addSnapshotListener((DocumentSnapshot snapshot, FirebaseFirestoreException error) -> {
                    if (snapshot!=null) {
                        Map<String, Object> fieldMap = snapshot.getData();
                        String currentLevel = fieldMap.get("currentLevel")!=null ?
                                fieldMap.get("currentLevel").toString() : "0";
                        String currentScore = fieldMap.get("currentScore")!=null ?
                                fieldMap.get("currentScore").toString() : "0";
                        TextView level = levelDialog.findViewById(R.id.current_level);
                        level.setText(currentLevel);
                        TextView score = levelDialog.findViewById(R.id.current_score);
                        score.setText(currentScore);
                    }


                });
    }

    class FeedsRunnable implements Runnable {
        @Override
        public void run() {
            getFriendsList();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.practice_btn:
                Intent intentPractice = new Intent(this,CategoryListActivity.class);
                intentPractice.putExtra("currentEmail", currentEmail);
                startActivity(intentPractice);
                break;
            case R.id.jam_btn:
                Intent intentJam = new Intent(this, JamActivity.class);
                intentJam.putExtra("currentEmail", currentEmail);
                startActivity(intentJam);
                break;
            case R.id.meet_btn:
                Intent intentMeet = new Intent(this, UserListActivity.class);
                intentMeet.putExtra("currentEmail", currentEmail);
                startActivity(intentMeet);
                break;
            default:
                return;

        }
    }




        private void setSearchComponent() {
            searchButton = findViewById(R.id.search_icon);
            searchTextBox = findViewById(R.id.search_box);
            searchButton.setOnClickListener(v -> {
                if (!searchTextBox.getText().toString().isEmpty()) {
                    String searchValue = searchTextBox.getText().toString();
                    Intent intent = new Intent(this, SearchResultActivity.class);
                    intent.putExtra("searchValue", searchValue);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this,"Please enter some item to search",
                            Toast.LENGTH_SHORT)
                            .show();
                }

            });


        }



    /*
    private void startMeetActivity() {
        Toast.makeText(HomeActivity.this, "Meeting",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }

     */

    private void setBottomNavigationListener() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(item->{
           int id = item.getItemId();
           switch(id) {
               case R.id.navigation_home:
                   break;
               case R.id.navigation_friends:
                   Intent friendsIntent = new Intent(this, FriendsActivity.class);
                   startActivity(friendsIntent);
                   break;
               case R.id.navigation_library:
                   Intent libraryIntent = new Intent(this, LibraryActivity.class);
                   startActivity(libraryIntent);
                   break;
               case R.id.navigation_notifications:
                   Intent notificationsIntent = new Intent(this, NotificationListActivity.class);
                   startActivity(notificationsIntent);
                   Toast.makeText(this, "My Notifications",Toast.LENGTH_SHORT).show();
                   break;
               default:
                   return true;
           }
           return true;

       });
    }

    private void setNavigationListener() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch(id)
            {
                case R.id.profile_title:
                    Intent intent = new Intent(this,ProfileActivity.class);
                    startActivity(intent);
                    break;
                case R.id.blogs_title:
                    Intent blogIntent = new Intent(this, BlogActivity.class);
                    startActivity(blogIntent);
                    break;
                case R.id.about_title:
                    aboutDialog.show();
                    break;
                case R.id.logout_title:
                    Helper.clearLoggedInName(this);
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Toast.makeText(HomeActivity.this, "You've logged out",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    return true;
            }
            drawer.close();
            return true;

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        if (item.getItemId() == R.id.reward) {
            levelDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_top_menu, menu);
        return true;
    }

    //citation: https://stackoverflow.com/a/39212571
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                EditText edit = ((EditText) v);
                Rect outR = new Rect();
                edit.getGlobalVisibleRect(outR);
                Boolean isKeyboardOpen = !outR.contains((int)ev.getRawX(), (int)ev.getRawY());
                if (isKeyboardOpen) {
                    edit.clearFocus();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                }
                edit.setCursorVisible(!isKeyboardOpen);

            }
        }
        return super.dispatchTouchEvent(ev);
    }




    private void createFeedsRecyclerView() {
        if(feedsItemArrayList.size() > 0) {
            Collections.sort(feedsItemArrayList, new Comparator<FeedsItem>() {
                @Override
                public int compare(FeedsItem o1, FeedsItem o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            });
        }

        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.feeds_recycler_view);
        recyclerView.setHasFixedSize(true);
        FeedsViewListener feedsViewListener = new FeedsViewListener() {
            @Override
            public void onPlayPauseClick(int position) {
                feedsItemArrayList.get(position).onPlayPauseClick(position);
            }
            @Override
            public void onLikeClick(int position) {
                feedsItemArrayList.get(position).onLikeClick(position);
            }
            @Override
            public void onCommentClick(int position) {
                feedsItemArrayList.get(position).onCommentClick(position);
            }
        };
        feedsAdapter = new FeedsAdapter(feedsItemArrayList, feedsViewListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(feedsAdapter);

    }


    private void getFriendsList() {

        firebaseFirestore.getInstance().collection("friends")
                .document(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        friendsList = (ArrayList<String>) document.get("friendsId");
                    } else {
                        Log.d("get friends list", "No such document");
                    }
                    // fetching recordings based on friends list
                    getFeed();
                } else {
                    Log.d("get friends list", "get failed with ", task.getException());
                }



            }



        });

    }

    private void getFeed() {
        if(friendsList.size() == 0) {
            Toast.makeText(HomeActivity.this, "you have no friend" ,Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseFirestore.getInstance().collection("recordings")
                .whereIn ("owner", friendsList)
                // not sure if i should limit or not
                // .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                FeedsItem item = new FeedsItem(documentSnapshot, HomeActivity.this);
                                feedsItemArrayList.add(item);
                                String ownerId = item.getOwnerId();
                                setOwnerPicture(ownerId, item);
                            }

                        } else {
                            Log.d("firebase", "Error getting feeds items", task.getException());
                        }
                        createFeedsRecyclerView();

                    }
                });


    }

    private void setOwnerPicture(String ownerId, FeedsItem item) {
        final String[] picturePath = {Helper.DEFAULT_PICTURE_PATH};
        Helper.db.collection("images")
                .document(ownerId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                picturePath[0] = snapshot.getString("path");
            }
            item.setProfileLink(picturePath[0]);
            feedsAdapter.notifyDataSetChanged();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                item.setProfileLink(picturePath[0]);
                feedsAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
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

    private void createDialogForAbout() {
        aboutDialog = new Dialog(HomeActivity.this);
        aboutDialog.setContentView(R.layout.about);
        aboutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        aboutDialog.setCancelable(false);
        TextView body = aboutDialog.findViewById(R.id.about_body);
        body.setText(R.string.about_body);

        Button closeButton = aboutDialog.findViewById(R.id.about_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.dismiss();
            }
        });

    }






}
