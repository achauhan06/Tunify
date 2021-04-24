package edu.neu.madcourse.numadsp21finalproject.commentview;

import com.google.firebase.Timestamp;

public class CommentItem {
    private final String commenterName, content;
    private final Timestamp timestamp;

    public CommentItem(String commenterName, String content, Timestamp timestamp) {
        this.commenterName = commenterName;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return commenterName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTimeString() {
        return timestamp.toDate().toString().substring(0,20).trim();
    }
}
