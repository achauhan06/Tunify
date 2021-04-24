package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.Context;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.neu.madcourse.numadsp21finalproject.UserProfileActivity;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendProfile;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsActivity;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryActivity;
import edu.neu.madcourse.numadsp21finalproject.commentview.CommentActivity;
import edu.neu.madcourse.numadsp21finalproject.service.FirebaseInstanceMessagingService;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class NotificationItem implements NotificationViewListener {

    private String title, body, type, contentId;
    private String status = "";
    private String projectName = "";

    private boolean isFriendRequest = false;
    private Timestamp timestamp;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentSnapshot documentSnapshot;
    private Context context;

    public NotificationItem(DocumentSnapshot documentSnapshot, Context context) {
        this.contentId = documentSnapshot.get("contentId").toString();
        this.type = documentSnapshot.get("type").toString();
        String senderName = documentSnapshot.get("senderName").toString();
        this.timestamp = (Timestamp) documentSnapshot.get("time");
        String extraInfo = documentSnapshot.get("extraInfo").toString();

        if(this.type.equals("friendRequest")){
            this.isFriendRequest = true;
            this.title = "Friend Request";
            this.body = senderName + " sent you a friend request.";
            this.status = extraInfo;
        }else if(this.type.equals("like")){
            this.projectName = extraInfo;
            this.title = "New Like";
            this.body = senderName + " liked your project " + projectName + ".";
        }else if(this.type.equals("comment")){
            this.projectName = extraInfo;
            this.title = "New Comment";
            this.body = senderName + " commented on your project " + projectName + ".";
        } else if(this.type.equals("acceptedRequest") || this.type.equals("declinedRequest")) {
            String actionDone = this.type.equals("acceptedRequest") ? "accepted" : "declined";
            this.isFriendRequest = true;
            this.title = "Friend Request";
            this.body = senderName + " " + actionDone + " your friend request.";
            this.status = extraInfo;
        }

        this.documentSnapshot = documentSnapshot;
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


    private void updateFriendRequest(String requestId, String status) {
        FirebaseFirestore.getInstance().collection("friendRequests").document(requestId).update("status",status);
    }
    private void updateNotificationInfo(String notificationId, String info) {
        FirebaseFirestore.getInstance().collection("notifications").document(notificationId).update("extraInfo",info);
    }

    private void addFriend(String myId, String myName, String friendId, String friendName){
        Map<String, Object> friendship = new HashMap<>();
        ArrayList<String> friends = new ArrayList<>();
        friends.add(friendId);
        friends.add(myId);
        friendship.put("friends", friends);
        friendship.put("name1", friendName);
        friendship.put("name2", myName);
        db.collection("friendships").add(friendship).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        db.collection("friends").document(myId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getReference().update("friendsId", FieldValue.arrayUnion(friendId));
                } else {
                    Map entry = new HashMap();
                    entry.put("friendsId", new ArrayList(){{add(friendId);}});
                    snapshot.getReference().set(entry);
                }

            }
        });

        db.collection("friends").document(friendId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()) {
                    snapshot.getReference().update("friendsId", FieldValue.arrayUnion(myId));
                } else {
                    Map entry = new HashMap();
                    entry.put("friendsId", new ArrayList(){{add(myId);}});
                    snapshot.getReference().set(entry);
                }
            }
        });

        String msg = myName + " accepted your friend request.";
        FirebaseInstanceMessagingService.sendMessageToDevice(friendId,
                friendName, title,  msg, contentId, "acceptedRequest", context);

    }

    @Override
    public void onLibraryClick(int position) {
        Intent intent = new Intent(context, LibraryActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void onAcceptClick(int position) {
        String friendId = documentSnapshot.get("senderId").toString();
        String friendName = documentSnapshot.get("senderName").toString();
        String myId = documentSnapshot.get("receiverId").toString();
        String myName = documentSnapshot.get("receiverName").toString();
        this.status = "accepted";
        updateFriendRequest(contentId, status);
        updateNotificationInfo(documentSnapshot.getReference().getId(),status);
        addFriend(myId,myName,friendId,friendName);
    }

    @Override
    public void onDeclineClick(int position) {
        this.status = "declined";
        updateFriendRequest(contentId, status);
        updateNotificationInfo(documentSnapshot.getReference().getId(),status);
        String friendId = documentSnapshot.get("senderId").toString();
        String friendName = documentSnapshot.get("senderName").toString();
        String myName = documentSnapshot.get("receiverName").toString();
        String msg = myName + " declined your friend request.";
        FirebaseInstanceMessagingService.sendMessageToDevice(friendId,
                friendName, title,  msg, contentId, "declinedRequest", context);


    }

    @Override
    public void onProfileClick(int position) {
        String friendId = documentSnapshot.get("senderId").toString();
        String friendName = documentSnapshot.get("senderName").toString();
        String myId = documentSnapshot.get("receiverId").toString();
        if (this.status.equals("acceptedRequest")) {
            Intent intent = new Intent(context, FriendProfile.class);
            intent.putExtra("userId", myId);
            intent.putExtra("friendId", friendId);
            intent.putExtra("friendName", friendName);
            context.startActivity(intent);
        } else if(this.status.equals("declinedRequest")) {
            Helper.db.collection("users")
                    .document(friendId).get().addOnSuccessListener(snapshot -> {
                        String email = snapshot.getString("Email");
                        Intent intent = new Intent(context, UserProfileActivity.class);
                        intent.putExtra("email", email);
                        context.startActivity(intent);
                    });

        }
    }
}