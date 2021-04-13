package edu.neu.madcourse.numadsp21finalproject;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);
        userItemList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();


        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // userItemList = getIntent().getParcelableArrayListExtra("users");
        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                UserItem users = d.toObject(UserItem.class);
                                userItemList.add(users);
                            }
                        } else {
                            Toast.makeText(UserListActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        createRecyclerView();
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
