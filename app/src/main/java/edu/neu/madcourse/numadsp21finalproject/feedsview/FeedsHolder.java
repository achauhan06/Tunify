package edu.neu.madcourse.numadsp21finalproject.feedsview;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class FeedsHolder extends RecyclerView.ViewHolder {

    TextView feedsItemName;
    TextView feedsItemGenre;
    Button feedsItemBtn;

    public FeedsHolder(@NonNull View itemView, Context context, FeedsViewListener feedsViewListener) {
        super(itemView);

        feedsItemName = itemView.findViewById(R.id.feeds_item_name);
        feedsItemGenre = itemView.findViewById(R.id.feeds_item_genre);
        feedsItemBtn = itemView.findViewById(R.id.feeds_item_button);

        feedsItemBtn.setOnClickListener(v -> {
            if (feedsViewListener != null) {
                int position = getLayoutPosition();
                feedsViewListener.onItemClick(position);
            }
        });
    }
}
