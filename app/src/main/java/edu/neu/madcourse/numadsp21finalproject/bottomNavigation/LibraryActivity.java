package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.R;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private LibraryAdapter libraryAdapter;
    private ArrayList<LibraryItem> libraryList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_library);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        libraryList = new ArrayList<>();
        LibraryItem item1 = new LibraryItem("test");
        libraryList.add(item1);
        createRecyclerView();
    }

    private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.library_recycler_view);
        recyclerView.setHasFixedSize(true);
        LibraryViewClickListener libraryViewClickListener = new LibraryViewClickListener() {
            @Override
            public void onItemClick(int position) {
                libraryList.get(position).onItemClick(position);
            }
        };
        libraryAdapter = new LibraryAdapter(libraryList, libraryViewClickListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(libraryAdapter);

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
