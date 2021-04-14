package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import edu.neu.madcourse.numadsp21finalproject.R;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryHolder> {
    private ArrayList<LibraryItem> libraryItemArrayList;
    private LibraryViewClickListener libraryViewClickListener;
    private Context context;

    public LibraryAdapter(ArrayList<LibraryItem> libraryItemArrayList,
                          LibraryViewClickListener libraryViewClickListener,
                          Context context) {
        this.libraryItemArrayList = libraryItemArrayList;
        this.libraryViewClickListener = libraryViewClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public LibraryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent, false);
        return new LibraryHolder(view, context, libraryViewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryHolder holder, int position) {
        LibraryItem currentItem = libraryItemArrayList.get(position);
        holder.libraryItemName.setText(currentItem.getProjectName());

    }

    @Override
    public int getItemCount() {
        return libraryItemArrayList.size();
    }
}
