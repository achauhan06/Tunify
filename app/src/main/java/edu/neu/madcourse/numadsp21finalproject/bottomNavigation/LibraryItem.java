package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.commentview.CommentActivity;
import edu.neu.madcourse.numadsp21finalproject.commentview.CommentItem;

public class LibraryItem implements LibraryViewClickListener{
    private final String projectName, path, genre, userId, recordingId;
    private final Context context;
    private final Timestamp timestamp;
    private ArrayList<String> likesList = new ArrayList<>();
    private boolean likedByMe;
    private int likeCount, commentCount;
    private MediaPlayer mediaPlayer;
    private Uri currentUri;
    private final boolean isLoaded = false;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;

    public LibraryItem(QueryDocumentSnapshot documentSnapshot, Context context, String userId) {
        this.projectName = documentSnapshot.get("name").toString();
        this.path = documentSnapshot.get("path").toString();
        this.genre = documentSnapshot.get("genre").toString();
        this.timestamp = (Timestamp) documentSnapshot.get("time");
        this.commentCount = documentSnapshot.getLong("commentsCount").intValue();
        this.likesList = (ArrayList<String>) documentSnapshot.get("likes");
        this.likeCount = likesList.size();
        this.likedByMe = likesList.contains(userId);
        this.context = context;
        this.userId = userId;
        this.recordingId = documentSnapshot.getId();
        this.documentReference = documentSnapshot.getReference();
    }
    /*
    public LibraryItem(String projectName, String path, String genre,  Context context, String userId, String recordingId) {
        this.projectName = projectName;
        this.path = path;
        this.genre = genre;
        this.context = context;
        this.userId = userId;
        this.recordingId = recordingId;
        // prepareAudio();
    }

     */

    public String getProjectName() {
        return projectName;
    }

    public String getGenre() {
        return genre;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTimeString() {
        return timestamp.toDate().toString();
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public boolean isLikedByMe() {
        return likedByMe;
    }

    private void prepareAudio() {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(this.path);
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

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void onItemClick(int position) {
        if(!this.isLoaded) {
            prepareAudio();
        }else {
            mediaPlayer.start();
        }

    }

    @Override
    public void onPauseClick(int position) {
        mediaPlayer.pause();
    }

    @Override
    public void onStopClick(int position) {
        mediaPlayer.stop();
        createMediaPlayer(currentUri);
    }

    @Override
    public void onCommentClick(int position) {

        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("recordingId", recordingId);
        intent.putExtra("prev","library");

        context.startActivity(intent);


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


}
