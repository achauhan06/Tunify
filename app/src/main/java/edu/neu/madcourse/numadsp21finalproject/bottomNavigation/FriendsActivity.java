package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.HomeActivity;
import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.navigation.ProfileActivity;
import edu.neu.madcourse.numadsp21finalproject.songview.SongAdapter;
import edu.neu.madcourse.numadsp21finalproject.songview.SongViewListener;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class FriendsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fireStore;
    FirebaseUser user;
    String userId, userName, friendName, friendId;
    ArrayList<FriendItem> friendsList;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;

    private BroadcastReceiver myBroadcastReceiver = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_main);
        Toolbar toolbar = findViewById(R.id.toolbar_friends);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        friendsList = new ArrayList<FriendItem>();

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(FriendsActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        getUserName();
        // Toast.makeText(this, userName,Toast.LENGTH_SHORT).show();


        // query for all current user's friendships
        Helper.db.collection("friendships")
                .whereArrayContains("friends", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                List<String> friendIds = (List<String>)documentSnapshot.get("friends");
                                String name1 = documentSnapshot.get("name1").toString();
                                String name2 = documentSnapshot.get("name2").toString();

                                // get friend name and friend id
                                if(name1.equals(userName)) {
                                    friendName = name2;
                                }else {
                                    friendName = name1;
                                }
                                if(friendIds.get(0).equals(userId)) {
                                    friendId = friendIds.get(1);
                                }else {
                                    friendId = friendIds.get(0);
                                }

                                // add friend item to friend list
                                FriendItem friend = new FriendItem( userId, friendId, friendName, FriendsActivity.this);
                                friendsList.add(friend);
                                // Toast.makeText(FriendsActivity.this, friendName,Toast.LENGTH_SHORT).show();

                            }
                            createRecyclerView();

                        } else {
                            Log.d("firebase", "Error getting friend list", task.getException());
                        }
                    }
                });

    }

    private void createRecyclerView(){
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.friends_recycler_view);
        recyclerView.setHasFixedSize(true);
        FriendViewClickListener friendViewClickListener = new FriendViewClickListener() {
            @Override
            public void onItemClick(int position) {
                friendsList.get(position).onItemClick(position);
            }
        };
        friendsAdapter = new FriendsAdapter(friendsList, friendViewClickListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(friendsAdapter);

    }

    public void getUserName() {
        DocumentReference userDocumentReference = fireStore.getInstance().collection("users").document(userId);
        userDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String first_name = value.getString("First Name");
                String last_name = value.getString("Last Name");
                userName = first_name + " " + last_name;

            }
        });
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
