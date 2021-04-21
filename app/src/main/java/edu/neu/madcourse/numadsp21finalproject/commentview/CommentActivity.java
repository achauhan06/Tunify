package edu.neu.madcourse.numadsp21finalproject.commentview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryActivity;
import edu.neu.madcourse.numadsp21finalproject.service.FirebaseInstanceMessagingService;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class CommentActivity extends AppCompatActivity {
    EditText input;
    Button post;
    private ArrayList<CommentItem> commentItemArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId, userName, recordingId,ownerId, projectName, prev;
    private FirebaseInstanceMessagingService firebaseInstanceMessagingService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                Toast.makeText(CommentActivity.this, inputStr,Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CommentActivity.this, inputStr,Toast.LENGTH_SHORT).show();
                        updateCommentCount();
                        CommentItem myComment = new CommentItem(userName, inputStr, timestamp);
                        commentItemArrayList.add(myComment);
                        createCommentRecyclerView();
                        firebaseInstanceMessagingService.sendMessageToDevice(ownerId, userName + " commented your project " + projectName);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CommentActivity.this, "failed to post comment",Toast.LENGTH_SHORT).show();

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
                // Toast.makeText(CommentActivity.this , "comments count updated",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentActivity.this, "Unable to update comment count", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void createCommentRecyclerView() {

        if(commentItemArrayList.size() > 0) {
            Collections.sort(commentItemArrayList, new Comparator<CommentItem>() {
                @Override
                public int compare(CommentItem o1, CommentItem o2) {
                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                }
            });
        }

        RecyclerView.LayoutManager rLayoutManger = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.comments_recycler_view);
        recyclerView.setHasFixedSize(true);
        CommentAdapter commentAdapter = new CommentAdapter(commentItemArrayList, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(commentAdapter);

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
                                // Toast.makeText(CommentActivity.this, content,Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(CommentActivity.this, "unable to get comments",Toast.LENGTH_SHORT).show();

                        }
                        createCommentRecyclerView();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(prev.equals("home")){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);

        }else if(prev.equals("library")){
            Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
            startActivity(intent);
        }
        this.finish();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
