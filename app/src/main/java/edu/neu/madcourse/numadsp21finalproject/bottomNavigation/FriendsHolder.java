package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class FriendsHolder extends RecyclerView.ViewHolder {
    public TextView friendName, friendsCategory;
    public ImageButton viewProfile,viewPlaylist, viewFriendBlog, chat;
    public ImageView profilePicture;

    public FriendsHolder(@NonNull View itemView,
                      Context context,
                      final FriendViewClickListener friendViewClickListener) {
        super(itemView);
        friendName = itemView.findViewById(R.id.friend_name);
        viewProfile = itemView.findViewById(R.id.friend_profile_button);
        viewPlaylist = itemView.findViewById(R.id.friend_songs_button);
        friendsCategory = itemView.findViewById(R.id.genres_liked);
        viewFriendBlog = itemView.findViewById(R.id.friend_blog_button);
        chat = itemView.findViewById(R.id.friend_chat_button);
        profilePicture = itemView.findViewById(R.id.userProfilePicture);

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

        viewFriendBlog.setOnClickListener(v -> {
            if (friendViewClickListener != null) {
                int position = getLayoutPosition();
                friendViewClickListener.onViewBlogClick(position);
            }
        });
        chat.setOnClickListener(v -> {
            if (friendViewClickListener != null) {
                int position = getLayoutPosition();
                friendViewClickListener.onChatClick(position);
           }
        });
    }
}
