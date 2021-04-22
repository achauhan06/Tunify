package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.Context;
<<<<<<< Updated upstream

import java.sql.Time;
import java.sql.Timestamp;

public class NotificationItem implements NotificationViewListener{
=======
import android.content.Intent;
import android.os.Parcelable;

import edu.neu.madcourse.numadsp21finalproject.UserProfileActivity;

public class NotificationItem implements NotificationViewListener {
>>>>>>> Stashed changes

    private String notificationItem;
    private String notificationToken;
    private Timestamp timestamp;

    public NotificationItem(String notificationItem) {
        this.notificationItem = notificationItem;
    }

    public String getNotificationItem() {
        return notificationItem;
    }

    public void setNotificationItem(String notificationItem) {
        this.notificationItem = notificationItem;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

<<<<<<< Updated upstream
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public void onItemClick(int position, Context context) {

=======
    @Override
    public void onItemClick(int position, Context context) {
        Intent intent = new Intent(context, NotificationListActivity.class);
        //intent.putExtra("email", email);
        //intent.putExtra("profileLink", profileLink);
        context.startActivity(intent);
>>>>>>> Stashed changes
    }
}
