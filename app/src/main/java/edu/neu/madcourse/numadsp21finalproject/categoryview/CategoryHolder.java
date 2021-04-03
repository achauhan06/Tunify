package edu.neu.madcourse.numadsp21finalproject.categoryview;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class CategoryHolder extends RecyclerView.ViewHolder{

    public TextView categoryName;
    public Button categoryButton;
    public ImageView imageView;

    public CategoryHolder(@NonNull View itemView,
                          Context context,
                          final CategoryViewListener categoryViewListener) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.category_item);
        categoryButton = itemView.findViewById(R.id.category_button);
        //imageView = itemView.findViewById(R.id.locked);

        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryViewListener != null) {
                    int position = getLayoutPosition();
                    categoryViewListener.onItemClick(position,context);
                }
            }
        });
    }
}
