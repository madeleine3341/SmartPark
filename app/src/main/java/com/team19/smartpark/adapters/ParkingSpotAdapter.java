package com.team19.smartpark.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team19.smartpark.ParkingListActivity;
import com.team19.smartpark.ParkingSpotsActivity;
import com.team19.smartpark.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ParkingSpotAdapter extends RecyclerView.Adapter<ParkingSpotAdapter.ViewHolder> {
    static ArrayList<Map.Entry<String, Boolean>> spots;
    LayoutInflater inflater;
    FragmentManager fragmentManager;
    Context context;
    Boolean deleteMode;
    private final FirebaseAuth fAuth;
    private final Boolean flag;

    public ParkingSpotAdapter(Context ctx, TreeMap<String, Boolean> spots, FragmentManager fragmentManager, Boolean deleteMode) {
        // bind the adapter fields to the constructor parameters
        ParkingSpotAdapter.spots = new ArrayList<Map.Entry<String, Boolean>>(spots.entrySet());
        this.inflater = LayoutInflater.from(ctx);
        this.fragmentManager = fragmentManager;
        this.context = ctx;
        this.deleteMode = deleteMode;
        fAuth = FirebaseAuth.getInstance();
        flag = spots.size() == 1;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind view to layout xml file of the parking card
        View view = inflater.inflate(R.layout.parking_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // set the general behaviour and content of each individual card
        Map.Entry<String, Boolean> spot = spots.get(position);
        holder.parkingSpotId.setText(spot.getKey().replace("Id", ""));
        // set the color of the card based on the spot's status
        // if status is available
        if (spot.getValue()) {
            // set color card to green
            holder.cardView.setCardBackgroundColor(Color.GREEN);
        } else {
            // set color card to red
            holder.cardView.setCardBackgroundColor(Color.RED);
        }
        // set the onclick behaviour of the card
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show the bottom sheet options and pass the id of the parking and the selected spot
                // for the bottom sheet options to use (ie. linking)
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                Bundle bundle = new Bundle();
                bundle.putString("spot", spot.getKey());
                bundle.putString("parent", ParkingSpotsActivity.parkingPath);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(fragmentManager, "bottomsheet");
            }
        });
        // if adapter is loaded in delete mode
        if (deleteMode) {
            //show the delete cross at the top right of each card so user can delete the parking spot
            holder.deleteCross.setVisibility(View.VISIBLE);
            // on clicking the cross the spot is removed
            holder.deleteCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mSpot = FirebaseDatabase.getInstance().getReference(fAuth.getCurrentUser().getUid() + "/parkingLots/" + ParkingSpotsActivity.parkingPath + "/spots");
                    mSpot.child(spot.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (flag) {
                                context.startActivity(new Intent(context, ParkingListActivity.class));
                                ((ParkingSpotsActivity) context).finish();
                            }
                            Toast.makeText(context, "Spot " + spot.getKey() + " deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            holder.deleteCross.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return spots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView parkingSpotId;
        ImageView deleteCross;

        public ViewHolder(@NonNull View itemView) {
            // bind view holder items
            super(itemView);
            cardView = itemView.findViewById(R.id.parkingCard);
            parkingSpotId = itemView.findViewById(R.id.parkingSpotId);
            String parkingPath = ParkingSpotsActivity.parkingPath;
            deleteCross = itemView.findViewById(R.id.deleteBtn);

        }
    }
}
