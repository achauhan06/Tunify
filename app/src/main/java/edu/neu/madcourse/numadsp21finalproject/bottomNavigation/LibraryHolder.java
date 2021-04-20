package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class LibraryHolder extends RecyclerView.ViewHolder {
    TextView libraryItemName, libraryItemGenre, libraryCommentCount, libraryLikeCount, libraryItemTime;
    ImageView libraryplayBtn, librarypauseBtn, stopItemBtn, libraryCommentBtn, libraryLikeWhite, libraryLikeRed;

    public LibraryHolder(@NonNull View itemView, Context context, LibraryViewClickListener libraryViewClickListener) {
        super(itemView);
        libraryItemName = itemView.findViewById(R.id.library_item_name);
        libraryItemGenre = itemView.findViewById(R.id.library_item_genre);
        libraryplayBtn = itemView.findViewById(R.id.library_item_button);
        stopItemBtn = itemView.findViewById(R.id.library_item_stop);
        librarypauseBtn = itemView.findViewById(R.id.library_item_pause);
        libraryCommentBtn = itemView.findViewById(R.id.library_item_comment_btn);
        libraryCommentCount = itemView.findViewById(R.id.library_item_comment_count);
        libraryLikeCount = itemView.findViewById(R.id.library_item_like_count);
        libraryLikeWhite = itemView.findViewById(R.id.library_item_like_white);
        libraryLikeRed = itemView.findViewById(R.id.library_item_like_red);
        libraryItemTime = itemView.findViewById(R.id.library_item_time);


        libraryplayBtn.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                stopItemBtn.setVisibility(View.VISIBLE);
                librarypauseBtn.setVisibility(View.VISIBLE);
                libraryplayBtn.setVisibility(View.INVISIBLE);
                libraryViewClickListener.onItemClick(position);
            }
        });
        librarypauseBtn.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                librarypauseBtn.setVisibility(View.INVISIBLE);
                libraryplayBtn.setVisibility(View.VISIBLE);
                libraryViewClickListener.onPauseClick(position);
            }
        });
        stopItemBtn.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                stopItemBtn.setVisibility(View.INVISIBLE);
                libraryplayBtn.setVisibility(View.VISIBLE);
                librarypauseBtn.setVisibility(View.INVISIBLE);
                libraryViewClickListener.onStopClick(position);
            }
        });

        libraryLikeWhite.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                if(libraryLikeWhite.getVisibility() == View.VISIBLE) {
                    libraryLikeWhite.setVisibility(View.INVISIBLE);
                    libraryLikeRed.setVisibility(View.VISIBLE);
                    int likeCount = Integer.valueOf(libraryLikeCount.getText().toString());
                    libraryLikeCount.setText(String.valueOf(likeCount + 1));

                }
                libraryViewClickListener.onLikeClick(position);
            }
        });

        libraryLikeRed.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                if(libraryLikeRed.getVisibility() == View.VISIBLE) {
                    libraryLikeWhite.setVisibility(View.VISIBLE);
                    libraryLikeRed.setVisibility(View.INVISIBLE);
                    int likeCount = Integer.valueOf(libraryLikeCount.getText().toString());
                    libraryLikeCount.setText(String.valueOf(likeCount - 1));

                }
                libraryViewClickListener.onLikeClick(position);
            }
        });

        libraryCommentBtn.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                libraryViewClickListener.onCommentClick(position);
            }
        });
    }
}
