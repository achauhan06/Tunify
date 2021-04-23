package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.R;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsHolder> {
    private final ArrayList<FriendItem> friendItemArrayList;
    private final FriendViewClickListener friendViewClickListener;
    private final Context context;

    public FriendsAdapter(ArrayList<FriendItem> friendItemArrayList,
                          FriendViewClickListener friendViewClickListener,
                       Context context) {
        this.friendItemArrayList = friendItemArrayList;
        this.friendViewClickListener = friendViewClickListener;
        this.context = context;
    }
    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_item, parent, false);
        return new FriendsHolder(view, context, friendViewClickListener);
    }



    @Override
    public void onBindViewHolder(@NonNull FriendsHolder holder, int position) {
        FriendItem currentItem = friendItemArrayList.get(position);
        holder.friendName.setText(currentItem.getFriendName());
        holder.friendsCategory.setText(currentItem.getGenres());
    }

    @Override
    public int getItemCount() {
        return friendItemArrayList.size();
    }
}
