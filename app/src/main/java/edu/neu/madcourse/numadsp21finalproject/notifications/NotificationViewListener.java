package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.Context;

public interface NotificationViewListener {
    void onLibraryClick(int position);
    void onAcceptClick(int position);
    void onDeclineClick(int position);

}
