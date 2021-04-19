package edu.neu.madcourse.numadsp21finalproject.commentview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsAdapter;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsItem;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsViewListener;

public class CommentActivity extends AppCompatActivity {
    EditText input;
    Button post;
    private ArrayList<CommentItem> commentItemArrayList;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        commentItemArrayList = new ArrayList<>();
        CommentItem item = new CommentItem("username", "comment content");
        commentItemArrayList.add(item);
        input = findViewById(R.id.comment_input);
        post = findViewById(R.id.comment_submit_btn);
        createCommentRecyclerView();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = input.getText().toString();
                Toast.makeText(CommentActivity.this, inputStr,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void createCommentRecyclerView() {

        RecyclerView.LayoutManager rLayoutManger = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.comments_recycler_view);
        recyclerView.setHasFixedSize(true);
        CommentAdapter commentAdapter = new CommentAdapter(commentItemArrayList, this);
        recyclerView.setLayoutManager(rLayoutManger);
        recyclerView.setAdapter(commentAdapter);

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
