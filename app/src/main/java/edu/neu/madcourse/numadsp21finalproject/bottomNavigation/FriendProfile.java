package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.navigation.ProfileActivity;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class FriendProfile extends AppCompatActivity {
    TextView heading, first_name, last_name, dob, genre, email, userName;
    ImageView profilePicture;
    Button unfriendBtn;
    FirebaseFirestore fireStore;
    String userId, friendId, friendName;

    private BroadcastReceiver myBroadcastReceiver = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_profile);
        Toolbar toolbar = findViewById(R.id.toolbar_friend_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(FriendProfile.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        heading = findViewById(R.id.profile_heading);
        userName = findViewById(R.id.profile_userName);
        email = findViewById(R.id.profile_email);
        first_name = findViewById(R.id.profile_first_name);
        last_name = findViewById(R.id.profile_last_name);
        dob = findViewById(R.id.profile_dob);
        genre = findViewById(R.id.profile_genre);
        unfriendBtn = findViewById(R.id.unfriend_btn);
        profilePicture = findViewById(R.id.friendProfilePicture);


        friendName = getIntent().getExtras().getString("friendName");
        heading.setText(friendName);
        userId = getIntent().getExtras().getString("userId");
        friendId = getIntent().getExtras().getString("friendId");
        DocumentReference documentReference = fireStore.getInstance().collection("users").document(friendId);
        setProfile(documentReference);
        Toast.makeText(FriendProfile.this, userId,Toast.LENGTH_SHORT).show();

        loadProfilePicture();


        unfriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireStore.getInstance().collection("friendships")
                        .whereArrayContains("friends", userId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                        ArrayList<String> idList = (ArrayList<String>) documentSnapshot.get("friends");

                                        if(friendId.equals(idList.get(0)) || friendId.equals(idList.get(1))) {
                                            documentSnapshot.getReference()
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(FriendProfile.this, "You have unfriended " + friendName,Toast.LENGTH_SHORT).show();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(FriendProfile.this, "unable to unfriend" + friendName,Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    }

                                } else {
                                    Log.d("firebase", "Error unfriending", task.getException());
                                }
                            }
                        });
                // remove friends from friends collection
                fireStore.getInstance().collection("friends")
                        .document(userId).update("friendsId", FieldValue.arrayRemove(friendId));

                fireStore.getInstance().collection("friends")
                        .document(friendId).update("friendsId", FieldValue.arrayRemove(userId));

            }
        });

    }

    private void loadProfilePicture() {
        final String[] picturePath = {Helper.DEFAULT_PICTURE_PATH};
        Helper.db.collection("images")
                .document(friendId).get().addOnSuccessListener(snapshot -> {
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
            Bitmap scaledImage = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            profilePicture.setImageBitmap(scaledImage);
            profilePicture.setVisibility(View.VISIBLE);

        }).addOnFailureListener(exception -> Toast.makeText(FriendProfile.this,
                exception.getMessage(),Toast.LENGTH_SHORT).show());
    }


    public void setProfile(DocumentReference documentReference) {
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                email.setText(value.getString("Email"));
                String first_name_str = value.getString("First Name");
                first_name.setText(first_name_str);
                String last_name_str = value.getString("Last Name");
                last_name.setText(last_name_str);
                dob.setText(value.getString("Date of Birth"));
                if (value.getString("Username") == null) {
                    userName.setText(first_name + " " + last_name);
                } else {
                    userName.setText(value.getString("Username"));
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
