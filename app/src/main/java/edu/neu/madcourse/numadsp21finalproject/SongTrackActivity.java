package edu.neu.madcourse.numadsp21finalproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.LibraryActivity;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

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
    private Button recordagain;
    FirebaseFirestore firebaseFirestore;
    private long finalScore = 0;

    private long timeWhenStopped = 0;

    private ImageButton playButton = null;
    private MediaPlayer player = null;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static String fileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    private String userName;
    private String userId;
    private boolean isRecording;

    private int songAttemptNumber = 1;

    final int[] progress = {0};
    
    Chronometer chronometer;

    YouTubePlayer player1;
    ProgressBar recordingProgressbar;
    DocumentReference ref;
    private TextView attemptView;
    private BroadcastReceiver myBroadcastReceiver = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(SongTrackActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        if (savedInstanceState!=null) {
            songAttemptNumber++;
        }

        setContentView(R.layout.song_track_layout);
        Toolbar toolbar = findViewById(R.id.toolbar_song_track_view);
        setActionBar(toolbar);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setTitle("");
        songName = getIntent().getStringExtra("songName");
        songUrl = getIntent().getStringExtra("songUrl");
        duration = getIntent().getStringExtra("duration");
        categoryName = getIntent().getStringExtra("genre");
        String[] lengthArray = duration.split(":");
        length = Integer.parseInt(lengthArray[0])*60 + Integer.parseInt(lengthArray[1]);
        artist = getIntent().getStringExtra("artist");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        }

        userName = Helper.getUsername(this);

        chronometer = findViewById(R.id.chronometer);
        createYoutubeView();
        setSongsDetailSection();

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/";
        fileName += songName+".mp3";
        createRecorder();
        setPlayUploadSection();
        setRecordSection();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recordingProgressbar = findViewById(R.id.recordProgressBar);
        recordingProgressbar.setProgress(0);
        recordingProgressbar.setMax(length);
        attemptView = findViewById(R.id.attempt_score);

        createBackButton();
        setDefaultYouTubePlayer();
    }



    private void setPlayUploadSection() {
        saveSong = findViewById(R.id.save);
        saveSong.setOnClickListener(v-> {
            uploadAudio();
        });
        recordagain = findViewById(R.id.record_again);
        recordagain.setOnClickListener(v-> {
            if (Build.VERSION.SDK_INT >= 11) {
                Bundle bundle = new Bundle();
                bundle.putInt("attempt", songAttemptNumber);
                onSaveInstanceState(bundle);
                recreate();
            } else {
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);

                startActivity(intent);
                overridePendingTransition(0, 0);
            }

        });
    }

    private void createRecorder() {
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

    private void createBackButton() {
        youtubeBackButton = findViewById(R.id.youtubeBackButton);
        youtubeBackButton.setOnClickListener(v-> {
            if (isRecording) {
                pauseRecording();
                alertButton("Recording Paused",
                        "Are you sure you want to quit?", true);
            } else {
                this.finish();
            }

        });
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
    }

    public YouTubePlayer setDefaultYouTubePlayer() {
        player1 = new YouTubePlayer() {
            @Override
            public void release() {

            }

            @Override
            public void cueVideo(String s) {

            }

            @Override
            public void cueVideo(String s, int i) {

            }

            @Override
            public void loadVideo(String s) {

            }

            @Override
            public void loadVideo(String s, int i) {

            }

            @Override
            public void cuePlaylist(String s) {

            }

            @Override
            public void cuePlaylist(String s, int i, int i1) {

            }

            @Override
            public void loadPlaylist(String s) {

            }

            @Override
            public void loadPlaylist(String s, int i, int i1) {

            }

            @Override
            public void cueVideos(List<String> list) {

            }

            @Override
            public void cueVideos(List<String> list, int i, int i1) {

            }

            @Override
            public void loadVideos(List<String> list) {

            }

            @Override
            public void loadVideos(List<String> list, int i, int i1) {

            }

            @Override
            public void play() {

            }

            @Override
            public void pause() {

            }

            @Override
            public boolean isPlaying() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public void next() {

            }

            @Override
            public void previous() {

            }

            @Override
            public int getCurrentTimeMillis() {
                return 0;
            }

            @Override
            public int getDurationMillis() {
                return 0;
            }

            @Override
            public void seekToMillis(int i) {

            }

            @Override
            public void seekRelativeMillis(int i) {

            }

            @Override
            public void setFullscreen(boolean b) {

            }

            @Override
            public void setOnFullscreenListener(OnFullscreenListener onFullscreenListener) {

            }

            @Override
            public void setFullscreenControlFlags(int i) {

            }

            @Override
            public int getFullscreenControlFlags() {
                return 0;
            }

            @Override
            public void addFullscreenControlFlag(int i) {

            }

            @Override
            public void setPlayerStyle(PlayerStyle playerStyle) {

            }

            @Override
            public void setShowFullscreenButton(boolean b) {

            }

            @Override
            public void setManageAudioFocus(boolean b) {

            }

            @Override
            public void setPlaylistEventListener(PlaylistEventListener playlistEventListener) {

            }

            @Override
            public void setPlayerStateChangeListener(PlayerStateChangeListener playerStateChangeListener) {

            }

            @Override
            public void setPlaybackEventListener(PlaybackEventListener playbackEventListener) {

            }
        };
        player1.cueVideo("5r0i76VJqL8");
        return player1;
    }

    private void createYoutubeView() {
        youtubePlayerView = findViewById(R.id.youtubePlayerView);
        youtubePlayerView.initialize(Helper.YOUTUBE_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
                        player1 = youTubePlayer == null ? setDefaultYouTubePlayer() : youTubePlayer;
                        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                        if (!wasRestored) {
                            youTubePlayer.cueVideo(songUrl);
                        } else {
                            if (isRecording) {
                                youTubePlayer.loadVideo(songUrl);
                            } else {
                                youTubePlayer.cueVideo(songUrl);
                            }

                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        player1 = setDefaultYouTubePlayer();
                        player1.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                        if (isRecording) {
                            player1.loadVideo(songUrl);
                        }
                        else {
                            player1.cueVideo(songUrl);
                        }


                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
            alertButton("Recording Paused",
                    "Do you want stop recording?", false);
        }
    }

    private void alertButton(String title, String message, boolean isBackButton) {
        AlertDialog.Builder songCloseAlert
                = new AlertDialog
                .Builder(SongTrackActivity.this);
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
            player.setOnCompletionListener(mp -> playButton.setImageResource(R.drawable.play));
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {
        try {
            recorder.start();
            player1.play();
            recordButton.setImageResource(R.drawable.microphone);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            isRecording = true;
            recordingProgressbar.setProgress(0);
            startProgressBarForRecording();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SongTrackActivity.this, "Error loading recorder", Toast.LENGTH_SHORT);
        }



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
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
            long finalTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenStopped = 0;
            chronometer.stop();
            progress[0] = 0;
            isRecording = false;
            recordButton.setImageResource(R.drawable.microphone_block);
            mStartRecording = true;
            player1.pause();
            showPlayer();
            setCurrentScore(finalTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setCurrentScore(long finalTime) {
        finalScore = (long) (finalTime/100 + Math.pow(2,songAttemptNumber));
        StringBuilder builder = new StringBuilder();
        builder.append("You scored " + finalScore);
        builder.append(". Sing again to improve");
        attemptView.setText(builder.toString());

    }

    private void showPlayer() {
        recordingProgressbar.setVisibility(View.INVISIBLE);
        recordButton.setVisibility(View.INVISIBLE);
        chronometer.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.VISIBLE);
        saveSong.setVisibility(View.VISIBLE);
        recordagain.setVisibility(View.VISIBLE);
        attemptView.setVisibility(View.VISIBLE);
        recordingProgressbar.setProgress(0);
    }

    private void pauseRecording() {
        try {
            recorder.pause();
            player1.pause();

            timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            isRecording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resumeRecording() {
        try {
            recorder.resume();
            player1.play();
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            chronometer.start();
            isRecording = true;
            startProgressBarForRecording();
            mStartRecording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void uploadAudio() {
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
                    CollectionReference songCollection = document.getReference()
                            .collection(songName);

                    songCollection.get().addOnCompleteListener(task1 -> {

                        if(task1.isSuccessful()) {
                            QuerySnapshot queryDocumentSnapshots = task1.getResult();
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
                        }

                    });

                } else {
                    Map song_entry = new HashMap<>();
                    song_entry.put("fileName", songName+".mp3");
                    userDocumentReference.set(song_entry);
                    userDocumentReference.collection(songName).add(songObject);
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
        song_entry.put("fileName", songName+"_"+version+".mp3");
        song_entry.put("genre", categoryName);
        song_entry.put("name", songName);
        song_entry.put("owner", userId);
        song_entry.put("time", new Timestamp(new Date()));
        song_entry.put("contributors",new HashMap<>());
        song_entry.put("version",version);
        song_entry.put("commentsCount", 0);
        song_entry.put("likes", new ArrayList<String>());

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
                    mFilePath.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        song_entry.put("link", uri1.toString());
                        song_entry.put("username", userName);
                        ref.set(song_entry);
                        Helper.db.collection("users")
                                .document(userId).get().addOnSuccessListener(
                                snapshot -> {
                                    if (snapshot != null) {
                                        Long score = (Long) snapshot.get("currentScore") == null ? 0L
                                                : (Long) snapshot.get("currentScore") ;
                                        Long level = (Long) snapshot.get("currentLevel") == null ? 0L
                                                : (Long) snapshot.get("currentLevel") ;
                                        String categories = (String) snapshot.get("Genres");
                                        List<String> currentCategoryList = new ArrayList<>();
                                        if (categories != null) {
                                            currentCategoryList = Arrays.asList(categories.split(";"));
                                        }
                                        score += finalScore;
                                        snapshot.getReference().update("currentScore", score);
                                        if (score > 500 + 200 * level) {
                                            snapshot.getReference().update("currentLevel",
                                                    level+1);
                                            String newCategory = "";
                                            for(String genre : Helper.CATEGORY_LIST) {
                                                if (!currentCategoryList.contains(genre)) {
                                                    newCategory = genre;
                                                    break;
                                                }
                                            }

                                            List<String> finalList = new ArrayList<>();
                                            finalList.addAll(currentCategoryList);
                                            finalList.add(newCategory);
                                            snapshot.getReference()
                                                    .update("Genres",
                                                            String.join(";", finalList));
                                            String categoryUnlockMessage =
                                                    "Congratulations!!! You have gone to the next level. " +
                                                            newCategory + " category has been unlocked for you. !";


                                              Toast successToast = Toast.makeText(getApplicationContext(),
                                                      categoryUnlockMessage
                                                      , Toast.LENGTH_LONG);
                                              View view = successToast.getView();
                                              view.setBackgroundColor( getResources()
                                                      .getColor(R.color.primaryTextColor));
                                              successToast.setGravity(Gravity.CENTER, 0, 0);
                                              TextView text = view.findViewById(android.R.id.message);

                                              text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
                                              text.setTextColor(Color.WHITE);
                                              text.setTextSize(Integer.valueOf(15));
                                              successToast.show();
                                        }

                                    }
                                });
                    });
                    Snackbar.make(findViewById(android.R.id.content),
                            "Audio has been published successfully", Snackbar.LENGTH_SHORT)
                            .addCallback(new Snackbar.Callback(){
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                                        openLibrary();
                                    }
                                }
                            })
                            .show();
                });
    }

    private void openLibrary() {
        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
        this.finish();
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
