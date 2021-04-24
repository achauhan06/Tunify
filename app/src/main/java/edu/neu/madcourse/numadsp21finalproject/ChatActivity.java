package edu.neu.madcourse.numadsp21finalproject;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.chat.ChatItem;
import edu.neu.madcourse.numadsp21finalproject.chat.ChatAdapter;
import edu.neu.madcourse.numadsp21finalproject.service.FirebaseInstanceMessagingService;
import edu.neu.madcourse.numadsp21finalproject.users.UserItem;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager rLayoutManger;
    private ImageButton sendButton;
    private String userName;
    private String friendId;
    private String friendName;
    private Snackbar snackbar;
    private EditText typeField;
    private List<ChatItem> chatItems;
    private ChatAdapter chatAdapter;
    ImageView profilePicture;
    FirebaseAuth auth;
    String userId;
    FirebaseFirestore firebaseFirestore;
    BroadcastReceiver myBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();


        typeField = findViewById(R.id.chat_dropdown_menu);
        firebaseFirestore = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        userName = Helper.getUserName(this);
        friendId = getIntent().getExtras().getString("friendId");
        friendName = getIntent().getExtras().getString("friendName");

        TextView friend = findViewById(R.id.friend_name_view);
        friend.setText(friendName);

        chatItems = new ArrayList<>();
        firebaseFirestore.collection("chats");

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = typeField.getText().toString();
                if (!item.isEmpty()) sendMessage(friendId, item);
                else Toast.makeText(ChatActivity.this, "Message cannot be empty",
                        Toast.LENGTH_SHORT).show();
            }
        });
        readMessages();

        profilePicture = findViewById(R.id.userProfilePicture);

    }

    private void sendMessage(String receiver, String message) {
        DocumentReference reference = firebaseFirestore.collection("chats").document();
        HashMap<String, Object> chatMap = new HashMap<>();
        chatMap.put("receiverId", receiver);
        chatMap.put("message", message);
        chatMap.put("senderId", userId);
        chatMap.put("receiverName", friendName);
        chatMap.put("message", message);
        chatMap.put("senderName", userName);
        chatMap.put("timeSent", new Timestamp(new Date()));

        try {
            reference.set(chatMap).addOnSuccessListener(aVoid -> {
                sendNotification();
                Toast.makeText(ChatActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
                typeField.setText("");
            }).addOnFailureListener(e -> {
                snackbar = Snackbar.make(recyclerView, "something went wrong", Snackbar.LENGTH_SHORT);
                snackbar.show();
            });
        } catch (Error error) {
            error.printStackTrace();
        }
    }

    private void sendNotification() {
        String title = "New Message Received";
        String message = "You have received a new message from " + userName;
        FirebaseInstanceMessagingService.getTokenByUserId(friendId, friendName, title ,message,
                "chatType", "", ChatActivity.this);
    }

    private void readMessages() {
        chatItems = new ArrayList<>();
        firebaseFirestore.collection("chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException error) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            chatItems.clear();
                            for (DocumentSnapshot d : list) {
                                String chatId = d.getId();
                                String senderId = d.getString("senderId");
                                String senderName = d.getString("senderName");
                                String receiverId = d.getString("receiverId");
                                String receiverName = d.getString("receiverName");
                                String message= d.getString("message");
                                Timestamp time = d.getTimestamp("timeSent");
                                if (senderId.equals(userId)
                                        && receiverId.equals(friendId) ||
                                        senderId.equals(friendId)
                                                && receiverId.equals(userId)) {
                                    ChatItem chat =
                                            new ChatItem(senderId, senderName,
                                                    receiverId, receiverName,
                                                    message, time);
                                    chat.setId(chatId);
                                    chatItems.add(chat);
                                    setOwnerPicture(friendId, chat);
                                }
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                        createRecyclerView();

                    }
        });
    }

    private void setOwnerPicture(String ownerId, ChatItem item) {
        final String[] picturePath = {Helper.DEFAULT_PICTURE_PATH};
        Helper.db.collection("images")
                .document(ownerId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                picturePath[0] = snapshot.getString("path");
            }
            setProfilePicture(picturePath[0]);

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setProfilePicture(picturePath[0]);
            }
        });
    }

    private void setProfilePicture(String picturePath) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(picturePath);
        final long FIVE_MEGABYTE = 5 * 1024 * 1024;
        ref.getBytes(FIVE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap scaledImage = Bitmap.createScaledBitmap(bmp, 60, 60, true);
            profilePicture.setImageBitmap(scaledImage);
            profilePicture.setVisibility(View.VISIBLE);

        }).addOnFailureListener(exception -> Toast.makeText(ChatActivity.this,
                "Unable to load image as it is more than 5MB in size",Toast.LENGTH_SHORT).show());
    }

    private void createRecyclerView() {
        if (chatItems!= null && !chatItems.isEmpty()) {
            Collections.sort(chatItems, new Comparator<ChatItem>() {
                @Override
                public int compare(ChatItem o1, ChatItem o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }
            });
        }
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        chatAdapter = new ChatAdapter(chatItems, userName);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(chatAdapter);;
        recyclerView.scrollToPosition(chatItems.size() - 1);

    }

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