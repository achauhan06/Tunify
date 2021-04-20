package edu.neu.madcourse.numadsp21finalproject.jamview;

import android.content.Context;

import java.util.HashMap;

public class JamItem implements JamViewListener {
    private String groupName;

    public JamItem(String groupName, HashMap<String, String> friendMap) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    @Override
    public void onItemClick(int position, Context context) {

    }

    @Override
    public void onItemDelete(int position) {

    }
}
