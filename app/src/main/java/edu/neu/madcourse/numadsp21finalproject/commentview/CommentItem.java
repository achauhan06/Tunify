package edu.neu.madcourse.numadsp21finalproject.commentview;

public class CommentItem {
    private final String username;
    private final String content;

    public CommentItem(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }
}
