package edu.neu.madcourse.numadsp21finalproject.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.neu.madcourse.numadsp21finalproject.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatHolder>{

    private List<ChatItem> listData;
    private String userName;
    public static final int DISPLAY_LEFT = 0;
    public static final int DISPLAY_RIGHT = 1;



    public ChatAdapter(List<ChatItem> listData, String userName) {
        this.userName = userName;
        this.listData = listData;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if (viewType == DISPLAY_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.chat_item_left, parent,
                    false);

        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right,
                    parent, false);

        }
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        ChatItem chat = listData.get(position);
        holder.chat.setText(chat.getMessage());
        holder.name.setText(chat.getSenderName());
        holder.messageTime.setText(chat.getTimeAsString());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(listData.get(position).getSenderName().equals(userName)) {
            return DISPLAY_RIGHT;
        }
        return DISPLAY_LEFT;
    }
}
