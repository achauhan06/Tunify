package edu.neu.madcourse.numadsp21finalproject.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.RegisterActivity;
import edu.neu.madcourse.numadsp21finalproject.UserService;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;


public class FirebaseInstanceMessagingService extends FirebaseMessagingService {
    private static final String TAG = FirebaseInstanceMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private static void sendRegistrationToServer(String token) {
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

    public static void login(UserService userService, String currentToken) {
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

    }

}