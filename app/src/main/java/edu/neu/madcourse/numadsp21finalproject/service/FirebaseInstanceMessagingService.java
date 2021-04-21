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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
import java.util.Random;
import java.util.Scanner;

import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.RegisterActivity;
import edu.neu.madcourse.numadsp21finalproject.UserService;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;


public class FirebaseInstanceMessagingService extends FirebaseMessagingService {
    private static final String TAG = FirebaseInstanceMessagingService.class.getSimpleName();
    private static String REFRESH_MOBILE_TOKEN;
    private static final String CHANNEL_ID  = "CHANNEL_ID";
    private static final String CHANNEL_NAME  = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION  = "CHANNEL_DESCRIPTION";

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

    private void showNotification2(RemoteMessage remoteMessage) {

        String click_action = remoteMessage.getNotification().getClickAction();
        Intent intent = new Intent(click_action);
        intent.putExtra("click_action",click_action);
        // intent.putExtra("friendName",remoteMessage.getData().get("friendName"));
        // intent.putExtra("friendToken",remoteMessage.getData().get("friendToken"));
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


        notification = builder.setContentTitle(remoteMessage.getNotification().getTitle())
                /*.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))*/
                .setContentText(remoteMessage.getNotification().getBody())
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

    //NOT NEEDED
   /* public static void login(UserService userService, String currentToken) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String refreshToken = task.getResult();
                    if (currentToken == null || currentToken.isEmpty() || !currentToken.equals(refreshToken)) {
                        userService.updateToken(refreshToken);
                    }
                });

    }*/
    public static void sendMessageToDevice(String id, String body) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                getTokenByUserId(id, body);
                // sendMessageToDeviceService(id, body);
            }
        }).start();
    }

    private static void getTokenByUserId(String id, String body) {

        FirebaseFirestore.getInstance().collection("users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                // String targetToken = value.getString("MobileToken");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendMessageToDeviceService(value.getString("MobileToken"), body);
                    }
                }).start();
                // sendMessageToDeviceService(targetToken, body);
            }
        });

    }

    private static void sendMessageToDeviceService(String targetToken, String body) {
        // String userToken = "eEmJrwCZTIS3bmQd2feBqs:APA91bE-yFSrDo6YZygzcWIYarzZhj0NQWdkivrvDPDwLUALuUUIBscXcF_RsEguC7UXrlsBfwgE1KZH5gUnVdRUFg1kh8yPDFkSvJRTNG0IV1dlIw8mZNt0lh25JQ2FwMnLccJ-0afW";

        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "New Message");
            jNotification.put("body", body);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            //citation : https://stackoverflow.com/a/43801355
            jNotification.put("click_action","libraryNotification");

            // jdata.put("friendName",userName);
            // jdata.put("friendToken",userToken);

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

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("TAG", "run: " + resp);
                    //Toast.makeText(ChatActivity.this,resp,Toast.LENGTH_LONG).show();
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



}