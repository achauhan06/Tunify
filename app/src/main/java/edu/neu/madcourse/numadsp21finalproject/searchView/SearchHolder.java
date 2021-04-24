package edu.neu.madcourse.numadsp21finalproject.searchView;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendViewClickListener;

public class SearchHolder extends RecyclerView.ViewHolder {
    public TextView searchName, searchCategory;
    public ImageButton viewProfile,viewPlaylist, viewFriendBlog, chat;
    public ImageView profilePicture;

    public SearchHolder(@NonNull View itemView,
                         Context context,
                         final SearchViewListener searchViewListener) {
        super(itemView);
        searchName = itemView.findViewById(R.id.search_name);
        viewProfile = itemView.findViewById(R.id.search_profile_button);
        viewPlaylist = itemView.findViewById(R.id.search_songs_button);
        searchCategory = itemView.findViewById(R.id.search_genres_liked);
        viewFriendBlog = itemView.findViewById(R.id.search_blog_button);
        chat = itemView.findViewById(R.id.search_chat_button);
        profilePicture = itemView.findViewById(R.id.search_profile_picture);

        viewProfile.setOnClickListener(v -> {
            if (searchViewListener != null) {
                int position = getLayoutPosition();
                searchViewListener.onItemClick(position);
            }
        });

        viewPlaylist.setOnClickListener(v -> {
            if (searchViewListener != null) {
                int position = getLayoutPosition();
                searchViewListener.onViewPlaylistClick(position);
            }
        });

        viewFriendBlog.setOnClickListener(v -> {
            if (searchViewListener != null) {
                int position = getLayoutPosition();
                searchViewListener.onViewBlogClick(position);
            }
        });
        chat.setOnClickListener(v -> {
            if (searchViewListener != null) {
                int position = getLayoutPosition();
                searchViewListener.onChatClick(position);
            }
        });
    }
}
