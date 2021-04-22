package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.Context;

import java.sql.Time;
import java.sql.Timestamp;

public class NotificationItem implements NotificationViewListener{

    private String notificationItem;
    private String notificationToken;
    private Timestamp timestamp;

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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public void onItemClick(int position, Context context) {

    }
}
