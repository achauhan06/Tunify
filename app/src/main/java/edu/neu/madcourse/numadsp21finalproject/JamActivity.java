package edu.neu.madcourse.numadsp21finalproject;

import android.app.Dialog;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.friendGroupView.FriendGroupAdapter;
import edu.neu.madcourse.numadsp21finalproject.friendGroupView.FriendGroupItem;
import edu.neu.madcourse.numadsp21finalproject.friendGroupView.FriendGroupListener;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class JamActivity extends AppCompatActivity {

    private Dialog dialog;
    private List<FriendGroupItem> friendsList;
    private String userId;
    private String userName;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private FriendGroupAdapter friendGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jam_activity_view);
        Toolbar toolbar = findViewById(R.id.toolbar_jam);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        createNewGroupDialogView();
        loadFriendsList();
        FloatingActionButton newGroup = findViewById(R.id.new_group_btn);
        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    private void loadFriendsList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getFriends();

            }
        }).start();
    }

    private void getFriends() {

        friendsList = new ArrayList();

        DocumentReference userDocumentReference = Helper.db.collection("users").document(userId);
        userDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String first_name = value.getString("First Name");
                String last_name = value.getString("Last Name");
                userName = first_name + " " + last_name;

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

                                        String friendName;
                                        String friendId;
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
                                        friendsList.add(new FriendGroupItem(friendName,friendId));
                                        createDialogRecyclerView();


                                    }

                                } else {
                                    Log.d("firebase", "Error getting friend list", task.getException());
                                }
                            }
                        });


            }
        });
    }

    private void createDialogRecyclerView() {

        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = dialog.findViewById(R.id.friend_list_view);
        recyclerView.setHasFixedSize(true);
        FriendGroupListener friendGroupListener = position ->
                friendsList.get(position).onItemClicked(position);
        recyclerView.setLayoutManager(rLayoutManger);
        friendGroupAdapter = new FriendGroupAdapter(friendsList, friendGroupListener);
        recyclerView.setAdapter(friendGroupAdapter);

    }


    private void createNewGroupDialogView() {

        dialog = new Dialog(JamActivity.this);
        dialog.setContentView(R.layout.new_group_addition_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        EditText groupName = dialog.findViewById(R.id.group_name_input);


        Button createButton = dialog.findViewById(R.id.create_group_btn);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupNameString = groupName.getText().toString();
                if (groupNameString.isEmpty()) {
                    Toast.makeText(JamActivity.this,
                            "Please provide a group name", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, String> friendMap = new HashMap<>();
                    for(FriendGroupItem friend : friendsList) {
                        if (friend.isChecked()) {
                            friendMap.put(friend.getFriendToken(), friend.getFriendName());
                        }
                    }
                    friendMap.put(userId, userName);
                    Map<String, Object> groupEntry = new HashMap<>();
                    groupEntry.put("groupName", groupNameString);
                    groupEntry.put("members", friendMap);
                    Helper.db.collection("jamGroups").document()
                            .set(groupEntry).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(findViewById(android.R.id.content)
                                    ,groupName + " group Created",Snackbar.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
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
}