package edu.neu.madcourse.numadsp21finalproject.songview;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class SongHolder extends RecyclerView.ViewHolder{

    public TextView songName;
    public Button songPlay;

    public SongHolder(@NonNull View itemView,
                      Context context,
                      final SongViewListener songViewListener) {
        super(itemView);
        songName = itemView.findViewById(R.id.song_item);
        songPlay = itemView.findViewById(R.id.play_button);

        songPlay.setOnClickListener(v -> {
            if (songViewListener != null) {
                int position = getLayoutPosition();
                songViewListener.onItemClick(position,context);
            }
        });
    }
}
