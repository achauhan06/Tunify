package edu.neu.madcourse.numadsp21finalproject.friendGroupView;

public class FriendGroupItem implements FriendGroupListener {

    private String friendName;
    private String friendToken;
    private boolean isChecked;

    public FriendGroupItem(String friendName, String friendToken) {
        this.friendName = friendName;
        this.friendToken = friendToken;
        this.isChecked = false;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getFriendToken() {
        return friendToken;
    }

    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void onItemClicked(int position, boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void setChecked() {
        this.isChecked = false;
    }
}
