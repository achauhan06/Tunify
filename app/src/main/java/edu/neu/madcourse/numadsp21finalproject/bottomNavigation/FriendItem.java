package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.content.Intent;

public class FriendItem implements FriendViewClickListener{
    private Context context;
    private final String userId;
    private final String friendId;
    private final String friendName;



    public String getFriendName() {
        return friendName;
    }

    public FriendItem(String userId, String friendId, String friendName,Context context){
        this.context = context;
        this.userId = userId;
        this.friendId = friendId;
        this.friendName = friendName;

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
}
