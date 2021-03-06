package edu.neu.madcourse.numadsp21finalproject.jamsession;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Objects;

public class JamSessionItem implements JamSessionListener {

    private String userName;
    private String userId;
    private String songName;
    private Timestamp timeUpdated;
    private String songLink;
    private String recordingId;
    private MediaPlayer mediaPlayer;
    private final boolean isLoaded = false;
    private Uri currentUri;
    private final Context context;

    public JamSessionItem(String userName,
                          String userId,
                          String songName,
                          Timestamp timeUpdated,
                          String songLink, Context context) {
        this.userName = userName;
        this.userId = userId;
        this.songName = songName;
        this.timeUpdated = timeUpdated;
        this.songLink = songLink.replace("%20", " ").replace("%2C", ",");;
        this.context = context;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getSongName() {
        return songName;
    }

    public String getTimeUpdated() {
        return timeUpdated.toDate().toString();
    }

    @Override
    public void onButtonClick(int pos) {
        if(!this.isLoaded) {
            prepareAudio();
        }else {
            mediaPlayer.start();
        }
    }

    @Override
    public void onDeleteItem(int position) {

    }

    private void prepareAudio() {
        this.songLink.replace("%2C", ",");
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(this.songLink);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                currentUri = uri;
                createMediaPlayer(uri);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context , exception.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createMediaPlayer(Uri uri) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(String.valueOf(uri));
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            Toast.makeText(context , e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId,songName);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof JamSessionItem))
            return false;
        if (obj == this)
            return true;
        return this.getUserId().equals(((JamSessionItem) obj ).getUserId())
                && this.getSongName().equals(((JamSessionItem) obj ).getSongName());
    }
}
