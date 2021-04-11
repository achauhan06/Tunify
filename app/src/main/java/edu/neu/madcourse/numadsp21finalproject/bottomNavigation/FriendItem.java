package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import edu.neu.madcourse.numadsp21finalproject.HomeActivity;

public class FriendItem implements FriendViewClickListener{
    private Context context;
    private final String friendId;
    private final String friendName;



    public String getFriendName() {
        return friendName;
    }

    public FriendItem(String friendId, String friendName,Context context){
        this.context = context;
        this.friendId = friendId;
        this.friendName = friendName;

    }


    @Override
    public void onItemClick(int position) {
        // Intent intent = new Intent(context, FriendsActivity.class);
        // intent.putExtra("friendId", friendId);
        // intent.putExtra("friendName", friendName);
        // context.startActivity(intent);
        Toast.makeText(context, friendId + " " + friendName,Toast.LENGTH_LONG).show();

    }
}
