package edu.neu.madcourse.numadsp21finalproject.commentview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.R;


public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {
    ArrayList<CommentItem> commentItemArrayList;
    Context context;

    public CommentAdapter(ArrayList<CommentItem> commentItemArrayList, Context context) {
        this.commentItemArrayList = commentItemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        CommentItem currentItem = commentItemArrayList.get(position);
        holder.username.setText(currentItem.getUsername() + ":");
        holder.content.setText(currentItem.getContent());
        holder.time.setText(currentItem.getTimeString());

    }

    @Override
    public int getItemCount() {
        return commentItemArrayList.size();
    }
}
