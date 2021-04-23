package edu.neu.madcourse.numadsp21finalproject.users;

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

public class UserAdapter extends RecyclerView.Adapter<UserHolder> {

    private final List<UserItem> userItems;
    private final UserViewListener userViewListener;
    private final Context context;

    public UserAdapter(List<UserItem> userItems,
                       UserViewListener userViewListener,
                       Context context) {
        this.userItems = userItems;
        this.userViewListener = userViewListener;
        this.context = context;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list, parent, false);
        return new UserHolder(view, context, userViewListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        UserItem currentItem = userItems.get(position);
        holder.userName.setText(currentItem.getUserName());
        holder.userGenre.setText(currentItem.getGenre());
    }

    @Override
    public int getItemCount() {
        return userItems.size();
    }
}
