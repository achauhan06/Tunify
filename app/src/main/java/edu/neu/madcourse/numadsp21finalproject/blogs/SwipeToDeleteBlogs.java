package edu.neu.madcourse.numadsp21finalproject.blogs;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryAdapter;

public class SwipeToDeleteBlogs extends ItemTouchHelper.SimpleCallback{

    private BlogAdapter blogAdapter;

    public SwipeToDeleteBlogs(BlogAdapter blogAdapter) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.blogAdapter = blogAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        blogAdapter.deleteItem(position);
    }
}
