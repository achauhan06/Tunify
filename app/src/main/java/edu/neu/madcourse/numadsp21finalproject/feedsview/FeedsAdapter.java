package edu.neu.madcourse.numadsp21finalproject.feedsview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.R;


public class FeedsAdapter extends RecyclerView.Adapter<FeedsHolder> {
    private ArrayList<FeedsItem> feedsItemArrayList;
    private FeedsViewListener feedsViewListener;
    private Context context;

    public FeedsAdapter(ArrayList<FeedsItem> feedsItemArrayList,
                        FeedsViewListener feedsViewListener, Context context) {
        this.feedsItemArrayList = feedsItemArrayList;
        this.feedsViewListener = feedsViewListener;
        this.context = context;

    }
    @NonNull
    @Override
    public FeedsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_item, parent, false);
        return new FeedsHolder(view, context, feedsViewListener);
    }

    public void onBindViewHolder(@NonNull FeedsHolder holder, int position) {
        FeedsItem currentItem = feedsItemArrayList.get(position);
        holder.feedsItemName.setText(currentItem.getProjectName());
        holder.feedsItemGenre.setText(currentItem.getGenre());
        holder.feedsItemOwner.setText(currentItem.getOwnerName());
        holder.feedsItemTime.setText(currentItem.getTime());
    }

    @Override
    public int getItemCount() {
        return feedsItemArrayList.size();
    }
}
