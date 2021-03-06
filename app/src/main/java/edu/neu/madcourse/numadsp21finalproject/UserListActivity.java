package edu.neu.madcourse.numadsp21finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.model.User;
import edu.neu.madcourse.numadsp21finalproject.users.UserAdapter;
import edu.neu.madcourse.numadsp21finalproject.users.UserItem;
import edu.neu.madcourse.numadsp21finalproject.users.UserViewListener;
import edu.neu.madcourse.numadsp21finalproject.utils.CustomToast;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserItem> userItemList;
    private List<String> friendIdsList;
    private FirebaseFirestore db;
    private ArrayList<UserItem> userItemList2;
    private ImageButton profile;
    private User currentUser;
    private String userId;
    FirebaseUser user;
    String currentUserId;

    private BroadcastReceiver myBroadcastReceiver = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(UserListActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        userId = FirebaseAuth.getInstance().getUid();
        profile = findViewById(R.id.profile_button);
        currentUser = new User();
        friendIdsList = new ArrayList<>();
        //profileActivity = new ProfileActivity();
        userItemList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        //email = getIntent().getExtras().getString("email");

        //UserItem user1 = new UserItem("user1", "");
        userItemList2 = new ArrayList<>();

        //userItemList2.add(user1);


        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();
        //DocumentReference documentReference = db.collection("users").document(userId);
        //setProfile(documentReference);
        // userItemList = getIntent().getParcelableArrayListExtra("users");
        createUserListView();
    }

    private void createUserListView() {
        Helper.db.collection("friends").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    friendIdsList = (List<String>)value.get("friendsId");
                }
                buildUserListView();
            }
        });
    }

    private void buildUserListView() {
        Helper.db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException error) {

                if (!queryDocumentSnapshots.isEmpty()) {
                    userItemList.clear();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        String userName = d.getString("First Name") + " " + d.getString("Last Name");
                        if (d.get("Username") != null) {
                            userName = d.getString("Username");
                        }
                        String email1 = d.getString("Email");
                        if (d.get("Username") != null) {
                            userName = d.getString("Username");
                        }
                        UserItem user = new UserItem(userName, email1);
                        String friendUserId = d.getId();

                        String genres = d.getString("Genres");
                        if (genres != null) {
                            String[] genreArray = genres.split(";");
                            for(int i = 0; i < genreArray.length ; i++) {
                                genreArray[i] =  "#"+genreArray[i];
                            }
                            user.setGenre(String.join(" ", genreArray));
                        }

                        if (!currentUserId.equals(d.getId()) && !friendIdsList.contains(friendUserId)) {
                            userItemList.add(user);
                            setOwnerPicture(friendUserId, user);
                        }

                        //Toast.makeText(UserListActivity.this, email, Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(UserListActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
                createRecyclerView();

            }
        });
    }

    private void setOwnerPicture(String ownerId, UserItem item) {
        final String[] picturePath = {Helper.DEFAULT_PICTURE_PATH};
        Helper.db.collection("images")
                .document(ownerId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                picturePath[0] = snapshot.getString("path");
            }
            item.setProfileLink(picturePath[0]);
            userAdapter.notifyDataSetChanged();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                item.setProfileLink(picturePath[0]);
                userAdapter.notifyDataSetChanged();
            }
        });

    }

    private void createRecyclerView() {
        /*if(userItemList.isEmpty()) {
            CustomToast toast = new CustomToast(UserListActivity.this,
                    "There are no other people!", Snackbar.LENGTH_SHORT);
            toast.makeCustomToast(Gravity.CENTER);
        }*/
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.user_recycler_view);
        recyclerView.setHasFixedSize(true);
        UserViewListener userViewListener = new UserViewListener() {
            @Override
            public void onItemClick(int position, Context context) {
                userItemList.get(position).onItemClick(position,context);
            }
        };
        userAdapter = new UserAdapter(userItemList, userViewListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(userAdapter);
        /*profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(UserListActivity.this, "My Profile",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserListActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });*/

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
