package edu.neu.madcourse.numadsp21finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsActivity;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryActivity;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsAdapter;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsItem;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsViewListener;
import edu.neu.madcourse.numadsp21finalproject.navigation.ProfileActivity;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private ImageButton searchButton;
    private EditText searchTextBox;
    private Button meet;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        if (user != null) {
            currentEmail = user.getEmail();
        } else {
            currentEmail = getIntent().getStringExtra("email");
        }

        //meet = findViewById(R.id.meet_btn);
        /*meet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startMeetActivity();
            }
        });*/
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
        getFriendsList();


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.practice_btn:
                Intent intent = new Intent(this,CategoryListActivity.class);
                intent.putExtra("currentEmail", currentEmail);
                startActivity(intent);
                break;
            case R.id.jam_btn:
                break;
            case R.id.meet_btn:
                Intent intent1 = new Intent(this, UserListActivity.class);
                intent1.putExtra("currentEmail", currentEmail);
                startActivity(intent1);
                break;
            default:
                return;

        }
    }


    private void setSearchComponent() {
        searchButton = findViewById(R.id.search_icon);
        searchButton.setOnClickListener(v ->
                Toast.makeText(HomeActivity.this,"search clicked", Toast.LENGTH_SHORT)
                        .show());
        searchTextBox = findViewById(R.id.search_box);
   /*     searchTextBox.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                searchTextBox.setCursorVisible(false);
            }
        });*/


    }

    private void startMeetActivity() {
        Toast.makeText(HomeActivity.this, "Meeting",Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, UserListActivity.class);
        startActivity(intent);
    }

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
                   Toast.makeText(HomeActivity.this, "My Notifications",Toast.LENGTH_SHORT).show();
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
                case R.id.account_title:
                    Toast.makeText(HomeActivity.this, "My Account",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.profile_title:
                    Intent intent = new Intent(this,ProfileActivity.class);
                    startActivity(intent);
                    break;
                case R.id.settings_title:
                    Toast.makeText(HomeActivity.this, "My Settings",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.blogs_title:
                    Toast.makeText(HomeActivity.this, "My Blogs",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.about_title:
                    Toast.makeText(HomeActivity.this, "About",Toast.LENGTH_SHORT).show();
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
            public void onItemClick(int position) {
                feedsItemArrayList.get(position).onItemClick(position);
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
            return;
        }
        firebaseFirestore.getInstance().collection("recordings")
                .whereIn ("owner", friendsList)
                // .orderBy("time")
                // not sure if i should limit or not
                // .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String path = documentSnapshot.get("path").toString();
                                String projectName = documentSnapshot.get("name").toString();
                                String genre = documentSnapshot.get("genre").toString();
                                String ownerName = documentSnapshot.get("username").toString();
                                Timestamp timestamp= (Timestamp) documentSnapshot.get("time");
                                String time =  timestamp.toDate().toString();
                                FeedsItem item = new FeedsItem(projectName, path, genre, ownerName,
                                        timestamp,time,HomeActivity.this);
                                feedsItemArrayList.add(item);
                            }




                        } else {
                            Log.d("firebase", "Error getting feeds items", task.getException());
                        }
                        createFeedsRecyclerView();

                    }
                });


    }

    @Override
    public void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }








}
