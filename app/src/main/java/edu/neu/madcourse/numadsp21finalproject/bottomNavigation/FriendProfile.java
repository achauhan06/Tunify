package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import edu.neu.madcourse.numadsp21finalproject.R;

public class FriendProfile extends AppCompatActivity {
    TextView heading, first_name, last_name, dob, genre, email;
    Button unfriendBtn;
    FirebaseFirestore fireStore;
    String userId, friendId, friendName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_profile);
        Toolbar toolbar = findViewById(R.id.toolbar_friend_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        heading = findViewById(R.id.profile_heading);
        email = findViewById(R.id.profile_email);
        first_name = findViewById(R.id.profile_first_name);
        last_name = findViewById(R.id.profile_last_name);
        dob = findViewById(R.id.profile_dob);
        genre = findViewById(R.id.profile_genre);
        unfriendBtn = findViewById(R.id.unfriend_btn);


        friendName = getIntent().getExtras().getString("friendName");
        heading.setText(friendName);
        userId = getIntent().getExtras().getString("userId");
        friendId = getIntent().getExtras().getString("friendId");
        DocumentReference documentReference = fireStore.getInstance().collection("users").document(friendId);
        setProfile(documentReference);
        Toast.makeText(FriendProfile.this, userId,Toast.LENGTH_SHORT).show();


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
                                        String name1 = documentSnapshot.get("name1").toString();
                                        String name2 = documentSnapshot.get("name2").toString();
                                        if(name1.equals(friendName) || name2.equals(friendName)) {
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
            }
        });

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

}
