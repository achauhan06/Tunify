package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.songview.SongHolder;
import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;
import edu.neu.madcourse.numadsp21finalproject.songview.SongViewListener;
import edu.neu.madcourse.numadsp21finalproject.users.UserHolder;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationHolder> {

    private final List<NotificationItem> notificationItems;
    private final NotificationViewListener notificationViewListener;
    private final Context context;

    public NotificationAdapter(List<NotificationItem> notificationItems,
                       NotificationViewListener notificationViewListener,
                       Context context) {
        this.notificationItems = notificationItems;
        this.notificationViewListener = notificationViewListener;
        this.context = context;

    }
    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_profile, parent, false);
        return new NotificationHolder(view, context, notificationViewListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        NotificationItem currentItem = notificationItems.get(position);
        holder.notificationText.setText(currentItem.getNotificationItem());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
