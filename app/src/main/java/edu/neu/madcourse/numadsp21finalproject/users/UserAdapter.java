package edu.neu.madcourse.numadsp21finalproject.users;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsHolder;
import edu.neu.madcourse.numadsp21finalproject.songview.SongHolder;
import edu.neu.madcourse.numadsp21finalproject.songview.SongItem;
import edu.neu.madcourse.numadsp21finalproject.songview.SongViewListener;

public class UserAdapter extends RecyclerView.Adapter<UserHolder> {

    private final List<UserItem> userItems;
    private final UserViewListener userViewListener;
    private final Context context;

    public UserAdapter(List<UserItem> userItems,
                       UserViewListener userViewListener,
                       Context context) {
        this.userItems = userItems;
        this.userViewListener = userViewListener;
        this.context = context;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.users_list, parent, false);
        return new UserHolder(view, context, userViewListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        UserItem currentItem = userItems.get(position);
        holder.userName.setText(currentItem.getUserName());
        holder.userGenre.setText(currentItem.getGenre());
        if (currentItem.getProfileLink() != null && !currentItem.getProfileLink().isEmpty()) {
            setProfilePicture(currentItem.getProfileLink(), holder);
        }
    }

    @Override
    public int getItemCount() {
        return userItems.size();
    }

    private void setProfilePicture(String picturePath, @NonNull UserHolder holder) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(picturePath);
        final long FIVE_MEGABYTE = 5 * 1024 * 1024;
        ref.getBytes(FIVE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap scaledImage = Bitmap.createScaledBitmap(bmp, 60, 60, true);
            holder.profilePicture.setImageBitmap(scaledImage);
            holder.profilePicture.setVisibility(View.VISIBLE);

        }).addOnFailureListener(exception -> Toast.makeText(context,
                exception.getMessage(),Toast.LENGTH_SHORT).show());
    }

}
