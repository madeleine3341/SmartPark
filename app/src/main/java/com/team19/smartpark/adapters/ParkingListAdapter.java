package com.team19.smartpark.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.team19.smartpark.ParkingSpotsActivity;
import com.team19.smartpark.R;
import com.team19.smartpark.models.Parking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ParkingListAdapter extends RecyclerView.Adapter<ParkingListAdapter.ViewHolder> {
    private final ArrayList<String> mParkingListName = new ArrayList<>();
    private final TreeMap<String, Parking> mKeys;
    LayoutInflater inflater;
    ArrayList<HashMap<String, Boolean>> spots = new ArrayList<>();
    ArrayList<String> id = new ArrayList<>();
    Context ctx;

    public ParkingListAdapter(TreeMap<String, Parking> Keyparking, Context ctx) {

        this.mKeys = Keyparking;
        SetArrays(mKeys);
        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    public void SetArrays(TreeMap<String, Parking> Keyparking) {

        for (Map.Entry<String, Parking> parking : Keyparking.entrySet()) {
            mParkingListName.add(parking.getValue().getName());
            spots.add(parking.getValue().getSpots());//get one spot
            id.add(parking.getKey());
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.list_items, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int total = 0, available = 0, unavailable = 0;
        boolean a = false;
        holder.parkingName.setText(mParkingListName.get(position));
        if (spots.get(position) != null) {
            for (Map.Entry<String, Boolean> entry : spots.get(position).entrySet()) {
                a = entry.getValue();

                if (a) {
                    available++;
                } else {
                    unavailable++;
                }
                total = available + unavailable;
            }
        }
        holder.parkingStatus.setText(available + "/" + total + " available");
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ctx, ParkingSpotsActivity.class);
                intent.putExtra("parkingId", id.get(position));
                ctx.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mParkingListName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView parkingName;
        TextView parkingStatus;
        ConstraintLayout card;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parkingName = itemView.findViewById(R.id.name_textView);
            parkingStatus = itemView.findViewById(R.id.status_textView);
            card = itemView.findViewById(R.id.Card);

        }
    }

}
