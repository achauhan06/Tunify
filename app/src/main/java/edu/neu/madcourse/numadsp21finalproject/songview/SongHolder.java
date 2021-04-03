package edu.neu.madcourse.numadsp21finalproject.songview;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SongHolder extends RecyclerView.ViewHolder{

    public TextView songName;

    public SongHolder(@NonNull View itemView,
                      Context context,
                      final SongViewListener songViewListener) {
        super(itemView);
    }
}
