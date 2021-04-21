package edu.neu.madcourse.numadsp21finalproject.jamsession;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class JamSessionItem implements JamSessionListener {

    private String userName;
    private String userId;
    private String songName;
    private Timestamp timeUpdated;
    private String songLink;
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
        this.songLink = songLink;
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

    private void prepareAudio() {
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
}
