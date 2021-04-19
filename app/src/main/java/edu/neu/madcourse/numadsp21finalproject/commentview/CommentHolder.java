package edu.neu.madcourse.numadsp21finalproject.commentview;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class CommentHolder extends RecyclerView.ViewHolder {
    TextView username, content;

    public CommentHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.comment_item_username);
        content = itemView.findViewById(R.id.comment_item_content);

    }
}
