package edu.neu.madcourse.numadsp21finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.categoryview.CategoryAdapter;
import edu.neu.madcourse.numadsp21finalproject.categoryview.CategoryItem;
import edu.neu.madcourse.numadsp21finalproject.categoryview.CategoryViewListener;
import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;
import edu.neu.madcourse.numadsp21finalproject.utils.SongListByCategory;

public class CategoryListActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<CategoryItem> categoryItemList;
    private String currentEmail;

    private BroadcastReceiver myBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_main);
        Toolbar toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(CategoryListActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        currentEmail = getIntent().getExtras().getString("currentEmail");
        createRecyclerView();
    }

    private void createRecyclerView() {



        Helper.db.collection("users").whereEqualTo("Email",currentEmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {

                        if (value != null) {
                            DocumentSnapshot documentSnapshot = value.getDocuments().get(0);
                            String genresString = documentSnapshot.getString("Genres") != null ?
                                    documentSnapshot.getString("Genres") : "";
                                    ;
                            String[] genreArray = genresString.split(";");
                            createCategoryListView(Arrays.asList(genreArray));
                        }

                    }
                });
    }

    private void createCategoryListView(List<String> selectedCategories) {
        categoryItemList = new ArrayList<>();
        SongListByCategory.createSongListByCategory();
        for(int i = 0; i<Helper.CATEGORY_LIST.length; i++) {
            final String categoryName = Helper.CATEGORY_LIST[i];
            List<SongItem> songItemList = SongListByCategory.getSongListForCategory(categoryName);
            CategoryItem categoryItem = new CategoryItem(categoryName,
                    songItemList,
                    !selectedCategories.contains(categoryName));
            categoryItemList.add(categoryItem);
        }

        Collections.sort(categoryItemList, (o1, o2) -> {
            if (!o1.isLocked())
                return -1;
            else return 0;
        });



        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.category_recycler_view);
        recyclerView.setHasFixedSize(true);
        CategoryViewListener categoryViewListener = new CategoryViewListener() {
            @Override
            public void onItemClick(int position, Context context) {
                categoryItemList.get(position).onItemClick(position,context);
            }
        };
        categoryAdapter = new CategoryAdapter(categoryItemList, categoryViewListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(categoryAdapter);
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



