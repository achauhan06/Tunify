package edu.neu.madcourse.numadsp21finalproject.searchView;

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
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendItem;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendViewClickListener;
import edu.neu.madcourse.numadsp21finalproject.bottomNavigation.FriendsHolder;

public class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {
    private final ArrayList<SearchItem> searchItems;
    private final SearchViewListener searchViewListener;
    private final Context context;

    public SearchAdapter(ArrayList<SearchItem> searchItems,
                          SearchViewListener searchViewListener,
                          Context context) {
        this.searchItems = searchItems;
        this.searchViewListener = searchViewListener;
        this.context = context;
    }
    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        return new SearchHolder(view, context, searchViewListener);
    }



    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        SearchItem currentItem = searchItems.get(position);
        holder.searchName.setText(currentItem.getSearchName());
        holder.searchCategory.setText(currentItem.getGenres());
        if (currentItem.getProfileLink() != null && !currentItem.getProfileLink().isEmpty()) {
            setProfilePicture(currentItem.getProfileLink(), holder);
        }
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    private void setProfilePicture(String picturePath, @NonNull SearchHolder holder) {
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
