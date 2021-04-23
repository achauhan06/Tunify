package edu.neu.madcourse.numadsp21finalproject.blogs;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class BlogHolder extends RecyclerView.ViewHolder {

    public TextView blogName, blogTime;
    public ImageButton blogView;


    public BlogHolder(@NonNull View itemView, Context context, BlogViewListener blogViewListener) {
        super(itemView);

        blogName = itemView.findViewById(R.id.blog_name);
        blogTime = itemView.findViewById(R.id.blog_time);
        blogView = itemView.findViewById(R.id.blog_view_button);
        blogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blogViewListener != null) {
                    int position = getLayoutPosition();
                    blogViewListener.onViewClicked(position);
                }
            }
        });



    }
}
