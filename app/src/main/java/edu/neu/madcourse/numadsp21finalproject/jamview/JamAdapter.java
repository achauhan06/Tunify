package edu.neu.madcourse.numadsp21finalproject.jamview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;

public class JamAdapter extends RecyclerView.Adapter<JamHolder> {

    private final ArrayList<JamItem> jamItems;
    private final JamViewListener jamViewListener;
    private final Context context;

    public JamAdapter(ArrayList<JamItem> jamItems, JamViewListener listener, Context context) {
        this.jamItems = jamItems;
        this.jamViewListener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public JamHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jam_item, parent, false);
        return new JamHolder(view, jamViewListener, context);
    }

    @Override
    public void onBindViewHolder(@NonNull JamHolder holder, int position) {
        JamItem jamItem = jamItems.get(position);
        holder.groupName.setText(jamItem.getGroupName());


    }

    @Override
    public int getItemCount() {
        return jamItems.size();
    }
}
