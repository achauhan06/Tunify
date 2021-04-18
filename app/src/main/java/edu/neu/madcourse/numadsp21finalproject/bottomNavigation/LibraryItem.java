package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class LibraryItem implements LibraryViewClickListener{
    private final String projectName;
    private final String path;
    private final String genre;
    private Context context;
    private MediaPlayer mediaPlayer;

    public LibraryItem(String projectName, String path, String genre,  Context context) {
        this.projectName = projectName;
        this.path = path;
        this.genre = genre;
        this.context = context;
        prepareAudio();
    }

    public String getProjectName() {
        return projectName;
    }

    public String getGenre() {
        return genre;
    }


    private void prepareAudio() {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(this.path);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

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
                } catch (IOException e) {
                    Toast.makeText(context , e.toString(),Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context , exception.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void onItemClick(int position) {
        mediaPlayer.start();
    }

    @Override
    public void onPauseClick(int position) {
        mediaPlayer.pause();
    }

    @Override
    public void onStopClick(int position) {
        mediaPlayer.stop();
    }
}
