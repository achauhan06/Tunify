package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.MainActivity;
import edu.neu.madcourse.numadsp21finalproject.R;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private LibraryAdapter libraryAdapter;
    private ArrayList<LibraryItem> libraryList;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private String userId;
    FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_library);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        libraryList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        getLibraryItems();
    }

    private void createRecyclerView() {
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.library_recycler_view);
        recyclerView.setHasFixedSize(true);
        LibraryViewClickListener libraryViewClickListener = new LibraryViewClickListener() {
            @Override
            public void onItemClick(int position) {
                libraryList.get(position).onItemClick(position);
            }
        };
        libraryAdapter = new LibraryAdapter(libraryList, libraryViewClickListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(libraryAdapter);

    }

    private void getLibraryItems() {
        firebaseFirestore.getInstance().collection("recordings")
                .whereEqualTo("owner", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String path = documentSnapshot.get("path").toString();
                                String projectName = documentSnapshot.get("name").toString();
                                String genre = documentSnapshot.get("genre").toString();
                                LibraryItem item = new LibraryItem(projectName, path, genre,LibraryActivity.this);
                                libraryList.add(item);
                                // Toast.makeText(LibraryActivity.this, projectName,Toast.LENGTH_SHORT).show();

                            }
                            createRecyclerView();

                        } else {
                            Log.d("firebase", "Error getting library items", task.getException());
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
