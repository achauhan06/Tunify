package edu.neu.madcourse.numadsp21finalproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
/*import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;*/

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class SongTrackActivity extends YouTubeBaseActivity {

    private YouTubePlayerView youtubePlayerView;
    private String songUrl;
    private String songName;
    private String duration;
    private String categoryName;
    private int length;
    private String artist;
    private ImageButton youtubeBackButton;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private ImageButton recordButton = null;
    private MediaRecorder recorder = null;
    private Button saveSong;
    FirebaseFirestore firebaseFirestore;

    private ImageButton playButton = null;
    private MediaPlayer player = null;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static String fileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private String currentEmail;
    private String userId;
    private boolean isRecording;

    final int[] progress = {0};
    
    Chronometer chronometer;

    YouTubePlayer player1;
    ProgressBar recordingProgressbar;
    DocumentReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_track_layout);
        Toolbar toolbar = findViewById(R.id.toolbar_song_track_view);
        setActionBar(toolbar);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setTitle("");
        //songName = getIntent().getStringExtra("songName")+"12345";
        songName = getIntent().getStringExtra("songName");
        songUrl = getIntent().getStringExtra("songUrl");
        duration = getIntent().getStringExtra("duration");
        categoryName = getIntent().getStringExtra("genre");
        String[] lengthArray = duration.split(":");
        length = Integer.parseInt(lengthArray[0])*60 + Integer.parseInt(lengthArray[1]);
        artist = getIntent().getStringExtra("artist");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentEmail = user.getEmail();
            userId = user.getUid();
        } else {
            currentEmail = getIntent().getStringExtra("email");
        }

        chronometer = findViewById(R.id.chronometer);
        createYoutubeView();
        setSongsDetailSection();
        youtubeBackButton = findViewById(R.id.youtubeBackButton);
        youtubeBackButton.setOnClickListener(v-> this.finish());
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/";
        fileName += songName+".mp3";
        setRecordSection();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recordingProgressbar = findViewById(R.id.recordProgressBar);
        recordingProgressbar.setMax(length);
    }

    private void setSongsDetailSection() {
        TextView name = findViewById(R.id.songName);
        TextView songDuration = findViewById(R.id.songDuration);
        TextView songArtist = findViewById(R.id.songArtist);
        name.setText(songName);
        songDuration.setText(duration);
        songArtist.setText(artist);
    }

    private void setRecordSection() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        recordButton = findViewById(R.id.record);
        recordButton.setOnClickListener(v -> {
            onRecord(mStartRecording);
        });

        playButton = findViewById(R.id.play);
        playButton.setOnClickListener(v -> {
            onPlay(mStartPlaying);
            if (mStartPlaying) {
                playButton.setImageResource(R.drawable.stop_button);
            } else {
                playButton.setImageResource(R.drawable.play);
            }
            mStartPlaying = !mStartPlaying;
        });

        saveSong = findViewById(R.id.save);
        saveSong.setOnClickListener(v-> {
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
                        youTubePlayer.cueVideo(songUrl);
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
            mStartRecording = false;
        } else {
            pauseRecording();
            AlertDialog.Builder songCloseAlert
                    = new AlertDialog
                    .Builder(SongTrackActivity.this);
            songCloseAlert.setMessage("Do you want stop recording?");
            songCloseAlert.setTitle("Recording Paused");
            songCloseAlert.setCancelable(false);
            songCloseAlert.setPositiveButton("Yes", (dialog, which) -> {
                        stopRecording();
                    });

            songCloseAlert.setNegativeButton("No", (dialog, which) -> {
                isRecording = true;
                resumeRecording();
                dialog.cancel();
            });
            AlertDialog alertDialog = songCloseAlert.create();
            alertDialog.show();
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
        recordButton.setImageResource(R.drawable.microphone);
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

        player1.play();
        recorder.start();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        isRecording = true;
        recordingProgressbar.setProgress(0);
        startProgressBarForRecording();

    }

    private void startProgressBarForRecording() {
        Handler handler = new Handler();
        new Thread(() -> {
            progress[0] = recordingProgressbar.getProgress();
            while (isRecording && progress[0] < length) {
                progress[0]++;
                handler.post(() -> recordingProgressbar.setProgress(progress[0]));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        chronometer.stop();
        progress[0] = 0;
        isRecording = false;
        recordButton.setImageResource(R.drawable.microphone_block);
        mStartRecording = true;
        player1.pause();
        showPlayer();
    }

    private void showPlayer() {
        recordingProgressbar.setVisibility(View.INVISIBLE);
        recordButton.setVisibility(View.INVISIBLE);
        chronometer.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
        saveSong.setVisibility(View.VISIBLE);

    }

    private void pauseRecording() {
        recorder.pause();
        player1.pause();
        chronometer.stop();
        isRecording = false;;
    }

    private void resumeRecording() {
        recorder.resume();
        player1.play();
        chronometer.start();
        isRecording = true;
        startProgressBarForRecording();
        mStartRecording = false;
    }

    private void uploadAudio() {
        /*CollectionReference collection = firebaseFirestore
                .collection("songsList").document(userId).collection(songName);*/

        Map<String, Object> songObject = new HashMap<>();
        songObject.put("songName", songName);
        songObject.put("version", 0);

        DocumentReference userDocumentReference = firebaseFirestore
                .collection("songsList")
                .document(userId);

        userDocumentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    System.out.println("test");
                    CollectionReference songCollection = document.getReference()
                            .collection(songName);

                    songCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if(task.isSuccessful()) {
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                                    DocumentSnapshot songDoc = queryDocumentSnapshots.getDocuments()
                                            .get(0);
                                    Long version = (Long) songDoc.get("version");
                                    songDoc.getReference().update("version", version+1);
                                    buildSongData(version+1);
                                } else {
                                    songCollection.add(songObject);
                                }
                            } else {
                                System.out.println("jikuhyt");
                            }

                        }
                    });

                } else {
                    firebaseFirestore
                            .collection("songsList")
                            .document(userId).collection(songName).add(songObject);
                    buildSongData(0L);

                }
            }  else {
                //Log.d(TAG, "Failed with: ", task.getException());
            }
        });
    }

    private void buildSongData(Long version) {
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        ref = firebaseFirestore.collection("recordings").document();
        Map song_entry = new HashMap<>();
        song_entry.put("fileName", songName+".mp3");
        song_entry.put("genre", categoryName);
        song_entry.put("name", songName);
        song_entry.put("owner", userId);
        song_entry.put("time", new Timestamp(new Date()));
        song_entry.put("contributors",new HashMap<>());
        song_entry.put("version",version);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference mFilePath = mStorageRef.child("audios")
                .child(userId)
                .child(songName+"_"+version+".mp3");

        song_entry.put("path", mFilePath.toString());

        Uri uri = Uri.fromFile(new File(fileName));
        mFilePath.putFile(uri,metadata)
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    System.out.println("Upload is " + progress + "% done");
                })

                .addOnSuccessListener(taskSnapshot -> {
                    mFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            song_entry.put("link", uri.toString());
                            ref.set(song_entry);
                        }
                    });
                    Snackbar.make(findViewById(android.R.id.content),
                            "Audio has been uploaded successfully", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", view -> {

                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                });
    }


}
