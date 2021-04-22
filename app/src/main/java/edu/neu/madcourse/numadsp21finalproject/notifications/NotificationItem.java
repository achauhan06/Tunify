package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.Context;

import java.sql.Time;

import android.content.Intent;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.neu.madcourse.numadsp21finalproject.UserProfileActivity;

public class NotificationItem implements NotificationViewListener {

    private String title, body, type, contentId;
    private String status = "";
    private String projectName = "";

    private boolean isFriendRequest = false;
    private Timestamp timestamp;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;

    public NotificationItem(DocumentSnapshot documentSnapshot ,Context context) {
        this.contentId = documentSnapshot.get("contentId").toString();
        this.type = documentSnapshot.get("type").toString();
        String senderName = documentSnapshot.get("senderName").toString();
        this.timestamp = (Timestamp) documentSnapshot.get("time");
        String extraInfo = documentSnapshot.get("extraInfo").toString();

        /*
        this.status = status;
        this.projectName = projectName;
        this.type = type;
        this.title = title;
        this.body = body;

         */
        if(this.type.equals("friendRequest")){
            this.isFriendRequest = true;
            this.title = "Friend Request";
            this.body = senderName + " sent you a friend request.";
            this.status = extraInfo;
        }else if(this.type.equals("like")){
            this.projectName = extraInfo;
            this.title = "New Like";
            this.body = senderName + " liked your project " + projectName;
        }else if(this.type.equals("comment")){
            this.projectName = extraInfo;
            this.title = "New Comment";
            this.body = senderName + " liked your project " + projectName;
        }

        // this.timestamp = timestamp;
        this.context = context;

    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }


    public String getBody() {
        return body;
    }
    public boolean isFriendRequest(){return isFriendRequest;}

    public Timestamp getTimestamp() {
        return timestamp;
    }

    private void getFriendRequestStatus() {
        db.collection("friendRequests").document(contentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    status = task.getResult().get("status").toString();
                    Toast.makeText(context,"status for " + contentId,Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(context,"failed to get friend request status",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getProjectName(){
        db.collection("recordings").document(contentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            projectName = task.getResult().get("name").toString();
                            Toast.makeText(context,"name of " + contentId,Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(context,"failed to get project name",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateFriendRequest(String requestId, String status) {
        FirebaseFirestore.getInstance().collection("friendRequests").document(requestId).update("status",status);
    }


    @Override
    public void onItemClick(int position, Context context) {
    }
}