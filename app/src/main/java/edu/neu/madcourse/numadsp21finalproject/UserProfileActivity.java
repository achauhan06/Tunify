package edu.neu.madcourse.numadsp21finalproject;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class UserProfileActivity extends AppCompatActivity {

    TextView dob, genre, fullName;
    private String email, friendId;
    Button addFriend;
    private String userToken = "";
    private FirebaseFirestore fireStore;
    private FirebaseInstanceMessagingService firebaseInstanceMessagingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        fireStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar_user_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addFriend = findViewById(R.id.addfriend_btn);
        //first_name = findViewById(R.id.user_profile_first_name);
        //last_name = findViewById(R.id.user_profile_last_name);
        dob = findViewById(R.id.user_profile_dob);
        genre = findViewById(R.id.user_profile_genre);
        email = getIntent().getExtras().getString("email");
        fullName = findViewById(R.id.user_profile_heading);
        fireStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        String email1 = d.getString("Email");
                        friendId = d.getId();
                        if (email.equals(email1)) {
                            fullName.setText(d.getString("First Name")+ " " +d.getString("Last Name"));

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
            }
            });
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend.setEnabled(false);
                addFriend.setText("Friend Request Sent");
                firebaseInstanceMessagingService.sendMessageToDevice(friendId, Helper.getUsername(UserProfileActivity.this) + " sent you a friend request.");
                // sendMessageToDevice("New Friend Request!");
            }
        });
    }

    public void sendMessageToDevice(String message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice(userToken, message);
            }
        }).start();
    }

    private void sendMessageToDevice(String userToken, String message) {
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

            //jdata.put("friendName",userName);
            //jdata.put("friendToken",yourToken);

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            jPayload.put("to", userToken); // CLIENT_REGISTRATION_TOKEN);
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
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

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
}
