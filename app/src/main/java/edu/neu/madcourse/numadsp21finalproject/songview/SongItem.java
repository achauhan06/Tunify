package edu.neu.madcourse.numadsp21finalproject.songview;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.SongListActivity;
import edu.neu.madcourse.numadsp21finalproject.SongTrackActivity;

public class SongItem implements Parcelable,SongViewListener {

    private String songName;
    private String songLength;
    private String songURL;
    private String songArtist;

    public SongItem(String songName, String songLength, String songURL, String songArtist) {
        this.songName = songName;
        this.songLength = songLength;
        this.songURL = songURL;
        this.songArtist = songArtist;
    }

    public SongItem(Parcel in) {
        this.songName = in.readString();
        this.songLength = in.readString();
        this.songURL =  in.readString();
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


    @Override
    public void onItemClick(int position, Context context) {
        Intent intent = new Intent(context, SongTrackActivity.class);
        intent.putExtra("songName", songName);
        intent.putExtra("songUrl", songURL);
        intent.putExtra("duration", songLength);
        intent.putExtra("artist", songArtist);
        context.startActivity(intent);
    }
}
