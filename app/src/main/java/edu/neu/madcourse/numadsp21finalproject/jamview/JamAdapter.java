package edu.neu.madcourse.numadsp21finalproject.jamview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.madcourse.numadsp21finalproject.R;
import edu.neu.madcourse.numadsp21finalproject.utils.Helper;

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

    public void deleteItem(int position) {
        JamItem jamItem = jamItems.get(position);
        String groupName = jamItem.getGroupName();
        HashMap<String, String> friendsList = jamItem.getFriendMap();
        WriteBatch writeBatch = Helper.db.batch();
        for (String groupMember : friendsList.keySet()) {
            writeBatch.delete(Helper.db.collection("jamGroups")
                    .document(groupMember).collection("groups")
                    .document(groupName));
        }
        writeBatch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, groupName + " is deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Some error occurred. Group could not be deleted",
                        Toast.LENGTH_SHORT).show();
            }
        });
        

    }
}
