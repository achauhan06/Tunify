package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteLibrary extends ItemTouchHelper.SimpleCallback {

    private LibraryAdapter libraryAdapter;

    public SwipeToDeleteLibrary(LibraryAdapter libraryAdapter) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.libraryAdapter = libraryAdapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        libraryAdapter.deleteItem(position);
        //TODO delete from server
    }
}
