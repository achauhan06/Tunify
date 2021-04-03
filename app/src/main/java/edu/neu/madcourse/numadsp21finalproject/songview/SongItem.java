package edu.neu.madcourse.numadsp21finalproject.songview;

import android.os.Parcel;
import android.os.Parcelable;


public class SongItem implements Parcelable {

    private String songName;
    private String songLength;
    private String songURL;

    public SongItem(String songName, String songLength, String songURL) {
        this.songName = songName;
        this.songLength = songLength;
        this.songURL = songURL;
    }

    public SongItem(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songName);
        dest.writeString(songLength);
        dest.writeString(songURL);
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<SongItem> CREATOR = new Creator<SongItem>() {
        @Override
        public SongItem createFromParcel(Parcel in) {
            return new SongItem(in);
        }

        @Override
        public SongItem[] newArray(int size) {
            return new SongItem[size];
        }
    };
    
    public String getSongName() {
        return songName;
    }

    public String getSongLength() {
        return songLength;
    }

    public String getSongURL() {
        return songURL;
    }
}
