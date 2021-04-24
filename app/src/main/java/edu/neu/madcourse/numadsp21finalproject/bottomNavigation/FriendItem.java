package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import edu.neu.madcourse.numadsp21finalproject.ChatActivity;
import edu.neu.madcourse.numadsp21finalproject.FriendsBlogActivity;

public class FriendItem implements FriendViewClickListener{

    private Context context;
    private final String userId;
    private final String friendId;
    private final String friendName;
    private String genres;
    private String profileLink;

    public String getFriendName() {
        return friendName;
    }

    public FriendItem(String userId, String friendId, String friendName,Context context){
        this.context = context;
        this.userId = userId;
        this.friendId = friendId;
        this.friendName = friendName;

    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getProfileLink() {
        return profileLink;
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.context, FriendProfile.class);
        intent.putExtra("userId", userId);
        intent.putExtra("friendId", friendId);
        intent.putExtra("friendName", friendName);
        this.context.startActivity(intent);
        // Toast.makeText(context, friendId + " " + friendName,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onViewPlaylistClick(int position) {
        Intent intent = new Intent(this.context, FriendsPlaylistActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("friendId", friendId);
        intent.putExtra("friendName", friendName);
        this.context.startActivity(intent);
    }

    @Override
    public void onViewBlogClick(int position) {
        Intent intent = new Intent(this.context, FriendsBlogActivity.class);
        intent.putExtra("friendId", friendId);
        this.context.startActivity(intent);
    }

    @Override
    public void onChatClick(int position) {
        Toast.makeText(context, "Entering chat",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this.context, ChatActivity.class);
        intent.putExtra("friendId", friendId);
        intent.putExtra("friendName", friendName);
        this.context.startActivity(intent);
    }

    public String getGenres() {
        return this.genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
