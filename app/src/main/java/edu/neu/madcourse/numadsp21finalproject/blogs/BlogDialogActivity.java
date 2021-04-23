package edu.neu.madcourse.numadsp21finalproject.blogs;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;
import edu.neu.madcourse.numadsp21finalproject.utils.MyBroadcastReceiver;

public class BlogDialogActivity extends Activity {

    private String blogId;
    private String blogBody;
    private String blogTitle;
    private boolean canBeUpdated;
    private boolean isFriend;
    private Button publishBlog;
    private Button closeBlog;
    private EditText title;
    private EditText body;

    private BroadcastReceiver myBroadcastReceiver = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myBroadcastReceiver = new MyBroadcastReceiver();
        broadcastIntent();
        blogId = getIntent().getStringExtra("blogId");
        blogBody = getIntent().getStringExtra("blogBody");
        blogTitle = getIntent().getStringExtra("blogTitle");
        canBeUpdated = getIntent().getBooleanExtra("canBeUpdated", false);
        isFriend = getIntent().getBooleanExtra("isFriend", false);
        int layout = isFriend ? R.layout.blog_details_view_friend : R.layout.blog_details_view ;
        setContentView(layout);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(false);

        if (isFriend) {
            showBlogView();

        } else {
            publishBlog = findViewById(R.id.publish_blog_btn);
            closeBlog = findViewById(R.id.close_blog);
            title = findViewById(R.id.title_input);
            title.setText(blogTitle);
            body = findViewById(R.id.body_input);
            body.setText(blogBody);

            if (canBeUpdated) {
                publishBlog.setText("Update");
            }
            createDialogListeners();
        }

    }

    private void showBlogView() {
        TextView titleView = findViewById(R.id.blog_title_frnd);
        TextView bodyView = findViewById(R.id.blog_content_frnd);
        closeBlog = findViewById(R.id.blog_view_close_button);
        titleView.setText(blogTitle);
        bodyView.setText(blogBody);
        closeBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createDialogListeners() {
        publishBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String updatedTitle = title.getText().toString();
                String updatedBody = body.getText().toString();
                if (updatedTitle.isEmpty() || updatedBody.isEmpty()) {
                    Toast.makeText(BlogDialogActivity.this, "Title and Body of blog should " +
                            "not be empty.", Toast.LENGTH_SHORT);
                } else {
                    Map map = new HashMap<>();
                    map.put("blogTitle", updatedTitle);
                    map.put("blogBody", updatedBody);
                    map.put("updatedAt",  new Timestamp(new Date()));
                    Helper.db.collection("blogs").document(blogId)
                            .update(map).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Blog Updated", Toast.LENGTH_SHORT);
                            toast.show();
                            finish();

                        }
                    });
                }
            }
        });

        closeBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
