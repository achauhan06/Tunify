package edu.neu.madcourse.numadsp21finalproject.blogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;

public class BlogAdapter extends RecyclerView.Adapter<BlogHolder> {

    private List<BlogItem> blogItemList;
    private Context context;
    private BlogViewListener blogViewListener;
    private boolean isFriend;

    public BlogAdapter(List<BlogItem> blogItemList, Context context,
                       boolean isFriend,
                       BlogViewListener blogViewListener) {
        this.blogItemList = blogItemList;
        this.context = context;
        this.blogViewListener = blogViewListener;
        this.isFriend = isFriend;
    }

    @NonNull
    @Override
    public BlogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_item, parent, false);
        return new BlogHolder(view, blogViewListener, isFriend);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogHolder holder, int position) {
        BlogItem blogItem = blogItemList.get(position);
        holder.blogName.setText(blogItem.getBlogTitle());
        holder.blogTime.setText(blogItem.getBlogTimestamp());
    }

    @Override
    public int getItemCount() {
        return blogItemList.size();
    }
}
