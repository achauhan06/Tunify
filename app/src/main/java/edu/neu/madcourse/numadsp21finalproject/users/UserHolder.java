package edu.neu.madcourse.numadsp21finalproject.users;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;

public class UserHolder extends RecyclerView.ViewHolder {

    public TextView userName;
    public ImageButton visitProfile;
    public TextView userGenre;
    public ImageView profilePicture;

    public UserHolder(@NonNull View itemView,
                      Context context,
                      final UserViewListener userViewListener) {
        super(itemView);
        userName = itemView.findViewById(R.id.userName);
        userGenre = itemView.findViewById(R.id.category_list);
        visitProfile = itemView.findViewById(R.id.profile_button);
        profilePicture = itemView.findViewById(R.id.userProfilePicture);
        visitProfile.setOnClickListener(v -> {
            if (userViewListener != null) {
                int position = getLayoutPosition();
                userViewListener.onItemClick(position,context);
            }
        });
    }
}
