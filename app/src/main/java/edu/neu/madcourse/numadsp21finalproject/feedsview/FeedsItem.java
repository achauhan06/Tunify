package edu.neu.madcourse.numadsp21finalproject.feedsview;

import android.content.Context;
import android.content.Intent;
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
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendProfile;
import edu.neu.madcourse.numadsp21finalproject.commentview.CommentActivity;
import edu.neu.madcourse.numadsp21finalproject.commentview.CommentItem;
import edu.neu.madcourse.numadsp21finalproject.navigation.ProfileActivity;

public class FeedsItem implements FeedsViewListener{
    private final String projectName, path, genre, ownerName, recordingId;
    private final Timestamp timestamp;
    private int commentsCount;
    private Context context;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean loaded = false;
    private ArrayList<String> likesList = new ArrayList<>();
    private boolean likedByMe;
    private int likeCount;
    private final DocumentReference documentReference;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseFirestore firebaseFirestore;

    public FeedsItem(DocumentSnapshot documentSnapshot, Context context) {
        this.path = documentSnapshot.get("path").toString();
        this.projectName = documentSnapshot.get("name").toString();
        this.genre = documentSnapshot.get("genre").toString();
        this.ownerName = documentSnapshot.get("username").toString();
        this.timestamp = (Timestamp) documentSnapshot.get("time");
        this.likesList = (ArrayList<String>) documentSnapshot.get("likes");
        this.likeCount = likesList.size();
        this.likedByMe = likesList.contains(userId);
        this.context = context;
        this.documentReference = documentSnapshot.getReference();
        this.recordingId = documentSnapshot.getId();
        this.commentsCount = documentSnapshot.getLong("commentsCount").intValue();
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
    public String getTimeString() {
        return timestamp.toDate().toString();
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
    public int getCommentsCount() {
        return commentsCount;
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
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("recordingId", recordingId);
        intent.putExtra("prev","home");


        context.startActivity(intent);
        // Toast.makeText(context , "commented " + position,Toast.LENGTH_SHORT).show();

    }
}
