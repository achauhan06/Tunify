package edu.neu.madcourse.numadsp21finalproject;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendItem;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsActivity;
import edu.neu.madcourse.numadsp21finalproject.service.FirebaseInstanceMessagingService;
import edu.neu.madcourse.numadsp21finalproject.users.UserItem;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class UserProfileActivity extends AppCompatActivity {

    TextView dob, genre, fullName, userNameView, emailView ;
    private String email, friendId, friendName;
    private String userId;
    Button addFriend;
    private FirebaseFirestore fireStore;
    private BroadcastReceiver myBroadcastReceiver = null;
    private boolean acceptRequestFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        fireStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar_user_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(UserProfileActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        userId = FirebaseAuth.getInstance().getUid();

        addFriend = findViewById(R.id.addfriend_btn);
        //first_name = findViewById(R.id.user_profile_first_name);
        //last_name = findViewById(R.id.user_profile_last_name);
        dob = findViewById(R.id.user_profile_dob);
        genre = findViewById(R.id.user_profile_genre);
        userNameView = findViewById(R.id.user_profile_username);
        emailView = findViewById(R.id.user_profile_email);
        email = getIntent().getExtras().getString("email");
        fullName = findViewById(R.id.user_profile_heading);
        createUserProfileView();
        fireStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        String email1 = d.getString("Email");
                        friendId = d.getId();
                        if (email.equals(email1)) {
                            friendName = d.getString("Username") != null ?
                                    d.getString("Username") :
                                    d.getString("First Name")+ " " +d.getString("Last Name");

                            String name =  d.getString("First Name")+ " " +d.getString("Last Name");
                            fullName.setText(name);
                            userNameView.setText(friendName);
                            emailView.setText(email1);

                            //first_name.setText(d.getString("First Name"));
                            //last_name.setText(d.getString("Last Name"));
                            dob.setText(d.getString("Date of Birth"));
                            //genre.setText(d.getString("Genres"));
                            // userToken = d.getString("MobileToken");
                            // Toast.makeText(UserProfileActivity.this, "Hi " +d.getString("MobileToken"), Toast.LENGTH_SHORT).show();
                            String[] arr = d.getString("Genres") == null ? new String[]{} : d.getString("Genres").split(";");
                            List<String> list1 = Lists.newArrayList(arr);
                            genre.setText(list1.toString().replace("[", "").replace("]", ""));
                            break;
                        }
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                }
                createUserProfileView();
            }
            });
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (acceptRequestFlag) {

                } else {
                    addFriend.setEnabled(false);
                    addFriend.setText("Friend Request Sent");
                    FirebaseInstanceMessagingService.sendMessageToDevice(friendId, friendName,"Friend Request",
                            Helper.getUsername(UserProfileActivity.this) + " sent you a friend request.",
                            "","pending",UserProfileActivity.this);

                }
            }
        });
    }

    private void createUserProfileView() {
        Helper.db.collection("friendRequests")
                .whereEqualTo("senderId", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documents = value.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documents) {
                            boolean isPending = documentSnapshot.getString("status")
                                    .equals("pending");
                            if (documentSnapshot.getString("receiverId").equals(friendId)
                            && isPending) {
                                addFriend.setText("Friend Request Sent");
                                addFriend.setEnabled(false);
                                break;
                            };
                        }

                    }
                });

        Helper.db.collection("friendRequests")
                .whereEqualTo("receiverId", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> documents = value.getDocuments();
                        for (DocumentSnapshot documentSnapshot : documents) {
                            boolean isPending = documentSnapshot.getString("status")
                                    .equals("pending");
                            if (documentSnapshot.getString("senderId").equals(friendId)
                            && isPending) {
                                addFriend.setText("Accept Friend Request");
                                addFriend.setEnabled(true);
                                acceptRequestFlag = true;
                                break;
                            };
                        }

                    }
                });

    }

/*    public void sendMessageToDevice(String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice(userToken, message);
            }
        }).start();
    }*/

    /*private void sendMessageToDevice(String userToken, String message) {
        String userName = "santosh shenoy";
        String yourToken = "dfDcGn0gT2yrKMGczlpidf:APA91bHT4spTXm33MgQ6ufTt_fiEY-Dy8Q4xM9dqE2OGdIhnJ7NLzcUpkxxNlAZgQvzKnPqrTR2LTC-vmhRDAhRWBpjUmPFq6wXBimVfoz3CZMadVOYdcJCmhTs_BG2RtNMIUOR-AA-i";
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "New Message");
            jNotification.put("body", "You received a friend request");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            //citation : https://stackoverflow.com/a/43801355
            jNotification.put("click_action","chatNotification");

            jdata.put("friendName",userName);
            jdata.put("friendToken",yourToken);

            *//***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             *//*

            jPayload.put("to", userToken); // CLIENT_REGISTRATION_TOKEN);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);


            *//***
             * The Payload object is now populated.
             * Send it to Firebase to send the message to the appropriate recipient.
             *//*
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key=AAAAf-Efg5c:APA91bH5jUEnPT04fw-qdgAMl5ghx_ZAgNL6x4cMKoxz9MRoMEXYS2g4UHOEEuDs2Eb2ysEFfgtp48D8oxMpCiUt7ir6ezA09tv0FjvyH1mQ6jUtLkO_4_xadmzKlgQXfmNVmXgGoarB");
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
    }*/

  /*  private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }*/

    /*public void sendMessageToDevice(String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice1(message);
            }
        }).start();
    }

    private void sendMessageToDevice1(String message) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "New Message");
            jNotification.put("body", "You received a new Friend requesr from "
                    + userName + " " + sticker);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            //citation : https://stackoverflow.com/a/43801355
            jNotification.put("click_action","chatNotification");

            jdata.put("friendName",userName);
            jdata.put("friendToken",userToken);


            jPayload.put("to", userToken); // CLIENT_REGISTRATION_TOKEN);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);


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
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
