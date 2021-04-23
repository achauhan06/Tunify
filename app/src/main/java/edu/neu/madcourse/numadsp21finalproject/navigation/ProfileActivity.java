package edu.neu.madcourse.numadsp21finalproject.navigation;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.UserListActivity;
import edu.neu.madcourse.numadsp21finalproject.UserProfileActivity;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendItem;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsActivity;
import edu.neu.madcourse.numadsp21finalproject.model.User;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class ProfileActivity extends AppCompatActivity {

    EditText first_name, last_name, dob;
    TextView email, genre, userName;
    Button updateBtn;

    User userItem;

    Button uploadProfilePicture;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fireStore;
    FirebaseStorage storage;
    private static String fileName = null;
    private Uri imageUri;
    DocumentReference ref;
    FirebaseUser user;
    String imageName = "testabc";
    String userId, oldUserName, newUserName;
    public static final int PICK_IMAGE = 2;
    private Uri filePath;
    StorageReference storageReference;

    //Uri imageUri;
    ImageView profilePicture;
    private BroadcastReceiver myBroadcastReceiver = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_profile);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(ProfileActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        userItem =  new User();
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/";
        fileName += imageName+".jpeg";

        userName = findViewById(R.id.user_profile_username);
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
        //storageReference = fireStore.getInstance().getReference();

        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();
        DocumentReference documentReference = fireStore.getInstance().collection("users").document(userId);
        setProfile(documentReference);
        userName.setText(Helper.getUsername(this));
        uploadProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                //uploadImage();
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
                    //if (value.getString("path")) profilePicture.setImageURI();
                    email.setText(value.getString("Email"));
                    userItem.setEmail(value.getString("Email"));
                    String first_name_str = value.getString("First Name");
                    first_name.setText(first_name_str);
                    String last_name_str = value.getString("Last Name");
                    last_name.setText(last_name_str);
                    dob.setText(value.getString("Date of Birth"));
                    oldUserName = first_name_str + " " + last_name_str;
                    String[] arr = value.getString("Genres").split(";");
                    List<String> list = Lists.newArrayList(arr);
                    genre.setText(list.toString().replace("[", "").replace("]", ""));
                    Toast.makeText(ProfileActivity.this, email.getText().toString(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(ProfileActivity.this, "onActivityResult", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
            uploadProfilePicture.setText("Change");
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("Uploading...");
        pd.show();
        if (imageUri != null) {
            StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("images").child(userId);
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d("DownloadURL", url);
                            pd.dismiss();
                            Toast.makeText(ProfileActivity.this, "Successful image upload", Toast.LENGTH_SHORT).show();
                            buildImageData();
                        }
                    });
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void buildImageData() {

        ref = fireStore.getInstance().collection("images").document(userId);
        userId = firebaseAuth.getCurrentUser().getUid();
        Map<String, Object> image_entry = new HashMap<>();
        image_entry.put("fileName", imageName + "_.jpeg");
        image_entry.put("name", imageName);
        image_entry.put("owner", userId);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference mFilePath = mStorageRef.child("images").child(userId);

        image_entry.put("path", mFilePath.toString());
        ref.set(image_entry).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }



        private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
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

