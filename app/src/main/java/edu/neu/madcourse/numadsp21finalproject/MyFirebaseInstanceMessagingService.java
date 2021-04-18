package edu.neu.madcourse.numadsp21finalproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.users.UserItem;

public class MyFirebaseInstanceMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseInstanceMessagingService.class.getSimpleName();
    String userId;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    UserItem user = new UserItem();



    public void sendRegistrationToServer(Map<String, Object> reg_entry) {
        userId = auth.getCurrentUser().getUid();
        DocumentReference reference1 = firebaseFirestore.collection("users").document(userId);
        //Map<String, Object> reg_entry = new HashMap<>();
        /*reg_entry.put("First Name", firstName.getText().toString());
        reg_entry.put("Last Name", lastName.getText().toString());
        reg_entry.put("Email", email.getText().toString());
        reg_entry.put("Genres", String.join(";",selectedGenres));
        reg_entry.put("Password", password.getText().toString());
        reg_entry.put("Date of Birth", dob.getText().toString());*/
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task1.getResult();
                    user.setToken(token);
                    Toast.makeText(MyFirebaseInstanceMessagingService.this, token, Toast.LENGTH_SHORT).show();

                    //sendRegistrationToServer(token,context);
                    //reg_entry.put("Mobile Token", token);

                    //openNewActivity("User created successfully", view, context);
                });
        //Object obj = token;
        //Toast.makeText(RegisterActivity.this, "Token : " +user.getToken(), Toast.LENGTH_SHORT).show();
        //firebaseInstanceMessagingService.sendRegistrationToServer(reg_entry);
        reg_entry.put("Mobile Token", user.getToken());
        reference1.set(reg_entry).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MyFirebaseInstanceMessagingService.this, "User Successfully registered!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyFirebaseInstanceMessagingService.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        // sendRegistrationToServer(token);
    }
}
