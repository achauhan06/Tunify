package edu.neu.madcourse.numadsp21finalproject.jamsession;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;

public class JamSessionAdapter extends RecyclerView.Adapter<JamSessionHolder> {

    private List<JamSessionItem> jamSessionItemList;
    private String userName;
    public static final int DISPLAY_LEFT = 0;
    public static final int DISPLAY_RIGHT = 1;
    private JamSessionListener jamSessionlistener;


    public JamSessionAdapter(List<JamSessionItem> jamSessionItemList, String userName) {
        this.userName = userName;
        this.jamSessionItemList = jamSessionItemList;
    }

    @NonNull
    @Override
    public JamSessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == DISPLAY_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jam_session_left,
                    parent, false);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jam_session_right,
                    parent, false);

        }
        return new JamSessionHolder(view);
    }

    public void setListener(JamSessionListener listener) {
        this.jamSessionlistener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull JamSessionHolder holder, int position) {
        JamSessionItem jamSessionItem = jamSessionItemList.get(position);
        holder.friendName.setText(jamSessionItem.getUserName());
        holder.songName.setText(jamSessionItem.getSongName());
        holder.messageTime.setText(jamSessionItem.getTimeUpdated());
        holder.playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jamSessionlistener != null) {
                    jamSessionlistener.onButtonClick(position);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(jamSessionItemList.get(position).getUserName().equals(userName)) {
            return DISPLAY_RIGHT;
        }
        return DISPLAY_LEFT;
    }

    @Override
    public int getItemCount() {
        return jamSessionItemList.size();
    }
}
