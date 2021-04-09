package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.content.Intent;

public class FriendItem implements FriendViewClickListener{
    private final String userName;
    private Context context;
    private final String friendName;
    private final String friendToken;

    public String getUserName() {
        return userName;
    }


    public String getFriendName() {
        return friendName;
    }

    public FriendItem(String userName, String friendName, String friendToken, Context context){
        this.userName = userName;
        this.context = context;
        this.friendName = friendName;
        this.friendToken = friendToken;
    }

    public String getFriendToken() {return friendToken;}

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(context, FriendsActivity.class);
        intent.putExtra("userName", userName);
        intent.putExtra("friendName", friendName);
        intent.putExtra("friendToken", friendToken);
        context.startActivity(intent);
    }
}
