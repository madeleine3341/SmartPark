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
        ParkingSpotAdapter.spots = new ArrayList<Map.Entry<String, Boolean>>(spots.entrySet());
        this.inflater = LayoutInflater.from(ctx);
        this.fragmentManager = fragmentManager;
        this.context = ctx;
        this.deleteMode = deleteMode;
        fAuth = FirebaseAuth.getInstance();
        flag = spots.size() == 1;
    }

    public static void clearData() {
        spots.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.parking_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, Boolean> spot = spots.get(position);
        holder.parkingSpotId.setText(spot.getKey().replace("Id", ""));
        if (spot.getValue()) {
            holder.cardView.setCardBackgroundColor(Color.GREEN);
        } else if (spot.getKey() == null) {
            holder.cardView.removeAllViews();
        } else {

            holder.cardView.setCardBackgroundColor(Color.RED);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: " + spot.getKey());
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                Bundle bundle = new Bundle();
                bundle.putString("spot", spot.getKey());
                bundle.putString("parent", ParkingSpotsActivity.parkingPath);
                bottomSheet.setArguments(bundle);
                bottomSheet.show(fragmentManager, "bottomsheet");
            }
        });
//        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                DatabaseReference mSpot = FirebaseDatabase.getInstance().getReference("/" + ParkingSpotsActivity.parkingPath + "/spots");
//                mSpot.child(spot.getKey()).removeValue(new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                        Toast.makeText(context, "Spot " + spot.getKey() + " deleted", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                return false;
//            }
//        });
        if (deleteMode) {
            Log.i("tag", "deletemode run");
            holder.deleteCross.setVisibility(View.VISIBLE);
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
            super(itemView);
            cardView = itemView.findViewById(R.id.parkingCard);
            parkingSpotId = itemView.findViewById(R.id.parkingSpotId);
            String parkingPath = ParkingSpotsActivity.parkingPath;
            deleteCross = itemView.findViewById(R.id.deleteBtn);

        }
    }
}
