package edu.neu.madcourse.numadsp21finalproject.commentview;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.HomeActivity;
import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsPlaylistActivity;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryActivity;
import edu.neu.madcourse.numadsp21finalproject.navigation.ProfileActivity;
import edu.neu.madcourse.numadsp21finalproject.service.FirebaseInstanceMessagingService;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class CommentActivity extends AppCompatActivity {
    EditText input;
    ImageButton post;
    private ArrayList<CommentItem> commentItemArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId, userName, recordingId,ownerId,ownerName, projectName, prev;
    private FirebaseInstanceMessagingService firebaseInstanceMessagingService;
    private BroadcastReceiver myBroadcastReceiver = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(CommentActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        userId = getIntent().getExtras().getString("userId");
        userName = Helper.getUsername(this);
        recordingId = getIntent().getExtras().getString("recordingId");
        ownerId = getIntent().getExtras().getString("ownerId");
        projectName = getIntent().getExtras().getString("projectName");

        prev = getIntent().getExtras().getString("prev");

        commentItemArrayList = new ArrayList<>();
        input = findViewById(R.id.comment_input);
        post = findViewById(R.id.comment_submit_btn);
        getAllComments();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = input.getText().toString();

                if (inputStr.isEmpty()) {
                    Toast.makeText(CommentActivity.this, "Message cannot be empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Timestamp timestamp = new Timestamp(new Date());
                Map<String, Object> comment = new HashMap<>();
                comment.put("commenterName", userName);
                comment.put("commenterId", userId);
                comment.put("content", inputStr);
                comment.put("time", timestamp);

                db.collection("recordings/" + recordingId + "/comments")
                        .add(comment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        updateCommentCount();
                        CommentItem myComment = new CommentItem(userName, inputStr, timestamp);
                        commentItemArrayList.add(myComment);
                        createCommentRecyclerView();
                        input.setText("");
                        if(!userId.equals(ownerId)) {
                            ownerName = getIntent().getExtras().getString("ownerName");
                            firebaseInstanceMessagingService.sendMessageToDevice(ownerId, ownerName,"New Comment",
                                    userName + " commented your project " + projectName, recordingId,projectName,CommentActivity.this);
                        }
                        input.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CommentActivity.this, "Some Error Occurred. Failed to post comment",Toast.LENGTH_SHORT).show();

                            }
                        });


            }
        });
    }

    private void updateCommentCount() {
        DocumentReference documentReference = db.collection("recordings" ).document(recordingId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                // DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                transaction.update(documentReference, "commentsCount", commentItemArrayList.size());
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this, "Some error occurred. " +
                        "Unable to update comment count", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void createCommentRecyclerView() {

        if(commentItemArrayList.size() > 0) {
            Collections.sort(commentItemArrayList, new Comparator<CommentItem>() {
                @Override
                public int compare(CommentItem o1, CommentItem o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
        }

        RecyclerView.LayoutManager rLayoutManger = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.comments_recycler_view);
        recyclerView.setHasFixedSize(true);
        CommentAdapter commentAdapter = new CommentAdapter(commentItemArrayList, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.scrollToPosition(commentItemArrayList.size() - 1);

    }

    private void getAllComments() {
        db.collection("recordings/" + recordingId + "/comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String commenterName = documentSnapshot.get("commenterName").toString();
                                String content = documentSnapshot.get("content").toString();
                                Timestamp timestamp = (Timestamp) documentSnapshot.get("time");
                                CommentItem item = new CommentItem(commenterName, content, timestamp);
                                commentItemArrayList.add(item);
                            }

                        }else {
                            Toast.makeText(CommentActivity.this, "Some error occurred. Unable to load comments.",Toast.LENGTH_SHORT).show();

                        }
                        createCommentRecyclerView();
                    }
                });
    }

    @Override
    public void onBackPressed() {

        /*if(prev.equals("home")){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);

        }else if(prev.equals("library")){
            Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
            startActivity(intent);
        }else if(prev.equals("friendsPlaylist")){
            Intent intent = new Intent(getApplicationContext(), FriendsPlaylistActivity.class);
            startActivity(intent);
        }*/


        this.finish();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void broadcastIntent() {
        registerReceiver(myBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            //Register or UnRegister your broadcast receiver here
            unregisterReceiver(myBroadcastReceiver);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
