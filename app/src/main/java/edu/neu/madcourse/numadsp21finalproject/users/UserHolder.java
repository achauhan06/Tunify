package edu.neu.madcourse.numadsp21finalproject.users;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.numadsp21finalproject.R;
//import edu.neu.madcourse.numadsp21finalproject.songview.SongViewListener;

public class UserHolder extends RecyclerView.ViewHolder {

    public TextView userName;
    public Button visitProfile;
    public TextView userGenre;

    public UserHolder(@NonNull View itemView,
                      Context context,
                      final UserViewListener userViewListener) {
        super(itemView);
        userName = itemView.findViewById(R.id.userName);
        userGenre = itemView.findViewById(R.id.category_list);
        visitProfile = itemView.findViewById(R.id.profile_button);

        visitProfile.setOnClickListener(v -> {
            if (userViewListener != null) {
                int position = getLayoutPosition();
                userViewListener.onItemClick(position,context);
            }
        });
    }
}
