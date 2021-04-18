package edu.neu.madcourse.numadsp21finalproject.navigation;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendItem;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsActivity;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class ProfileActivity extends AppCompatActivity {

    EditText first_name, last_name, dob, genre;
    TextView email;
    Button updateBtn, uploadProfilePicture;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fireStore;
    FirebaseUser user;
    String userId, oldUserName, newUserName;
    public static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView profilePicture;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        email = findViewById(R.id.profile_email);
        first_name = findViewById(R.id.profile_first_name);
        last_name = findViewById(R.id.profile_last_name);
        dob = findViewById(R.id.profile_dob);
        genre = findViewById(R.id.profile_genre);
        updateBtn = findViewById(R.id.profile_update);
        uploadProfilePicture = findViewById(R.id.uploadProfilePicture);
        profilePicture = (ImageView)findViewById(R.id.yourProfilePicture);


        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(ProfileActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        DocumentReference documentReference = fireStore.getInstance().collection("users").document(userId);
        setProfile(documentReference);
        uploadProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }


        });


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month,
                                          int day) {

                        String a = day+ "/" + (month + 1) +"/" +year;
                        dob.setText(""+a);
                    }
                };
                Time date = new Time();
                DatePickerDialog d = new DatePickerDialog(ProfileActivity.this, dpd, date.year ,date.month, date.monthDay);
                d.show();
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (first_name.getText().toString().equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please enter your First Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*
                else if (email.getText().toString().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "Please type an email id", Toast.LENGTH_SHORT).show();
                    return;
                }
                */
                else if (dob.getText().toString().equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please enter a DOB", Toast.LENGTH_SHORT).show();
                    return;
                } else if (last_name.getText().toString().equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please type a last name", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    updateProfile(documentReference);
                }

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
                    oldUserName = first_name_str + " " + last_name_str;
                    String[] arr = value.getString("Genres").split(";");
                    List<String> list = Lists.newArrayList(arr);
                    genre.setText(list.toString().replace("[", "").replace("]", ""));
                }
            });

        }


        public void updateProfile(DocumentReference documentReference) {
            fireStore.getInstance().runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                    transaction.update(documentReference, "First Name", first_name.getText().toString());
                    transaction.update(documentReference, "Last Name", last_name.getText().toString());
                    transaction.update(documentReference, "Date of Birth", dob.getText().toString());

                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    newUserName = first_name.getText().toString() + " " + last_name.getText().toString();
                    updateFriendShips();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Unable to update your profile", Toast.LENGTH_SHORT).show();

                }
            });

        }

        public void updateFriendShips() {
            fireStore.getInstance().collection("friendships")
                    .whereArrayContains("friends", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for(DocumentSnapshot documentSnapshot : task.getResult()) {
                                    String name1 = documentSnapshot.get("name1").toString();
                                    DocumentReference documentReference = documentSnapshot.getReference();
                                    if(name1.equals(oldUserName)) {
                                        updateFriendShipsName(documentReference, "name1");
                                    }else {
                                        updateFriendShipsName(documentReference, "name2");
                                    }


                                }

                            } else {
                                Toast.makeText(ProfileActivity.this, "Error updating friendship name", Toast.LENGTH_SHORT).show();
                                Log.d("firebase", "Error updating friendship name", task.getException());
                            }
                        }
                    });

        }

        private void updateFriendShipsName(DocumentReference documentReference, String field) {
            fireStore.getInstance().runTransaction(new Transaction.Function<Void>() {
                @Nullable
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                    transaction.update(documentReference, field, newUserName);
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProfileActivity.this, "Friends list name updated", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Unable to update your name on friends list", Toast.LENGTH_SHORT).show();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
        }
    }



    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }
}
