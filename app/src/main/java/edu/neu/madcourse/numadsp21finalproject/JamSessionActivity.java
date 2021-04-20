package edu.neu.madcourse.numadsp21finalproject;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private ImageButton cancelRecording;
    private ImageButton sendRecording;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jam_session_layout);
        Toolbar toolbar = findViewById(R.id.toolbar_jam_session);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupName = getIntent().getStringExtra("groupName");
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/";
        fileName += groupName + "_" + userName + ".mp3";
        TextView groupTitle = findViewById(R.id.group_title);
        groupTitle.setText(groupName);
        String[] membersAsString = getIntent().getStringExtra("members").split(";");
        friendsMap = new HashMap<>();
        for(String memberArr : membersAsString) {
            String[] member = memberArr.split(":");
            if (member[0].equals(userId)) {
                userName = member[1];
            } else {
                friendsMap.put(member[1],member[0]);
            }
        }

        createRecorder();
        createPlayer();
        cancelAndSend();

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

            }
        });
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
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
