package edu.neu.madcourse.numadsp21finalproject.blogs;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class BlogItem implements BlogViewListener{

    String blogId;
    String blogTitle;
    String blogBody;
    Timestamp blogTimestamp;
    String userId;
    Context context;

    public BlogItem(DocumentSnapshot documentSnapshot, Context context, String userId) {
        this.blogId = documentSnapshot.getId();
        this.blogTitle = documentSnapshot.getString("blogTitle");
        this.blogBody = documentSnapshot.getString("blogBody");;
        this.blogTimestamp = documentSnapshot.getTimestamp("updatedAt");
        this.context = context;
        this.userId = userId;
    }

    public String getBlogId() {
        return blogId;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public String getBlogTimestamp() {
        return blogTimestamp.toDate().toString();
    }

    public Timestamp getBlogTimeInTimeFormat() {
        return blogTimestamp;
    }

    @Override
    public void onViewClicked(int position) {
        Intent intent = new Intent(context, BlogDialogActivity.class);
        intent.putExtra("blogId", blogId);
        intent.putExtra("blogTitle", blogTitle);
        intent.putExtra("blogBody", blogBody);
        intent.putExtra("canBeUpdated", true);
        context.startActivity(intent);
    }
}
