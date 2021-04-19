package edu.neu.madcourse.numadsp21finalproject.feedsview;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class FeedsHolder extends RecyclerView.ViewHolder {

    TextView feedsItemName, feedsItemGenre, feedsItemOwner, feedsItemTime,
            feedsLikeCount, feedsCommentCount;

    ImageButton feedsItemBtn, feedsItemLikeWhiteBtn, feedsItemLikeRedBtn, feedsItemCommentBtn;

    public FeedsHolder(@NonNull View itemView, Context context, FeedsViewListener feedsViewListener) {
        super(itemView);

        feedsItemName = itemView.findViewById(R.id.feeds_item_name);
        feedsItemGenre = itemView.findViewById(R.id.feeds_item_genre);
        feedsItemOwner = itemView.findViewById(R.id.feeds_item_owner);
        feedsItemTime = itemView.findViewById(R.id.feeds_item_time);
        feedsLikeCount = itemView.findViewById(R.id.feeds_item_like_count);
        feedsCommentCount = itemView.findViewById(R.id.feeds_item_comment_count);

        feedsItemBtn = itemView.findViewById(R.id.feeds_item_button);
        feedsItemLikeWhiteBtn = itemView.findViewById(R.id.feeds_item_like_white);
        feedsItemLikeRedBtn = itemView.findViewById(R.id.feeds_item_like_red);
        feedsItemCommentBtn = itemView.findViewById(R.id.feeds_item_comment_btn);



        feedsItemBtn.setOnClickListener(v -> {
            if (feedsViewListener != null) {
                int position = getLayoutPosition();
                feedsViewListener.onPlayPauseClick(position);
            }
        });
        feedsItemLikeWhiteBtn.setOnClickListener(v -> {
            if (feedsViewListener != null) {
                int position = getLayoutPosition();
                if(feedsItemLikeWhiteBtn.getVisibility() == View.VISIBLE) {
                    feedsItemLikeWhiteBtn.setVisibility(View.INVISIBLE);
                    feedsItemLikeRedBtn.setVisibility(View.VISIBLE);
                    int likeCount = Integer.valueOf(feedsLikeCount.getText().toString());
                    feedsLikeCount.setText(String.valueOf(likeCount + 1));

                }
                feedsViewListener.onLikeClick(position);
            }
        });

        feedsItemLikeRedBtn.setOnClickListener(v -> {
            if (feedsViewListener != null) {
                int position = getLayoutPosition();
                if(feedsItemLikeRedBtn.getVisibility() == View.VISIBLE) {
                    feedsItemLikeWhiteBtn.setVisibility(View.VISIBLE);
                    feedsItemLikeRedBtn.setVisibility(View.INVISIBLE);
                    int likeCount = Integer.valueOf(feedsLikeCount.getText().toString());
                    feedsLikeCount.setText(String.valueOf(likeCount - 1));

                }
                feedsViewListener.onLikeClick(position);
            }
        });

        feedsItemCommentBtn.setOnClickListener(v -> {
            if (feedsViewListener != null) {
                int position = getLayoutPosition();
                feedsViewListener.onCommentClick(position);
            }
        });
    }
}
