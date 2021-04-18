package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class LibraryHolder extends RecyclerView.ViewHolder {
    TextView libraryItemName;
    TextView libraryItemGenre;
    ImageView libraryItemBtn;

    public LibraryHolder(@NonNull View itemView, Context context, LibraryViewClickListener libraryViewClickListener) {
        super(itemView);

        libraryItemName = itemView.findViewById(R.id.library_item_name);
        libraryItemGenre = itemView.findViewById(R.id.library_item_genre);
        libraryItemBtn = itemView.findViewById(R.id.library_item_button);

        libraryItemBtn.setOnClickListener(v -> {
            if (libraryViewClickListener != null) {
                int position = getLayoutPosition();
                libraryViewClickListener.onItemClick(position);
            }
        });
    }
}
