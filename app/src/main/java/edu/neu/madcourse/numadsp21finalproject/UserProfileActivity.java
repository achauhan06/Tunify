package edu.neu.madcourse.numadsp21finalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendItem;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsActivity;
import edu.neu.madcourse.numadsp21finalproject.users.UserItem;

public class UserProfileActivity extends AppCompatActivity {

    TextView dob, genre, fullName;
    private String email;
    private FirebaseFirestore fireStore;

    ArrayList<UserItem> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        fireStore = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar_user_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //first_name = findViewById(R.id.user_profile_first_name);
        //last_name = findViewById(R.id.user_profile_last_name);
        dob = findViewById(R.id.user_profile_dob);
        genre = findViewById(R.id.user_profile_genre);
        email = getIntent().getExtras().getString("email");
        fullName = findViewById(R.id.user_profile_heading);
        fireStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        String email1 = d.getString("Email");
                        if (email.equals(email1)) {
                            fullName.setText(d.getString("First Name")+ " " +d.getString("Last Name"));

                            //first_name.setText(d.getString("First Name"));
                            //last_name.setText(d.getString("Last Name"));
                            dob.setText(d.getString("Date of Birth"));
                            //genre.setText(d.getString("Genres"));
                            String[] arr = d.getString("Genres").split(";");
                            List<String> list1 = Lists.newArrayList(arr);
                            genre.setText(list1.toString().replace("[", "").replace("]", ""));
                            break;
                        }
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
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
}
