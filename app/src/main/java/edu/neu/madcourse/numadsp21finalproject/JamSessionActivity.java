package edu.neu.madcourse.numadsp21finalproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.neu.madcourse.numadsp21finalproject.jamsession.JamSessionAdapter;
import edu.neu.madcourse.numadsp21finalproject.jamsession.JamSessionItem;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class JamSessionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest" ;
    private String userId;
    private String userName;
    private String groupName;
    private Map<String, String> friendsMap;
    private ImageButton recordButton = null;
    private MediaRecorder recorder = null;
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaPlayer player = null;
    private ImageView playButton;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static String fileName = null;
    private static String songName = null;
    private ImageButton cancelRecording;
    private ImageButton sendRecording;
    private Long version;
    private List<JamSessionItem> jamSessionItemList;
    private Set<JamSessionItem> tempJamSet;
    private LinearLayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private JamSessionAdapter jamSessionAdapter;
    private BroadcastReceiver myBroadcastReceiver = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jam_session_layout);
        Toolbar toolbar = findViewById(R.id.toolbar_jam_session);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupName = getIntent().getStringExtra("groupName");
        version = getIntent().getLongExtra("songVersion",0);
        TextView groupTitle = findViewById(R.id.group_title);
        groupTitle.setText(groupName);
        String[] membersAsString = getIntent().getStringExtra("members").split(";");
        friendsMap = new HashMap<>();
        for(String memberArr : membersAsString) {
            String[] member = memberArr.split(":");
            if (member[0].equals(userId)) {
                userName = member[1];
            }
            friendsMap.put(member[0],member[1]);
        }
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/";
        songName = groupName + "_" + userId + "_" + version + ".mp3";
        fileName += songName;
        createRecorder();
        createPlayer();
        cancelAndSend();
        new Thread(new Runnable() {
            @Override
            public void run() {
                createJamSessionChatView();
            }
        }).start();


    }

    private void createJamSessionChatView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.jam_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(rLayoutManger);
        tempJamSet = new HashSet<>();
        Helper.db.collection("jamGroups")
                .document(userId)
                .collection("groups")
                .document(groupName)
                .collection("recordings")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {

                        if (value!= null && !value.isEmpty()) {
                            List<DocumentSnapshot> documents = value.getDocuments();

                            for (DocumentSnapshot documentSnapshot : documents) {
                                String currentUserId = documentSnapshot.getId();
                                if (currentUserId.equals(userId)) {
                                    userName = friendsMap.get(currentUserId);
                                }
                                String currentUserName = friendsMap.get(currentUserId);;
                                documentSnapshot.getReference()
                                        .collection("songList")
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                                                final List<DocumentSnapshot> songList = queryDocumentSnapshots.getDocuments();
                                                for (DocumentSnapshot songSnapShot : songList) {
                                                    String songName = songSnapShot.getId();
                                                    Timestamp time = (Timestamp) songSnapShot.get("time");
                                                    String path = songSnapShot.get("path").toString();
                                                    JamSessionItem jamSessionItem = new JamSessionItem(
                                                            currentUserName,
                                                            currentUserId,
                                                            songName, time, path,
                                                            JamSessionActivity.this);
                                                    tempJamSet.add(jamSessionItem);
                                                }
                                               showDataInJamChat();
                                            }
                                        });
                            }
                            System.out.println("hello");
                        }

                    }
                });
    }

    private void showDataInJamChat() {
        jamSessionItemList = Arrays.asList(tempJamSet.toArray(new JamSessionItem[]{}));
        Collections.sort(jamSessionItemList, (o1, o2) -> o1.getTimeUpdated().compareTo(o2.getTimeUpdated()));

        jamSessionAdapter = new JamSessionAdapter(jamSessionItemList, userName);
        jamSessionAdapter.setListener(pos -> jamSessionItemList.get(pos).onButtonClick(pos));
        recyclerView.setAdapter(jamSessionAdapter);
        recyclerView.scrollToPosition(jamSessionItemList.size()-1);

    }

    private void cancelAndSend() {
        cancelRecording = findViewById(R.id.cancel_recording);
        cancelRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecorder();
            }
        });
        sendRecording = findViewById(R.id.send_recording);
        sendRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadAudio();
                    }
                }).start();


            }
        });
    }

    private void uploadAudio() {

        WriteBatch documentBatch = Helper.db.batch();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();
        Map song_entry = new HashMap<>();
        song_entry.put("fileName", songName);
        song_entry.put("time", new Timestamp(new Date()));

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference mFilePath = mStorageRef.child("audios")
                .child(userId)
                .child(songName);
        Uri uri = Uri.fromFile(new File(fileName));
        song_entry.put("path", mFilePath.toString());
        mFilePath.putFile(uri,metadata).addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            System.out.println("Upload is " + progress + "% done");
        }).addOnSuccessListener(taskSnapshot -> {
                    mFilePath.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        song_entry.put("link", uri1.toString());
                        song_entry.put("username", userName);

                        for (String user : friendsMap.keySet()) {
                            DocumentReference userDocumentReference = Helper.db
                                    .collection("jamGroups")
                                    .document(user)
                                    .collection("groups")
                                    .document(groupName);


                            documentBatch.update(userDocumentReference ,new HashMap(){{
                                        put("songVersion", version+1);
                            }});
                            Map<String, Object> emptyMap = new HashMap<>();
                            emptyMap.put("lastUpdated", new Timestamp(new Date()));
                            documentBatch.set(userDocumentReference
                                            .collection("recordings")
                                            .document(userId), emptyMap);
                            documentBatch.set(userDocumentReference
                                    .collection("recordings")
                                    .document(userId)
                                    .collection("songList")
                                    .document(songName),song_entry);
                        }

                        documentBatch.commit().addOnSuccessListener(aVoid -> {
                            version = version+1;
                            songName = groupName + "_" + userId + "_" + version + ".mp3";
                            sendPublishedItemNotification();
                            Snackbar.make(findViewById(android.R.id.content)
                                    , "Audio posted successfully",
                                    Snackbar.LENGTH_SHORT).show();
                            showRecorder();


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(findViewById(android.R.id.content)
                                        , "Some error occurred. Audio cannot be posted."
                                        , Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    });

                });



    }

    private void sendPublishedItemNotification() {
    }

    private void createRecorder() {

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        recordButton = findViewById(R.id.record_jam_btn);
        recordButton.setOnClickListener(v -> {
            onRecord(mStartRecording);
        });

        loadRecorder();
    }

    private void loadRecorder() {
        recorder = new MediaRecorder();
        recorder.setOutputFile(fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void createPlayer() {
        playButton = findViewById(R.id.player_jam);
        playButton.setOnClickListener(v -> {
            onPlay(mStartPlaying);
            mStartPlaying = !mStartPlaying;
        });
    }

    private void showRecorder() {
        ViewGroup linearLayout_recorder = findViewById(R.id.linearLayout_recorder);
        linearLayout_recorder.setVisibility(View.VISIBLE);
        ViewGroup linearLayout_player = findViewById(R.id.linearLayout_player);
        linearLayout_player.setVisibility(View.INVISIBLE);
        loadRecorder();
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
            mStartRecording = false;
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        recordButton.setImageResource(R.drawable.stop_30);
        recorder.start();
        isRecording = true;
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;
        recordButton.setImageResource(R.drawable.jam_mic);
        mStartRecording = true;
        showPlayer();
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void showPlayer() {
        ViewGroup linearLayout_player = findViewById(R.id.linearLayout_player);
        linearLayout_player.setVisibility(View.VISIBLE);
        ViewGroup linearLayout_recorder = findViewById(R.id.linearLayout_recorder);
        linearLayout_recorder.setVisibility(View.INVISIBLE);

    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    @Override
    public void onBackPressed() {
        if (isRecording) {
            pauseRecording();
            alertButton("Recording Paused",
                    "Are you sure you want to quit?", true);
        } else {
            this.finish();
        }
    }

    private void alertButton(String title, String message, boolean isBackButton) {
        AlertDialog.Builder songCloseAlert
                = new AlertDialog
                .Builder(JamSessionActivity.this);
        songCloseAlert.setMessage(message);
        songCloseAlert.setTitle(title);
        songCloseAlert.setCancelable(false);
        songCloseAlert.setPositiveButton("Yes", (dialog, which) -> {
            stopRecording();
            if (isBackButton) {
                this.finish();
            }
        });

        songCloseAlert.setNegativeButton("No", (dialog, which) -> {
            isRecording = true;
            resumeRecording();
            dialog.cancel();
        });
        AlertDialog alertDialog = songCloseAlert.create();
        alertDialog.show();

    }

    private void pauseRecording() {
        if (player!= null && player.isPlaying()) {
            player.pause();
        }
        recorder.pause();
        isRecording = false;
    }

    private void resumeRecording() {
        recorder.resume();
        isRecording = true;
        mStartRecording = false;
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
