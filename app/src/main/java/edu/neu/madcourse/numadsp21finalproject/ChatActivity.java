package edu.neu.madcourse.numadsp21finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import edu.neu.madcourse.numadsp21finalproject.chat.Chat;
import edu.neu.madcourse.numadsp21finalproject.chat.ChatAdapter;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton sendButton;
    private String userName;
    private String userToken;
    private String friendName;
    private String friendToken;
    //private FirebaseDatabaseReference databaseReference;
    private Snackbar snackbar;
    private Spinner dropdown;
    private ArrayList<Chat> mchat;
    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        /*getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dropdown = findViewById(R.id.chat_dropdown_menu);
        int unicode = 0x1F60A;
        int unicode1 = 0x1F600;
        int unicode2 = 0x1F642;
        String[] items = new String[]{new String(Character.toChars(unicode2))
                ,new String(Character.toChars(unicode1))
                , new String(Character.toChars(unicode))};
        final ArrayAdapter<String>[] adapter1 = new ArrayAdapter[]{
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, items)};
        dropdown.setAdapter(adapter1[0]);

        // set up recycler view
        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (!Helper.getLoggedIn(this)) {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        userName = Helper.getUserName(this);
        userToken = Helper.getUserToken(this);
        friendName = getIntent().getExtras().getString("friendName");
        friendToken = getIntent().getExtras().getString("friendToken");

        TextView friend = findViewById(R.id.friend_name_view);
        friend.setText(friendName);

        mchat = new ArrayList<Chat>();
        DatabaseReference nm= FirebaseFirestore.getInstance().getCollection("chats/"+userName);

        nm.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Object object = snapshot.getValue();
                    String json = new Gson().toJson(object);
                    Chat chats = new Gson().fromJson(json, Chat.class);

                    if (chats.getSender().equals(userName) && chats.getReceiver().equals(friendName) ||
                            chats.getReceiver().equals(userName) && chats.getSender().equals(friendName)) {
                        mchat.add(chats);
                    }
                    adapter = new ChatAdapter(mchat, userName);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference = new FirebaseDatabaseReference();
        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = dropdown.getSelectedItem().toString();
                sendSticker(userName, friendName, item);
            }
        });

    }


    // TODO: sendMessageToDevice is not tested.
    private void sendMessage(String sender, String receiver, String message) {

        HashMap<String, Object> stickerMap = new HashMap<>();
        stickerMap.put("receiver", receiver);
        stickerMap.put("message", message);
        stickerMap.put("sender", sender);

        try{
            databaseReference.getDatabase()
                    .child("stickers")
                    .child(sender)
                    .push()
                    .setValue(stickerMap);

            databaseReference.getDatabase()
                    .child("stickers")
                    .child(receiver)
                    .push()
                    .setValue(stickerMap);

            if(!Helper.isOnline(ChatActivity.this)) {
                Toast.makeText(ChatActivity.this, Helper.NO_INTERNET,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            this.sendMessageToDevice(recyclerView, friendToken,sticker);



        }catch (Error error) {
            snackbar = Snackbar.make(recyclerView, "something went wrong", Snackbar.LENGTH_SHORT);
            snackbar.show();
            error.printStackTrace();
        }


    }

    /*private void readMessages(final String myid, final String friendid){
        mchat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("stickers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserItem items = snapshot.getValue(UserItem.class);
                    if (items.getUserName().equals(myid) && items.getFriendName().equals(friendid) ||
                            items.getFriendName().equals(friendid) && items.getUserName().equals(myid)){
                        mchat.add(items);
                    }
                    adapter = new UserViewAdapter(mchat);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }*/

    /*public void sendMessageToDevice(View type, String token, String sticker) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessageToDevice(token,sticker);
            }
        }).start();
    }

    private void sendMessageToDevice(String targetToken, String sticker) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "New Message");
            jNotification.put("body", "You received a sticker from "
                    + userName + " " + sticker);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            //citation : https://stackoverflow.com/a/43801355
            jNotification.put("click_action","chatNotification");

            jdata.put("friendName",userName);
            jdata.put("friendToken",userToken);

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            /*jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);


            /***
             * The Payload object is now populated.
             * Send it to Firebase to send the message to the appropriate recipient.
             */
            /*URL url = new URL("https://fcm.googleapis.com/fcm/send");
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

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }*/


    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }*/
}}
