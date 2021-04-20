package edu.neu.madcourse.numadsp21finalproject.jamview;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class JamHolder extends RecyclerView.ViewHolder {

    public TextView groupName;
    public Button groupEnter;

    public JamHolder(View view, JamViewListener jamViewListener, Context context) {
        super(view);
        groupName = view.findViewById(R.id.group_name);
        groupEnter = view.findViewById(R.id.group_enter_btn);
        groupEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jamViewListener != null) {
                    int position = getLayoutPosition();
                    jamViewListener.onItemClick(position,context);
                }
            }
        });
    }
}
