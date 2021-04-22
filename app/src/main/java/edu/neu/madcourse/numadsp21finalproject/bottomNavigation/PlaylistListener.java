package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

public interface PlaylistListener {
    void onItemClick(int position);
    void onPauseClick(int position);
    void onStopClick(int position);
    void onCommentClick(int position);
    void onLikeClick(int position);
}
