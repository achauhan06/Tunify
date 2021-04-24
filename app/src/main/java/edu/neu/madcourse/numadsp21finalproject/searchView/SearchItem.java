package edu.neu.madcourse.numadsp21finalproject.searchView;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import edu.neu.madcourse.numadsp21finalproject.ChatActivity;
import edu.neu.madcourse.numadsp21finalproject.FriendsBlogActivity;
import edu.neu.madcourse.numadsp21finalproject.UserProfileActivity;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendProfile;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsPlaylistActivity;

public class SearchItem implements SearchViewListener{
    private Context context;
    private final String userId;
    private final String friendId;
    private final String searchName;
    private String genres;
    private String profileLink;
    private boolean isFriend;
    private String email;

    public String getSearchName() {
        return searchName;
    }

    public SearchItem(String userId, String friendId, String searchName, String email, Context context){
        this.context = context;
        this.userId = userId;
        this.friendId = friendId;
        this.searchName = searchName;
        this.email = email;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public boolean isFriend(){return isFriend;}

    public void setIsFriend(boolean isFriend){this.isFriend = isFriend;}

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this.context, isFriend ? FriendProfile.class
                : UserProfileActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("friendId", friendId);
        intent.putExtra("friendName", searchName);
        intent.putExtra("email", email);
        this.context.startActivity(intent);
    }

    @Override
    public void onViewPlaylistClick(int position) {
        Intent intent = new Intent(this.context, FriendsPlaylistActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("friendId", friendId);
        intent.putExtra("friendName", searchName);
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
        //intent.putExtra("friendId", friendId);
        this.context.startActivity(intent);
    }

    public String getGenres() {
        return this.genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}


