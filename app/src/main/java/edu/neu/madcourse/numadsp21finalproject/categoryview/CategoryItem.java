package edu.neu.madcourse.numadsp21finalproject.categoryview;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.SongListActivity;
import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;

public class CategoryItem implements CategoryViewListener{
    private String categoryName;
    private List<SongItem> songsList;
    private boolean isLocked;

    public CategoryItem(String categoryName, List<SongItem> songsList, boolean isLocked) {
        this.categoryName = categoryName;
        this.songsList = songsList;
        this.isLocked = isLocked;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<SongItem> getSongsList() {
        return songsList;
    }

    @Override
    public void onItemClick(int position, Context context) {
        Intent intent = new Intent(context, SongListActivity.class);
        intent.putExtra("categoryName", categoryName);
        intent.putParcelableArrayListExtra("songs",
                (ArrayList<? extends Parcelable>) songsList);
        context.startActivity(intent);
    }

    public boolean isLocked() {
        return isLocked;
    }
}
