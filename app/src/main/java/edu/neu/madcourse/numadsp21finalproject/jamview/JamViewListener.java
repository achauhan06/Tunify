package edu.neu.madcourse.numadsp21finalproject.jamview;

import android.content.Context;

public interface JamViewListener {
    void onItemClick(int position, Context context);
    void onItemDelete(int position);
}
