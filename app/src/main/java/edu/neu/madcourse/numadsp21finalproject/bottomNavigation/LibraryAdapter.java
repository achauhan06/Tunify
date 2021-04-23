package edu.neu.madcourse.numadsp21finalproject.bottomNavigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.rpc.Help;

import java.util.ArrayList;
import java.util.zip.Inflater;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryHolder> {
    private ArrayList<LibraryItem> libraryItemArrayList;
    private PlaylistListener libraryViewClickListener;
    private Context context;

    public LibraryAdapter(ArrayList<LibraryItem> libraryItemArrayList,
                          PlaylistListener libraryViewClickListener,
                          Context context) {
        this.libraryItemArrayList = libraryItemArrayList;
        this.libraryViewClickListener = libraryViewClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public LibraryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent, false);
        return new LibraryHolder(view, context, libraryViewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryHolder holder, int position) {
        LibraryItem currentItem = libraryItemArrayList.get(position);
        holder.libraryItemName.setText(currentItem.getProjectName());
        holder.libraryItemGenre.setText(currentItem.getGenre());
        holder.libraryItemTime.setText(currentItem.getTimeString());
        holder.libraryLikeCount.setText(String.valueOf(currentItem.getLikeCount()));
        holder.libraryCommentCount.setText(String.valueOf(currentItem.getCommentCount()));
        if(currentItem.isLikedByMe()) {
            holder.libraryLikeRed.setVisibility(View.VISIBLE);
            holder.libraryLikeWhite.setVisibility(View.INVISIBLE);
        }else {
            holder.libraryLikeRed.setVisibility(View.INVISIBLE);
            holder.libraryLikeWhite.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return libraryItemArrayList.size();
    }

    public void deleteItem(int position) {
        String recordingId = libraryItemArrayList.get(position).getRecordingId();
        Helper.db.collection("recordings")
                .document(recordingId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                libraryItemArrayList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Recording is deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Some error occurred. Recording could not be deleted",
                        Toast.LENGTH_SHORT).show();
            }
        });



    }
}
