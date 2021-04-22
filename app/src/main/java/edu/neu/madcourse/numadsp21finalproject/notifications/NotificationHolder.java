package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.songview.SongViewListener;

public class NotificationHolder extends RecyclerView.ViewHolder {
    public TextView notificationText;
    public Button accept;
    public Button decline;
    public NotificationHolder(@NonNull View itemView, Context context,
                              final NotificationViewListener notificationViewListener) {
        super(itemView);
        accept = itemView.findViewById(R.id.accept_button);
        decline = itemView.findViewById(R.id.decline_button);
        notificationText = itemView.findViewById(R.id.notificationName);
        accept.setOnClickListener(v -> {
            if (notificationViewListener != null) {
                int position = getLayoutPosition();
                notificationViewListener.onItemClick(position,context);
            }
        });
        decline.setOnClickListener(v -> {

        });
    }
}
