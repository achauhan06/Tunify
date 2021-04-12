package edu.neu.madcourse.numadsp21finalproject.users;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import edu.neu.madcourse.numadsp21finalproject.SongTrackActivity;
import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;

public class UserItem implements Parcelable, UserViewListener{

    private String userName;
    private String profileLink;

    public UserItem(String userName, String profileLink) {
        this.userName =  userName;
        this.profileLink = profileLink;
    }

    public UserItem(Parcel in) {
        this.userName = in.readString();
        this.profileLink = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(profileLink);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Parcelable.Creator<UserItem> CREATOR = new Parcelable.Creator<UserItem>() {
        @Override
        public UserItem createFromParcel(Parcel in) {
            return new UserItem(in);
        }

        @Override
        public UserItem[] newArray(int size) {
            return new UserItem[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    @Override
    public void onItemClick(int position, Context context) {
        /*Intent intent = new Intent(context, SongTrackActivity.class);
        intent.putExtra("userName", userName);
        intent.putExtra("profileLink", profileLink);
        context.startActivity(intent);*/
    }


}
