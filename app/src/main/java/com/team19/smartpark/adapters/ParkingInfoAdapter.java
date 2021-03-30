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

import com.team19.smartpark.ParkingInfoActivity;
import com.team19.smartpark.R;
import com.team19.smartpark.models.Parking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParkingInfoAdapter extends RecyclerView.Adapter<ParkingInfoAdapter.ViewHolder> {
    LayoutInflater inflater;
    Parking parking;
    String[] info = new String[4];
    ArrayList<String> infoType = new ArrayList<>();
    Context ctx;

    public ParkingInfoAdapter(Parking inParking, Context ctx) {
        this.parking = inParking;
        SetArrays();
        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    public void SetArrays() {

        for (int i = 0; i < 4; i++) {
            if (i == 0){
                infoType.add("Address:");
                info[0] = parking.getAddress();
            }else if(i == 1){
                infoType.add("Coordinates:");
                info[1] = parking.getLat() + "," + parking.getLng();
            }else if(i == 2){
                infoType.add("Recommended:");
                boolean availableExists = false;
                Iterator it = parking.getSpots().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if ((Boolean)pair.getValue()){
                        info[2] = (String)pair.getKey();
                        availableExists = true;
                        break;
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                    if (!availableExists){
                        info[2] =" ";
                    }
                }
            }else if(i == 3) {
                infoType.add("Status:");
                if (parking.isStatus()){
                    info[3] ="open";
                }else{
                    info[3] = "closed";
                }


            }
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

        if (position <= 3){
            String information = infoType.get(position) + " " + info[position];
            holder.parkingName.setText(information);
            if (position == 0){
                holder.parkingStatus.setText(" ");
            }else if (position == 1){
                holder.parkingStatus.setText(" ");
            }else if (position == 2){
                holder.parkingStatus.setText("View All Spots ->");
            }else if (position == 3){
                holder.parkingStatus.setText("View Operating Hours ->");
            }
        }




//        **CLICK ON RECOMMENDED SPOT TO SHOW ALL SPACES (eventually to pay, opening hours, etc.)
//        holder.card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(ctx, ParkingSpotsActivity.class);
//                intent.putExtra("parkingId", id.get(position));
//                ctx.startActivity(intent);
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return 4;
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
