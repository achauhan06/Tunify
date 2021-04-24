package edu.neu.madcourse.numadsp21finalproject;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.searchView.SearchAdapter;
import edu.neu.madcourse.numadsp21finalproject.searchView.SearchItem;
import edu.neu.madcourse.numadsp21finalproject.searchView.SearchViewListener;
import edu.neu.madcourse.numadsp21finalproject.utils.CustomToast;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class SearchResultActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId, userName, friendName, friendId;
    ArrayList<SearchItem> searchItems;
    private List<String> friendIdsList;

    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;

    private BroadcastReceiver myBroadcastReceiver = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(SearchResultActivity.this, "Please log in first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        searchItems = new ArrayList<SearchItem>();
        user = firebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        userName = Helper.getUsername(this);
        String searchValue = getIntent().getExtras().getString("searchValue");
        getSearchResults(searchValue);


    }


    private void  getSearchResults(String searchValue) {

        Helper.db.collection("friends").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    friendIdsList = (List<String>)value.get("friendsId");
                }
                buildUserListView(searchValue);
            }
        });
    }

    private void buildUserListView(String searchValue) {
        Toast.makeText(SearchResultActivity.this, searchValue + "2", Toast.LENGTH_SHORT).show();

        Helper.db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException error) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                if(!d.getId().equals(userId) && d.getString("Username") != null
                                        && d.getString("Username").contains(searchValue)
                                        || d.getString("First Name").contains(searchValue)
                                        || d.getString("Last Name").contains(searchValue)) {
                                    String searchName = d.getString("Username");
                                    String email = d.getString("Email");
                                    String searchUserId = d.getId();
                                    SearchItem user = new SearchItem(userId, searchUserId, searchName, email,SearchResultActivity.this);

                                    String genres = d.getString("Genres");
                                    if (genres != null) {
                                        String[] genreArray = genres.split(";");
                                        for (int i = 0; i < genreArray.length; i++) {
                                            genreArray[i] = "#" + genreArray[i];
                                        }
                                        user.setGenres(String.join(" ", genreArray));
                                    }

                                    if (!friendIdsList.contains(searchUserId)) {
                                        user.setIsFriend(false);
                                    }else {
                                        user.setIsFriend(true);
                                    }
                                    searchItems.add(user);
                                    setOwnerPicture(searchUserId, user);
                                }

                            }
                        } else {
                            Toast.makeText(SearchResultActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                        createRecyclerView();

                    }
                });
    }

    private void setOwnerPicture(String ownerId, SearchItem item) {
        final String[] picturePath = {Helper.DEFAULT_PICTURE_PATH};
        Helper.db.collection("images")
                .document(ownerId).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                picturePath[0] = snapshot.getString("path");
            }
            item.setProfileLink(picturePath[0]);
            searchAdapter.notifyDataSetChanged();
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                item.setProfileLink(picturePath[0]);
                searchAdapter.notifyDataSetChanged();
            }
        });

    }

    private void createRecyclerView() {
        if (searchItems.size() == 0) {
            CustomToast toast = new CustomToast(SearchResultActivity.this,
                    "No results found!", Snackbar.LENGTH_SHORT);
            toast.makeCustomToast(Gravity.CENTER);
        }
        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.search_recycler_view);
        recyclerView.setHasFixedSize(true);
        SearchViewListener searchViewListener = new SearchViewListener() {
            @Override
            public void onItemClick(int position) {
                searchItems.get(position).onItemClick(position);
            }

            @Override
            public void onViewPlaylistClick(int position) {
                searchItems.get(position).onViewPlaylistClick(position);

            }

            @Override
            public void onViewBlogClick(int position) {
                searchItems.get(position).onViewBlogClick(position);

            }

            @Override
            public void onChatClick(int position) {
                searchItems.get(position).onChatClick(position);

            }
        };
        searchAdapter = new SearchAdapter(searchItems, searchViewListener, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(searchAdapter);

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
