package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

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

import java.util.ArrayList;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.feedsview.FeedsHolder;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsHolder> {
    private final ArrayList<FriendItem> friendItemArrayList;
    private final FriendViewClickListener friendViewClickListener;
    private final Context context;

    public FriendsAdapter(ArrayList<FriendItem> friendItemArrayList,
                          FriendViewClickListener friendViewClickListener,
                       Context context) {
        this.friendItemArrayList = friendItemArrayList;
        this.friendViewClickListener = friendViewClickListener;
        this.context = context;
    }
    @NonNull
    @Override
    public FriendsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_item, parent, false);
        return new FriendsHolder(view, context, friendViewClickListener);
    }



    @Override
    public void onBindViewHolder(@NonNull FriendsHolder holder, int position) {
        FriendItem currentItem = friendItemArrayList.get(position);
        holder.friendName.setText(currentItem.getFriendName());
        holder.friendsCategory.setText(currentItem.getGenres());
        if (currentItem.getProfileLink() != null && !currentItem.getProfileLink().isEmpty()) {
            setProfilePicture(currentItem.getProfileLink(), holder);
        }
    }

    @Override
    public int getItemCount() {
        return friendItemArrayList.size();
    }

    private void setProfilePicture(String picturePath, @NonNull FriendsHolder holder) {
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
