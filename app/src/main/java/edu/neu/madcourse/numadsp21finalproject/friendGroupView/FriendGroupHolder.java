package edu.neu.madcourse.numadsp21finalproject.friendGroupView;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class FriendGroupHolder extends RecyclerView.ViewHolder {

    CheckBox checkBox;

    public FriendGroupHolder(@NonNull View itemView, FriendGroupListener friendGroupListener) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.checkbox_item);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (friendGroupListener != null) {
                    int position = getLayoutPosition();
                    friendGroupListener.onItemClicked(position, isChecked);
                }
            }
        });
    }
}
