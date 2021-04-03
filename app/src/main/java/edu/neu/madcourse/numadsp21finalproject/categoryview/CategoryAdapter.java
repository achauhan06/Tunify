package edu.neu.madcourse.numadsp21finalproject.categoryview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder>{

    private final List<CategoryItem> categories;
    private final CategoryViewListener categoryViewListener;
    private final Context context;

    public CategoryAdapter(List<CategoryItem> categories,
                           CategoryViewListener categoryViewListener,
                           Context context) {
        this.categories = categories;
        this.categoryViewListener = categoryViewListener;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new CategoryHolder(view, context, categoryViewListener);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        CategoryItem currentItem = categories.get(position);
        holder.categoryName.setText(currentItem.getCategoryName());

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
