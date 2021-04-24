package edu.neu.madcourse.numadsp21finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import edu.neu.madcourse.numadsp21finalproject.chat.Chat;
import edu.neu.madcourse.numadsp21finalproject.chat.ChatAdapter;
import edu.neu.madcourse.numadsp21finalproject.users.UserAdapter;
import edu.neu.madcourse.numadsp21finalproject.users.UserItem;
import edu.neu.madcourse.numadsp21finalproject.users.UserViewListener;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager rLayoutManger;
    private ImageButton sendButton;
    private String userName;
    private String userToken;
    private String friendId;
    private String friendToken;
    String friendName;
    //private FirebaseDatabaseReference databaseReference;
    private Snackbar snackbar;
    private EditText typeField;
    private List<String> mchat;
    private ChatAdapter adapter;
    FirebaseAuth auth;
    String userId;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        typeField = findViewById(R.id.chat_dropdown_menu);
        firebaseFirestore = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        userName = Helper.getUserName(this);
        userToken = Helper.getUserToken(this);
        friendId = getIntent().getExtras().getString("friendId");
        friendName = getIntent().getExtras().getString("friendName");

        TextView friend = findViewById(R.id.friend_name_view);
        friend.setText(friendName);

        mchat = new ArrayList<>();
        firebaseFirestore.collection("chats");

        sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = typeField.getText().toString();
                if (!item.isEmpty()) sendMessage(friendId, item);
            }
        });
        readMessages();

    }

    private void sendMessage(String receiver, String message) {
        DocumentReference reference = firebaseFirestore.collection("chats").document();
        HashMap<String, Object> chatMap = new HashMap<>();
        chatMap.put("receiver", receiver);
        chatMap.put("message", message);
        chatMap.put("sender", userId);

        try {
            reference.set(chatMap).addOnSuccessListener(aVoid -> {
                Toast.makeText(ChatActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
                typeField.setText("");
            });
        } catch (Error error) {
            snackbar = Snackbar.make(recyclerView, "something went wrong", Snackbar.LENGTH_SHORT);
            snackbar.show();
            error.printStackTrace();
        }
    }

    private void readMessages() {
        mchat = new ArrayList<>();
        firebaseFirestore.collection("chats")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException error) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                String uId = d.getString("sender");
                                String rId = d.getString("receiver");
                                String message= d.getString("message");
                                if (uId.equals(userId) && rId.equals(friendId)) {
                                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
                                    mchat.add(message);
                                }
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                        //createRecyclerView();

                    }
        });
    }

    /*private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        ChatViewListener chatViewListener = new ChatViewListener() {
            @Override
            public void onItemClick(int position, Context context) {
                mchat.get(position).onItemClick(position,context);
            }
        };
        chatAdapter = new ChatAdapter(mchat, chatViewListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(chatAdapter);
        ;

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