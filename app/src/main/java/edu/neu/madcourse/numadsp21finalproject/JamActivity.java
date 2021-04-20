package edu.neu.madcourse.numadsp21finalproject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

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
import edu.neu.madcourse.numadsp21finalproject.jamview.JamAdapter;
import edu.neu.madcourse.numadsp21finalproject.jamview.JamItem;
import edu.neu.madcourse.numadsp21finalproject.jamview.JamViewListener;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class JamActivity extends AppCompatActivity {

    private Dialog dialog;
    private List<FriendGroupItem> friendsList;
    private String userId;
    private String userName;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView dialogRecyclerView;
    private FriendGroupAdapter friendGroupAdapter;

    private RecyclerView jamRecyclerView;
    private List<JamItem> jamItemList;
    private RecyclerView.LayoutManager jamLayoutManger;
    private JamAdapter jamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jam_activity_view);
        Toolbar toolbar = findViewById(R.id.toolbar_jam);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        createJamView();

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

    private void createJamView() {
        jamItemList = new ArrayList<>();
        jamLayoutManger = new LinearLayoutManager(this);
        jamRecyclerView = findViewById(R.id.jam_group_view);
        jamRecyclerView.setHasFixedSize(true);
        JamViewListener jamViewListener = new JamViewListener() {
            @Override
            public void onItemClick(int position, Context context) {

            }

            @Override
            public void onItemDelete(int position) {

            }
        };
        jamRecyclerView.setLayoutManager(jamLayoutManger);
        jamAdapter = new JamAdapter(jamItemList, jamViewListener, this);
        jamRecyclerView.setAdapter(jamAdapter);
        Helper.db.collection("jamGroups")
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {

                        Map<String, Object> fieldMap = snapshot.getData();
                        if (fieldMap != null) {
                            for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
                                HashMap<String, String> friendMap =
                                        (HashMap<String, String>) entry.getValue();
                                jamItemList.add(new JamItem(entry.getKey(), friendMap));
                            }
                            jamRecyclerView.setAdapter(jamAdapter);
                        }
                    }
                });
    }

    private void createDialogRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        dialogRecyclerView = dialog.findViewById(R.id.friend_list_view);
        dialogRecyclerView.setHasFixedSize(true);
        FriendGroupListener friendGroupListener = position ->
                friendsList.get(position).onItemClicked(position);
        dialogRecyclerView.setLayoutManager(rLayoutManger);
        friendGroupAdapter = new FriendGroupAdapter(friendsList, friendGroupListener);
        dialogRecyclerView.setAdapter(friendGroupAdapter);

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
        userDocumentReference.addSnapshotListener(this, (value, error) -> {
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


        });
    }




    private void createNewGroupDialogView() {

        dialog = new Dialog(JamActivity.this);
        dialog.setContentView(R.layout.new_group_addition_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        EditText groupName = dialog.findViewById(R.id.group_name_input);


        Button createButton = dialog.findViewById(R.id.create_group_btn);
        createButton.setOnClickListener(v -> {
            String groupNameString = groupName.getText().toString();
            if (groupNameString.isEmpty()) {
                Toast.makeText(JamActivity.this,
                        "Please provide a group name", Toast.LENGTH_SHORT).show();
            } else {
                WriteBatch documentBatch = Helper.db.batch();
                HashMap<String, String> friendMap = new HashMap<>();
                for(FriendGroupItem friend : friendsList) {
                    if (friend.isChecked()) {
                        friendMap.put(friend.getFriendToken(), friend.getFriendName());

                    }
                }
                friendMap.put(userId, userName);
                Map<String, Object> groupEntry = new HashMap<>();
                groupEntry.put(groupNameString, friendMap);

                for(String user : friendMap.keySet()) {
                    documentBatch.set(Helper.db.collection("jamGroups")
                            .document(user), groupEntry);

                }


                documentBatch.commit().addOnSuccessListener(aVoid -> {
                    Snackbar.make(findViewById(android.R.id.content)
                            ,groupName.getText() + " group Created",Snackbar.LENGTH_SHORT).show();
                    dialog.dismiss();
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(android.R.id.content)
                                ,"Some error occurred. Group could not be created"
                                ,Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
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