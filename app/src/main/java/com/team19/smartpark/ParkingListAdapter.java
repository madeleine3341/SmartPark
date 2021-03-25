package com.team19.smartpark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class ParkingListAdapter extends RecyclerView.Adapter<ParkingListAdapter.ViewHolder> {
    private ArrayList<String>mParkingListName;
    ArrayList<HashMap<String, Boolean>> spots;
    //ArrayList<Map.Entry<String, Boolean>> spots;

    private List<String> mKeys;
    LayoutInflater inflater;

    public ParkingListAdapter(ArrayList<String> mParkingListName, Context ctx, ArrayList<HashMap<String, Boolean>> spots) { //TreeMap<String, Boolean> spots
        this.mParkingListName = mParkingListName;
//        this.spots = new ArrayList<Map.Entry<String, Boolean>>(spots.entrySet());
       this.spots =  spots;
        //.entrySet());

        this.inflater = LayoutInflater.from(ctx);
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
        boolean a=false;

        holder.parkingName.setText(mParkingListName.get(position));

       //  Set s=  spots.get(position).entrySet(); //returns one spot set<string,bool>
        //convert spots to mapentry
       // Map<String,Boolean> m=new LinkedHashMap<String,Boolean>();}
       for(Map.Entry<String,Boolean> entry: spots.get(position).entrySet()) {
           a = entry.getValue();

           if (a) {
               available++;
           } else {
               unavailable++;
           }
           total = available + unavailable;
       }
            holder.parkingStatus.setText(available + "/" + total + " available");
       }


    @Override
    public int getItemCount() {
       // return 2;
        return mParkingListName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView parkingName;
        TextView parkingStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parkingName = itemView.findViewById(R.id.name_textView);
            parkingStatus = itemView.findViewById(R.id.status_textView);


        }
    }

}
