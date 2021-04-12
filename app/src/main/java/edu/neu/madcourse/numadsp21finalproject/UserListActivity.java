package edu.neu.madcourse.numadsp21finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);
        Toolbar toolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userItemList = getIntent().getParcelableArrayListExtra("users");
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
