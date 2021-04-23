package edu.neu.madcourse.numadsp21finalproject.notifications;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class NotificationHolder extends RecyclerView.ViewHolder {
    public TextView title, body, time, requestText;
    public Button accept, decline, goToLibrary, viewProfile ;

    public NotificationHolder(@NonNull View itemView, Context context,
                              final NotificationViewListener notificationViewListener) {
        super(itemView);
        accept = itemView.findViewById(R.id.accept_button);
        decline = itemView.findViewById(R.id.decline_button);
        title = itemView.findViewById(R.id.notificationTitle);
        body = itemView.findViewById(R.id.notificationBody);
        time = itemView.findViewById(R.id.notificationTime);
        goToLibrary = itemView.findViewById(R.id.notification_library_button);
        requestText = itemView.findViewById(R.id.notification_request_text);
        viewProfile = itemView.findViewById(R.id.view_profile_button);

        accept.setOnClickListener(v -> {
            if (notificationViewListener != null) {
                int position = getLayoutPosition();
                notificationViewListener.onAcceptClick(position);
            }
            accept.setVisibility(View.INVISIBLE);
            decline.setVisibility(View.INVISIBLE);
            goToLibrary.setVisibility(View.INVISIBLE);
            requestText.setVisibility(View.VISIBLE);
            requestText.setText("You have accepted.");
        });
        decline.setOnClickListener(v -> {
            if (notificationViewListener != null) {
                int position = getLayoutPosition();
                notificationViewListener.onDeclineClick(position);
            }
            accept.setVisibility(View.INVISIBLE);
            decline.setVisibility(View.INVISIBLE);
            goToLibrary.setVisibility(View.INVISIBLE);
            requestText.setVisibility(View.VISIBLE);
            requestText.setText("You have declined.");

        });

        goToLibrary.setOnClickListener(v -> {
            if (notificationViewListener != null) {
                int position = getLayoutPosition();
                notificationViewListener.onLibraryClick(position);
            }

        });

        viewProfile.setOnClickListener(v -> {
            if (notificationViewListener != null) {
                int position = getLayoutPosition();
                notificationViewListener.onProfileClick(position);
            }
        });
    }
}
