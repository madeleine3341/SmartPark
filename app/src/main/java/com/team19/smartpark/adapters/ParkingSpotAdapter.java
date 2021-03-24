package com.team19.smartpark.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team19.smartpark.ParkingSpotsActivity;
import com.team19.smartpark.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ParkingSpotAdapter extends RecyclerView.Adapter<ParkingSpotAdapter.ViewHolder> {
    ArrayList<Map.Entry<String, Boolean>> spots;
    LayoutInflater inflater;
    FragmentManager fragmentManager;

    public ParkingSpotAdapter(Context ctx, TreeMap<String, Boolean> spots, FragmentManager fragmentManager) {
        this.spots = new ArrayList<Map.Entry<String, Boolean>>(spots.entrySet());
        this.inflater = LayoutInflater.from(ctx);
        this.fragmentManager = fragmentManager;
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
        holder.parkingSpotId.setText(spot.getKey());
        if (spot.getValue()) {
            holder.cardView.setCardBackgroundColor(Color.GREEN);

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

    }

    @Override
    public int getItemCount() {
        return spots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView parkingSpotId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.parkingCard);
            parkingSpotId = itemView.findViewById(R.id.parkingSpotId);
            String parkingPath = ParkingSpotsActivity.parkingPath;

        }
    }
}
