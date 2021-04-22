package edu.neu.madcourse.numadsp21finalproject.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;

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
                .inflate(R.layout.notifications_list, parent, false);
        return new NotificationHolder(view, context, notificationViewListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        NotificationItem currentItem = notificationItems.get(position);
        holder.title.setText(currentItem.getTitle());
        holder.body.setText(currentItem.getBody());
        holder.time.setText(currentItem.getTimestamp().toDate().toString());
        if(currentItem.isFriendRequest()) {
            holder.goToLibrary.setVisibility(View.INVISIBLE);
            if(currentItem.getStatus().equals("pending")){
                holder.accept.setVisibility(View.VISIBLE);
                holder.decline.setVisibility(View.VISIBLE);
                holder.requestText.setVisibility(View.INVISIBLE);
            }else if(currentItem.getStatus().equals("accepted")){
                holder.accept.setVisibility(View.INVISIBLE);
                holder.decline.setVisibility(View.INVISIBLE);
                holder.requestText.setVisibility(View.VISIBLE);
                holder.requestText.setText("You have accepted.");
            }else if(currentItem.getStatus().equals("declined")){
                holder.accept.setVisibility(View.INVISIBLE);
                holder.decline.setVisibility(View.INVISIBLE);
                holder.requestText.setVisibility(View.VISIBLE);
                holder.requestText.setText("You have declined.");

            }

        }else {
            holder.accept.setVisibility(View.INVISIBLE);
            holder.decline.setVisibility(View.INVISIBLE);
            holder.requestText.setVisibility(View.INVISIBLE);
            holder.goToLibrary.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }
}
