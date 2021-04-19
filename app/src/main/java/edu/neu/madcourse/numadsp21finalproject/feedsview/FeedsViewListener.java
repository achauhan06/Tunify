package edu.neu.madcourse.numadsp21finalproject.feedsview;

import android.content.Context;

public interface FeedsViewListener {
    void onPlayPauseClick(int position);

    void onLikeClick(int position);

    void onCommentClick(int position);

}
