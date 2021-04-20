package edu.neu.madcourse.numadsp21finalproject.jamview;

import android.content.Context;

import java.util.HashMap;

public class JamItem implements JamViewListener {
    private String groupName;
    private HashMap<String, String> friendMap;

    public JamItem(String groupName, HashMap<String, String> friendMap) {
        this.groupName = groupName;
        this.friendMap = friendMap;
    }

    public String getGroupName() {
        return groupName;
    }

    public HashMap<String, String> getFriendMap() {
        return friendMap;
    }

    @Override
    public void onItemClick(int position, Context context) {

    }

    @Override
    public void onItemDelete(int position) {

    }
}
