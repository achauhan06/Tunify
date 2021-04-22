package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.R;

public class FriendsPlaylistAdapter extends RecyclerView.Adapter<FriendsPlaylistHolder> {

    private ArrayList<FriendsPlaylistItem> friendsPlaylistItemArrayList;
    private PlaylistListener playlistListener;
    private Context context;

    public FriendsPlaylistAdapter(ArrayList<FriendsPlaylistItem> friendsPlaylistItemArrayList,
                          PlaylistListener libraryViewClickListener,
                          Context context) {
        this.friendsPlaylistItemArrayList = friendsPlaylistItemArrayList;
        this.playlistListener = libraryViewClickListener;
        this.context = context;
    }
    @NonNull
    @Override
    public FriendsPlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent,
                false);
        return new FriendsPlaylistHolder(view, context, playlistListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsPlaylistHolder holder, int position) {
        FriendsPlaylistItem currentItem = friendsPlaylistItemArrayList.get(position);
        holder.libraryItemName.setText(currentItem.getProjectName());
        holder.libraryItemGenre.setText(currentItem.getGenre());
        holder.libraryItemTime.setText(currentItem.getTimeString());
        holder.libraryLikeCount.setText(String.valueOf(currentItem.getLikeCount()));
        holder.libraryCommentCount.setText(String.valueOf(currentItem.getCommentCount()));
        if(currentItem.isLikedByMe()) {
            holder.libraryLikeRed.setVisibility(View.VISIBLE);
            holder.libraryLikeWhite.setVisibility(View.INVISIBLE);
        }else {
            holder.libraryLikeRed.setVisibility(View.INVISIBLE);
            holder.libraryLikeWhite.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return friendsPlaylistItemArrayList.size();
    }

}
