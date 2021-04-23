package edu.neu.madcourse.numadsp21finalproject;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.blogs.BlogAdapter;
import edu.neu.madcourse.numadsp21finalproject.blogs.BlogItem;
import edu.neu.madcourse.numadsp21finalproject.blogs.BlogViewListener;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class BlogActivity extends AppCompatActivity {

    private BroadcastReceiver myBroadcastReceiver = null;
    private FloatingActionButton floatingActionButton;
    private RecyclerView.LayoutManager rLayoutManger;
    private RecyclerView recyclerView;
    private BlogAdapter blogAdapter;
    private List<BlogItem> blogItemList;
    private Dialog dialog;
    private boolean isNew = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_blog);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();


        floatingActionButton = findViewById(R.id.new_blog_button);
        blogItemList = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getUid();

        getBlogList();
        createDialogForBlog();
        createAddNewBlogButton();

    }

    private void getBlogList() {

        Helper.db.collection("blogs").whereEqualTo("owner", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                List<DocumentSnapshot> documents = value.getDocuments();
                blogItemList.clear();
                for (DocumentSnapshot documentSnapshot : documents) {
                    BlogItem blogItem = new BlogItem(documentSnapshot, BlogActivity.this ,
                            userId);
                    blogItemList.add(blogItem);
                    createRecyclerView();
                }
            }
        });
    }

    private void createDialogForBlog() {
        dialog = new Dialog(BlogActivity.this);
        dialog.setContentView(R.layout.blog_details_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button publishBlog = dialog.findViewById(R.id.publish_blog_btn);
        Button closeBlog = dialog.findViewById(R.id.close_blog);
        EditText title = dialog.findViewById(R.id.title_input);
        EditText body = dialog.findViewById(R.id.body_input);
        if (isNew) {
            publishBlog.setText("Update");
        } else {
            publishBlog.setText("Publish");
        }
        publishBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String blogTitle = title.getText().toString();
                String blogBody = body.getText().toString();
                if (blogTitle.isEmpty() || blogBody.isEmpty()) {
                    Toast.makeText(BlogActivity.this, "Title and Body of blog should " +
                            "not be empty.", Toast.LENGTH_SHORT);
                } else {
                    Helper.db.collection("blogs")
                            .document().get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            Map map = new HashMap<>();
                            map.put("blogTitle", blogTitle);
                            map.put("blogBody", blogBody);
                            map.put("owner", userId);
                            map.put("updatedAt",  new Timestamp(new Date()));
                            snapshot.getReference().set(map);
                            dialog.dismiss();
                            clearDialogContent(title, body);
                        }
                    });
                }
            }
        });

        closeBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDialogContent(title, body);
                dialog.dismiss();
            }
        });
    }

    private void clearDialogContent(EditText title, EditText body) {
        title.setText("");
        body.setText("");
    }

    private void createAddNewBlogButton() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    private void createRecyclerView() {
        if(blogItemList.size() > 0) {
            Collections.sort(blogItemList, (o1, o2) ->
                    o2.getBlogTimeInTimeFormat().compareTo(o1.getBlogTimeInTimeFormat()));
        }

        rLayoutManger = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.blog_recycler_view);
        recyclerView.setHasFixedSize(true);
        BlogViewListener blogViewListener = new BlogViewListener() {
            @Override
            public void onViewClicked(int position, Boolean isFriend) {
              blogItemList.get(position).onViewClicked(position, isFriend);
            }
        };
        blogAdapter = new BlogAdapter(blogItemList, BlogActivity.this, false, blogViewListener);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(blogAdapter);
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
