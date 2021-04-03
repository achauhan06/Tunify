package edu.neu.madcourse.numadsp21finalproject.songview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;

public class SongAdapter extends RecyclerView.Adapter<SongHolder>{

    private final List<SongItem> songItems;
    private final SongViewListener songViewListener;
    private final Context context;

    public SongAdapter(List<SongItem> songItems,
                       SongViewListener songViewListener,
                       Context context) {
        this.songItems = songItems;
        this.songViewListener = songViewListener;
        this.context = context;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);
        return new SongHolder(view, context, songViewListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        SongItem currentItem = songItems.get(position);
        holder.songName.setText(currentItem.getSongName());

    }

    @Override
    public int getItemCount() {
        return songItems.size();
    }
}
