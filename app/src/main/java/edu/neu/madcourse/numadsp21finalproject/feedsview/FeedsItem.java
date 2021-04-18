package edu.neu.madcourse.numadsp21finalproject.feedsview;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Date;

public class FeedsItem implements FeedsViewListener{
    private final String projectName;
    private final String path;
    private final String genre;
    private final String time;
    private final Timestamp timestamp;
    private final String ownerName;
    private Context context;
    private MediaPlayer mediaPlayer;

    public FeedsItem(String projectName, String path, String genre, String ownerName,
                     Timestamp timestamp, String time, Context context) {
        this.projectName = projectName;
        this.path = path;
        this.genre = genre;
        this.time = time;
        this.timestamp = timestamp;
        this.ownerName = ownerName;
        this.context = context;


        prepareAudio();
    }



    public String getOwnerName() {

        return ownerName;
    }


    public String getProjectName() {

        return projectName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTime() {
        return time;
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

    @Override
    public void onItemClick(int position) {

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

        }else {
            mediaPlayer.start();
        }



    }
}
