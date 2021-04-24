package edu.neu.madcourse.numadsp21finalproject.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.net.URLEncoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.RegisterActivity;
import edu.neu.madcourse.numadsp21finalproject.UserService;
import edu.neu.madcourse.numadsp21finalproject.commentview.CommentActivity;
import edu.neu.madcourse.numadsp21finalproject.commentview.CommentItem;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;


public class FirebaseInstanceMessagingService extends FirebaseMessagingService {
    private static final String TAG = FirebaseInstanceMessagingService.class.getSimpleName();
    private static String REFRESH_MOBILE_TOKEN;
    private static final String CHANNEL_ID  = "CHANNEL_ID";
    private static final String CHANNEL_NAME  = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION  = "CHANNEL_DESCRIPTION";

    public static void setMobilTokenConstant(String token) {
        REFRESH_MOBILE_TOKEN = token;
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        REFRESH_MOBILE_TOKEN = token;
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification() != null) {
            showNotification2(remoteMessage);
        }
//        extractPayloadDataForegroundCase(remoteMessage);
    }

    /*
    private void showNotification(RemoteMessage remoteMessage) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        //.setSmallIcon(R.drawable.abc)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

    }

     */

    private void showNotification2(RemoteMessage remoteMessage) {

        String click_action = remoteMessage.getNotification().getClickAction();
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Intent intent = new Intent(click_action);
        intent.putExtra("click_action",click_action);

        intent.putExtra("title",title);
        intent.putExtra("body",body);
        intent.putExtra("friendName",remoteMessage.getData().get("friendName"));
        intent.putExtra("friendToken",remoteMessage.getData().get("friendToken"));
        intent.putExtra("friendId",remoteMessage.getData().get("friendId"));
        intent.putExtra("time",remoteMessage.getData().get("time"));

        //intent.putExtra("friendName",remoteMessage.getData().get("friendName"));
        //intent.putExtra("friendToken",remoteMessage.getData().get("friendToken"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this,CHANNEL_ID);

        }
        else {
            builder = new NotificationCompat.Builder(this);
        }
        int notificationId = new Random().nextInt();


        notification = builder.setContentTitle(title)
                /*.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))*/
                .setContentText(body)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(notificationId,notification);

    }


    public static String getMobileRefreshToken() {
        return REFRESH_MOBILE_TOKEN;
    }



    private static void sendRegistrationToServer(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Helper.db.collection("users")
                    .document(userId)
                    .update("MobileToken", token);
        }

    }

    public static void register(UserService userService) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    userService.register(task.getResult());
                });
    }

    public static void sendMessageToDevice(String receiverId,String receiverName, String title,
                                           String body,String contentId,String extraInfo, Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                getTokenByUserId(receiverId,receiverName,title, body, contentId,extraInfo,context);
            }
        }).start();
    }

    public static void getTokenByUserId(String receiverId, String receiverName,
                                        String title, String body, String contentId,
                                        String extraInfo, Context context) {

        FirebaseFirestore.getInstance().collection("users").document(receiverId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {

                if (snapshot.exists()) {
                    String token = snapshot.getString("MobileToken");
                    System.out.println("iuyjgtfyghjiokljiythfrghjkl.jhgfc");
                    System.out.println(token);

                    if(token == null) {
                        Handler h = new Handler(Looper.getMainLooper());
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "receiver token not found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendMessageToDeviceService(token, receiverId,receiverName,title,body,
                                        contentId, extraInfo,context);
                            }
                        }).start();
                    }
                }

            }
        });



    }

    private static void sendMessageToDeviceService(String targetToken,
                                                   String receiverId,String receiverName,
                                                   String title, String body, String contentId,
                                                   String extraInfo,Context context) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {

            jNotification.put("title", title);
            jNotification.put("body", body);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            //citation : https://stackoverflow.com/a/43801355
            if (contentId.equals("chatType")) {
                jNotification.put("click_action","openChat");
            } else {
                jNotification.put("click_action","openNotification");
            }

            String senderName = Helper.getUsername(context);
            // String senderToken = Helper.getUserToken(context);
            String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Timestamp timestamp = new Timestamp(new Date());
            // String time = timestamp.toDate().toString();

            jdata.put("senderName",senderName);
            // jdata.put("senderToken",senderToken);
            jdata.put("friendId",senderId);
            jdata.put("time",timestamp);


            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);


            /***
             * The Payload object is now populated.
             * Send it to Firebase to send the message to the appropriate recipient.
             */
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", Helper.SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            if(title.equals("Friend Request")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (extraInfo.equals("acceptedRequest")) {
                            addNotification("acceptedRequest",senderName, senderId,receiverName,receiverId,contentId,timestamp,extraInfo,context);
                        } else if (extraInfo.equals("declinedRequest")) {
                            addNotification("declinedRequest",senderName, senderId,receiverName,receiverId,contentId,timestamp,extraInfo,context);
                        } else {
                            addFriendRequest(senderName, senderId,receiverName,receiverId,timestamp,context);
                        }

                    }
                }).start();
            }else if(title.equals("New Like")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addNotification("like",senderName, senderId,receiverName,receiverId,contentId,timestamp,extraInfo,context);

                    }
                }).start();
            } else if(title.equals("New Comment")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addNotification("comment",senderName, senderId,receiverName,
                                receiverId,contentId,timestamp,extraInfo,context);

                    }
                }).start();
            }

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("TAG", "run: " + resp);
                }
            });



        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    private static void addFriendRequest(String senderName, String senderId, String receiverName,
                                         String receiverId, Timestamp timestamp, Context context) {
        Map<String, Object> friendRequest = new HashMap<>();
        friendRequest.put("senderName", senderName);
        friendRequest.put("senderId", senderId);
        friendRequest.put("receiverName", receiverName);
        friendRequest.put("receiverId", receiverId);
        friendRequest.put("time", timestamp);
        friendRequest.put("status", "pending");


        FirebaseFirestore.getInstance().collection("friendRequests")
                .add(friendRequest).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(context, "friend request sent",Toast.LENGTH_SHORT).show();
                String contentId = documentReference.getId();
                addNotification("friendRequest",senderName,senderId,receiverName, receiverId,
                        contentId,timestamp, "pending",context);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "failed to send friend request",Toast.LENGTH_SHORT).show();

            }
        });
    }

        private static void addNotification(String type,String senderName, String senderId, String receiverName,
                String receiverId, String contentId,Timestamp timestamp,String extraInfo, Context context) {
            Map<String, Object> friendRequest = new HashMap<>();
            friendRequest.put("senderName", senderName);
            friendRequest.put("senderId", senderId);
            friendRequest.put("receiverName", receiverName);
            friendRequest.put("receiverId", receiverId);
            friendRequest.put("contentId", contentId);
            friendRequest.put("time", timestamp);
            friendRequest.put("type", type);
            friendRequest.put("extraInfo", extraInfo);



            FirebaseFirestore.getInstance().collection("notifications")
                    .add(friendRequest).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(context, "notification added",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "failed to add notification",Toast.LENGTH_SHORT).show();

                }
            });

    }

}