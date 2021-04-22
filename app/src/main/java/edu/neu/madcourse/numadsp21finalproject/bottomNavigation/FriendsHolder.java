package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class FriendsHolder extends RecyclerView.ViewHolder {
    public TextView friendName;
    public ImageButton viewProfile;
    public ImageButton viewPlaylist;

    public FriendsHolder(@NonNull View itemView,
                      Context context,
                      final FriendViewClickListener friendViewClickListener) {
        super(itemView);
        friendName = itemView.findViewById(R.id.friend_name);
        viewProfile = itemView.findViewById(R.id.friend_profile_button);
        viewPlaylist = itemView.findViewById(R.id.friend_songs_button);

        viewProfile.setOnClickListener(v -> {
            if (friendViewClickListener != null) {
                int position = getLayoutPosition();
                friendViewClickListener.onItemClick(position);
            }
        });

        viewPlaylist.setOnClickListener(v -> {
            if (friendViewClickListener != null) {
                int position = getLayoutPosition();
                friendViewClickListener.onViewPlaylistClick(position);
            }
        });
    }
}
