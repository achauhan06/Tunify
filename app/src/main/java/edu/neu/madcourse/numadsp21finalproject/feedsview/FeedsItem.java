package edu.neu.madcourse.numadsp21finalproject.feedsview;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import edu.neu.madcourse.numadsp21finalproject.HomeActivity;
import edu.neu.madcourse.numadsp21finalproject.navigation.ProfileActivity;

public class FeedsItem implements FeedsViewListener{
    private final String projectName;
    private final String path;
    private final String genre;
    private final String time;
    private final Timestamp timestamp;
    private final String ownerName;
    private Context context;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean loaded = false;
    private ArrayList<String> likesList = new ArrayList<>();
    private boolean likedByMe;
    private int likeCount;
    private DocumentReference documentReference;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseFirestore firebaseFirestore;

    public FeedsItem(String projectName, String path, String genre, String ownerName,
                     Timestamp timestamp, String time, ArrayList<String> likesList,
                     Context context, DocumentReference documentReference) {
        this.projectName = projectName;
        this.path = path;
        this.genre = genre;
        this.time = time;
        this.timestamp = timestamp;
        this.ownerName = ownerName;
        this.likesList = likesList;
        this.likeCount = likesList.size();
        this.likedByMe = likesList.contains(userId);
        this.context = context;
        this.documentReference = documentReference;

        // new Thread(new MediaRunnable()).start();
        // prepareAudio();
    }

    class MediaRunnable implements Runnable {
        @Override
        public void run() {
            prepareAudio();
        }
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
    public boolean getLikedByMe() {
        return likedByMe;
    }
    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }




    private void prepareAudio() {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(this.path);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                try {

                    mediaPlayer.setAudioAttributes(
                            new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                    );
                    mediaPlayer.setDataSource(String.valueOf(uri));
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                    mediaPlayer.start();
                    loaded = true;
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
    public void onPlayPauseClick(int position) {
        if(!loaded) {
            prepareAudio();
        }else {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();

            }else {
                mediaPlayer.start();
            }
        }

    }

    @Override
    public void onLikeClick(int position) {
        if(this.likedByMe) {
            this.likedByMe = false;
            this.likeCount -= 1;
            likesList.remove(userId);
        }else {
            this.likedByMe = true;
            this.likeCount += 1;
            likesList.add(userId);
        }
        firebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                transaction.update(documentReference, "likes", likesList);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context , "you liked " + projectName,Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Unable to unlike", Toast.LENGTH_SHORT).show();

            }
        });



    }

    @Override
    public void onCommentClick(int position) {
        Toast.makeText(context , "commented " + position,Toast.LENGTH_SHORT).show();

    }
}
