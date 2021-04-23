package edu.neu.madcourse.numadsp21finalproject.blogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.neu.madcourse.numadsp21finalproject.BlogActivity;
import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class BlogDialogActivity extends Activity {

    private String blogId;
    private String blogBody;
    private String blogTitle;
    private boolean canBeUpdated;
    private Button publishBlog;
    private Button closeBlog;
    private EditText title;
    private EditText body;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_details_view);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(false);

        blogId = getIntent().getStringExtra("blogId");
        blogBody = getIntent().getStringExtra("blogBody");
        blogTitle = getIntent().getStringExtra("blogTitle");
        canBeUpdated = getIntent().getBooleanExtra("canBeUpdated", false);

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
}
