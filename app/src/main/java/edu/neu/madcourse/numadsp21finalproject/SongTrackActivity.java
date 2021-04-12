package edu.neu.madcourse.numadsp21finalproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
/*import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;*/

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class SongTrackActivity extends YouTubeBaseActivity {

    private YouTubePlayerView youtubePlayerView;
    private String songUrl;
    private String songName;
    private ImageButton youtubeBackButton;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private ImageButton recordButton = null;
    private MediaRecorder recorder = null;
    private Button save;
    FirebaseFirestore firebaseFirestore;

    private ImageButton playButton = null;
    private MediaPlayer player = null;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static String fileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    YouTubePlayer player1;
    DocumentReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_track_layout);
        Toolbar toolbar = findViewById(R.id.toolbar_song_track_view);
        setActionBar(toolbar);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setTitle("");
        /*setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        songName = getIntent().getStringExtra("songName");
        songUrl = getIntent().getStringExtra("songUrl");
        createYoutubeView();
        youtubeBackButton = findViewById(R.id.youtubeBackButton);
        youtubeBackButton.setOnClickListener(v-> this.finish());
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/";
        fileName += "new_audio.mp3";
        setRecordSection();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setRecordSection() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recordButton = findViewById(R.id.record);
        recordButton.setOnClickListener(v -> {
            onRecord(mStartRecording);
            if (mStartRecording) {
                recordButton.setImageResource(R.drawable.microphone);

            } else {
                recordButton.setImageResource(R.drawable.microphone_block);
            }
            mStartRecording = !mStartRecording;
        });

        playButton = findViewById(R.id.play);
        playButton.setOnClickListener(v -> {
            onPlay(mStartPlaying);
            if (mStartPlaying) {
                playButton.setImageResource(R.drawable.stop);
            } else {
                playButton.setImageResource(R.drawable.play);
            }
            mStartPlaying = !mStartPlaying;
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(v-> {
            uploadAudio();
        });
    }

    private void createYoutubeView() {
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        youtubePlayerView.initialize(Helper.YOUTUBE_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo("xNN372Ud0EE");
                        player1 = youTubePlayer;
                        youTubePlayer.play();
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
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

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        player1.play();
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void uploadAudio() {
        /*mProgressDialog.setMessage("Uploading Audio...");//declare it globally and initialize it with passing the current activity i.e this
        mProgressDialog.show();*/

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        ref = firebaseFirestore.collection("recordings").document("file1");
        Map<String, Object> reg_entry = new HashMap<>();
        reg_entry.put("fileName", "new_audio.mp3");
        ref.set(reg_entry);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference mFilePath = mStorageRef.child("aishwarya").child("songs");

        Uri u = Uri.fromFile(new File(fileName));
        mFilePath.putFile(u,metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SongTrackActivity.this,"Done",Toast.LENGTH_SHORT);

               // mProgressDialog.dismiss();
                //mTextView.setText("Audio has been Uploaded Successfully !!!");
            }
        });
    }



}
