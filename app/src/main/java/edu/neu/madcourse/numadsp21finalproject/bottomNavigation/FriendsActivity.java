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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.utils.CustomToast;
import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
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
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(FriendsActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        friendsList = new ArrayList<FriendItem>();

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(FriendsActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        userName = Helper.getUsername(this);
        //getUserName();
        Helper.db.collection("friendships")
                .whereArrayContains("friends", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {

                            int pos = 0;
                            for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                List<String> friendIds = (List<String>)documentSnapshot.get("friends");
                                String name1 = documentSnapshot.get("name1").toString();
                                String name2 = documentSnapshot.get("name2").toString();

                                // get friend name and friend id
                                if(friendIds.get(0).equals(userId)) {
                                    friendId = friendIds.get(1);
                                    friendName = name2;
                                }else {
                                    friendId = friendIds.get(0);
                                    friendName = name1;
                                }

                                // add friend item to friend list
                                FriendItem friend = new FriendItem( userId, friendId, friendName, FriendsActivity.this);
                                setGenreForFriend(friendId, friend, pos++);
                                friendsList.add(friend);
                                setOwnerPicture(friendId, friend);

                            }
                            createRecyclerView();

                        } else {
                            Log.d("firebase", "Error getting friend list", task.getException());
                        }
                    }
                });

    }

    private void setOwnerPicture(String ownerId, FriendItem item) {
        final String[] picturePath = {Helper.DEFAULT_PICTURE_PATH};
        Helper.db.collection("images")
                .document(ownerId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                picturePath[0] = snapshot.getString("path");
            }
            item.setProfileLink(picturePath[0]);
            friendsAdapter.notifyDataSetChanged();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                item.setProfileLink(picturePath[0]);
                friendsAdapter.notifyDataSetChanged();
            }
        });

    }

    private void setGenreForFriend(String friendId, FriendItem friendItem, int pos) {
        Helper.db.collection("users").document(friendId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                String genres = snapshot.getString("Genres");
                String[] genreArray = genres.split(";");
                for(int i = 0; i < genreArray.length ; i++) {
                    genreArray[i] =  "#"+genreArray[i];
                }
                friendItem.setGenres(String.join(" ", genreArray));
                friendsAdapter.notifyItemChanged(pos);

            }
        });
    }

    private void createRecyclerView(){

        if (friendsList.size() == 0) {
            CustomToast toast = new CustomToast(FriendsActivity.this,
                    "You do not have any friends!", Snackbar.LENGTH_SHORT);
            toast.makeCustomToast(Gravity.CENTER);
        }
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.friends_recycler_view);
        recyclerView.setHasFixedSize(true);
        FriendViewClickListener friendViewClickListener = new FriendViewClickListener() {
            @Override
            public void onItemClick(int position) {
                friendsList.get(position).onItemClick(position);
            }

            @Override
            public void onViewPlaylistClick(int position) {
                friendsList.get(position).onViewPlaylistClick(position);
            }

            @Override
            public void onViewBlogClick(int position) {
                friendsList.get(position).onViewBlogClick(position);
            }

            @Override
            public void onChatClick(int position) {
                friendsList.get(position).onChatClick(position);
            }
        };
        friendsAdapter = new FriendsAdapter(friendsList, friendViewClickListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(friendsAdapter);

    }
    /*
    public void getFirstLastName() {
        DocumentReference userDocumentReference = fireStore.getInstance().collection("users").document(userId);
        userDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String first_name = value.getString("First Name");
                String last_name = value.getString("Last Name");
                userName = first_name + " " + last_name;
                if (value.getString("Username")!=null) {
                    userName = value.getString("Username");
                }
                // System.out.println(userName);

            }
        });
    }

     */

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
