package edu.neu.madcourse.numadsp21finalproject.jamsession;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class JamSessionHolder extends RecyclerView.ViewHolder {

    TextView friendName;
    TextView messageTime;
    TextView songName;
    ImageButton playPause;

    public JamSessionHolder(View view) {
        super(view);
        friendName = view.findViewById(R.id.show_jam_name);
        messageTime = view.findViewById(R.id.show_jam_time);
        songName = view.findViewById(R.id.show_jam_message);
        playPause = view.findViewById(R.id.jam_play_pause_btn);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
