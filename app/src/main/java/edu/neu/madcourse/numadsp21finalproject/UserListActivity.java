package edu.neu.madcourse.numadsp21finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.navigation.ProfileActivity;
import edu.neu.madcourse.numadsp21finalproject.songview.SongAdapter;
import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;
import edu.neu.madcourse.numadsp21finalproject.songview.SongViewListener;
import edu.neu.madcourse.numadsp21finalproject.users.UserAdapter;
import edu.neu.madcourse.numadsp21finalproject.users.UserItem;
import edu.neu.madcourse.numadsp21finalproject.users.UserViewListener;

public class UserListActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<UserItem> userItemList;
    private FirebaseFirestore db;
    private ArrayList<UserItem> userItemList2;
    private Button profile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);
        profile = findViewById(R.id.profile_button);
        userItemList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        UserItem user1 = new UserItem("user1", "");
        userItemList2 = new ArrayList<>();

        userItemList2.add(user1);


        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // userItemList = getIntent().getParcelableArrayListExtra("users");
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                String userName = d.getString("First Name") + " " + d.getString("Last Name");
                                UserItem user = new UserItem(userName, "");
                                //userItemList
                                //UserItem users = d.toObject(UserItem.class);
                                userItemList.add(user);
                                //Toast.makeText(UserListActivity.this, userName, Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(UserListActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                        createRecyclerView();

                    }
        });
    }

    private void createRecyclerView() {
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
}
