package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class LibraryHolder extends RecyclerView.ViewHolder {
    TextView libraryItemName;
    TextView libraryItemGenre;
    ImageView libraryplayBtn;
    ImageView librarypauseBtn;
    ImageView stopItemBtn;

    public LibraryHolder(@NonNull View itemView, Context context, LibraryViewClickListener libraryViewClickListener) {
        super(itemView);

        libraryItemName = itemView.findViewById(R.id.library_item_name);
        libraryItemGenre = itemView.findViewById(R.id.library_item_genre);
        libraryplayBtn = itemView.findViewById(R.id.library_item_button);
        stopItemBtn = itemView.findViewById(R.id.library_item_stop);
        librarypauseBtn = itemView.findViewById(R.id.library_item_pause);

        libraryplayBtn.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                stopItemBtn.setVisibility(View.VISIBLE);
                librarypauseBtn.setVisibility(View.VISIBLE);
                libraryplayBtn.setVisibility(View.INVISIBLE);
                libraryViewClickListener.onItemClick(position);
            }
        });
        librarypauseBtn.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                librarypauseBtn.setVisibility(View.INVISIBLE);
                libraryplayBtn.setVisibility(View.VISIBLE);
                libraryViewClickListener.onPauseClick(position);
            }
        });
        stopItemBtn.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                stopItemBtn.setVisibility(View.INVISIBLE);
                libraryplayBtn.setVisibility(View.VISIBLE);
                librarypauseBtn.setVisibility(View.INVISIBLE);
                libraryViewClickListener.onStopClick(position);
            }
        });
    }
}
