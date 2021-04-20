package edu.neu.madcourse.numadsp21finalproject.friendGroupView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;


public class FriendGroupAdapter extends RecyclerView.Adapter<FriendGroupHolder> {

    private final ArrayList<FriendGroupItem> friendGroupItems;
    FriendGroupListener friendGroupListener;

    public FriendGroupAdapter(ArrayList<FriendGroupItem> friendGroupItems,
                              FriendGroupListener friendGroupListener ) {
        this.friendGroupItems = friendGroupItems;
        this.friendGroupListener = friendGroupListener;
    }

    @NonNull
    @Override
    public FriendGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_layout_item, parent, false);
        return new FriendGroupHolder(view, friendGroupListener);

    }

    @Override
    public void onBindViewHolder(@NonNull FriendGroupHolder holder, int position) {
        FriendGroupItem friendGroupItem = friendGroupItems.get(position);
        holder.checkBox.setText(friendGroupItem.getFriendName());
        holder.checkBox.setChecked(friendGroupItem.isChecked());

    }

    @Override
    public int getItemCount() {
        return friendGroupItems.size();
    }
}
