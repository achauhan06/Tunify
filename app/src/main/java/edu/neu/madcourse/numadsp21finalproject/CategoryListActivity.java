package edu.neu.madcourse.numadsp21finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.categoryview.CategoryAdapter;
import edu.neu.madcourse.numadsp21finalproject.categoryview.CategoryItem;
import edu.neu.madcourse.numadsp21finalproject.categoryview.CategoryViewListener;
import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class CategoryListActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<CategoryItem> categoryItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_main);
        Toolbar toolbar = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        createRecyclerView();
    }

    private void createRecyclerView() {

        categoryItemList = new ArrayList<>();
        for(int i = 0; i<Helper.CATEGORY_LIST.length; i++) {
            List<SongItem> songItemList = new ArrayList(){{
                add(new SongItem("Complicated", "Hello",
                        "https://www.youtube.com/watch?v=vnsZ6VrprHw&ab_channel=KaraFunKaraoke"));
                add(new SongItem("Hello1", "Hello1", "Hello1"));
                add(new SongItem("Hello2", "Hello2", "Hello2"));
            }};
            CategoryItem categoryItem = new CategoryItem(Helper.CATEGORY_LIST[i],
                    songItemList,
                    i > 2);
            categoryItemList.add(categoryItem);
        }



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

}



