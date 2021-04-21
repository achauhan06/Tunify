package edu.neu.madcourse.numadsp21finalproject.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.RegisterActivity;
import edu.neu.madcourse.numadsp21finalproject.UserService;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;


public class FirebaseInstanceMessagingService extends FirebaseMessagingService {
    private static final String TAG = FirebaseInstanceMessagingService.class.getSimpleName();
    private static String REFRESH_MOBILE_TOKEN;

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
            showNotification(remoteMessage);
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

}