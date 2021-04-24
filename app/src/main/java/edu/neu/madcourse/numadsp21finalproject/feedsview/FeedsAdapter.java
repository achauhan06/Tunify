package edu.neu.madcourse.numadsp21finalproject.feedsview;

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
import edu.neu.madcourse.numadsp21finalproject.UserProfileActivity;


public class FeedsAdapter extends RecyclerView.Adapter<FeedsHolder> {
    private ArrayList<FeedsItem> feedsItemArrayList;
    private FeedsViewListener feedsViewListener;
    private Context context;

    public FeedsAdapter(ArrayList<FeedsItem> feedsItemArrayList,
                        FeedsViewListener feedsViewListener, Context context) {
        this.feedsItemArrayList = feedsItemArrayList;
        this.feedsViewListener = feedsViewListener;
        this.context = context;

    }
    @NonNull
    @Override
    public FeedsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_item, parent, false);
        return new FeedsHolder(view, context, feedsViewListener);
    }

    public void onBindViewHolder(@NonNull FeedsHolder holder, int position) {
        FeedsItem currentItem = feedsItemArrayList.get(position);
        holder.feedsItemName.setText(currentItem.getProjectName());
        holder.feedsItemGenre.setText(currentItem.getGenre());
        holder.feedsItemOwner.setText(currentItem.getOwnerName());
        holder.feedsItemTime.setText(currentItem.getTimeString());
        holder.feedsLikeCount.setText(String.valueOf(currentItem.getLikeCount()));
        holder.feedsCommentCount.setText(String.valueOf(currentItem.getCommentsCount()));

        if(currentItem.getLikedByMe()) {
            holder.feedsItemLikeRedBtn.setVisibility(View.VISIBLE);
            holder.feedsItemLikeWhiteBtn.setVisibility(View.INVISIBLE);
        }else {
            holder.feedsItemLikeRedBtn.setVisibility(View.INVISIBLE);
            holder.feedsItemLikeWhiteBtn.setVisibility(View.VISIBLE);
        }

        if (currentItem.getProfileLink() != null && !currentItem.getProfileLink().isEmpty()) {
            setProfilePicture(currentItem.getProfileLink(), holder);
        }

    }

    @Override
    public int getItemCount() {
        return feedsItemArrayList.size();
    }

    private void setProfilePicture(String picturePath, @NonNull FeedsHolder holder) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(picturePath);
        final long FIVE_MEGABYTE = 5 * 1024 * 1024;
        ref.getBytes(FIVE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Bitmap scaledImage = Bitmap.createScaledBitmap(bmp, 80, 80, true);
            holder.profilePicture.setImageBitmap(scaledImage);
            holder.profilePicture.setVisibility(View.VISIBLE);

        }).addOnFailureListener(exception -> Toast.makeText(context,
                "Unable to load image as its is more than 5MB in size.",Toast.LENGTH_SHORT).show());
    }
}
