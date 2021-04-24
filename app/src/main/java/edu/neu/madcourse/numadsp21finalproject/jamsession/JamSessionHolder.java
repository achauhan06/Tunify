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
    ImageButton deleteItem;
    ImageButton cancelDelete;
    View viewToUpdate;
    View deleteSection ;

    public JamSessionHolder(View view, JamSessionListener jamSessionlistener) {
        super(view);
        friendName = view.findViewById(R.id.show_jam_name);
        messageTime = view.findViewById(R.id.show_jam_time);
        songName = view.findViewById(R.id.show_jam_message);
        playPause = view.findViewById(R.id.jam_play_pause_btn);
        deleteItem = view.findViewById(R.id.delete_jam_item);
        cancelDelete = view.findViewById(R.id.cancel_delete_jam_item);
        deleteSection = view.findViewById(R.id.delete_section);

        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDeleteView(v);
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jamSessionlistener != null) {
                    int position = getLayoutPosition();
                    jamSessionlistener.onButtonClick(position);
                }
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDeleteView(v);
                int position = getLayoutPosition();
                jamSessionlistener.onDeleteItem(position);

            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                viewToUpdate = v;
                v.setBackgroundColor(v.getResources()
                        .getColor(R.color.common_google_signin_btn_text_light_disabled));
                deleteSection.setVisibility(View.VISIBLE);
                playPause.setVisibility(View.INVISIBLE);
                return false;
            }
        });
    }

    private void clearDeleteView(View view) {
        deleteSection.setVisibility(View.INVISIBLE);
        playPause.setVisibility(View.VISIBLE);
        viewToUpdate.setBackgroundColor(view.getResources()
                .getColor(R.color.jamPrimaryLightColor));
    }


}
