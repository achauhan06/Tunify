package edu.neu.madcourse.numadsp21finalproject.jamview;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.JamSessionActivity;

public class JamItem implements JamViewListener {
    private String groupName;
    private Long songVersion;
    private HashMap<String, String> friendMap;

    public JamItem(String groupName, HashMap<String, String> friendMap, Long songVersion) {
        this.groupName = groupName;
        this.friendMap = friendMap;
        this.songVersion = songVersion;
    }

    public String getGroupName() {
        return groupName;
    }

    public HashMap<String, String> getFriendMap() {
        return friendMap;
    }

    @Override
    public void onItemClick(int position, Context context) {

        String members = "";
        for(Map.Entry entry : friendMap.entrySet()) {
            members += entry.getKey() + ":" + entry.getValue() + ";";
        }
        Intent intent = new Intent(context,JamSessionActivity.class);
        intent.putExtra("groupName", groupName);
        intent.putExtra("songVersion", songVersion);
        intent.putExtra("members", members);
        context.startActivity(intent);
    }

    @Override
    public void onItemDelete(int position) {

    }
}
