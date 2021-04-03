package com.team19.smartpark.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.team19.smartpark.MapsActivity;
import com.team19.smartpark.R;
import com.team19.smartpark.models.Parking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class filterListAdapter extends ArrayAdapter<String> {
    private int mylayout;
    private Context fcontext;
    private ArrayList<Parking> parkingInfo = new ArrayList<Parking>();
    private ArrayList<String> fdistance = new ArrayList<String>();
    private ArrayList<String> avSpots = new ArrayList<String>();

    public filterListAdapter(Context context, int resource, ArrayList<Parking> parkingInfo, ArrayList<String> distance) {
        super(context, resource);
        mylayout = resource;
        fcontext = context;
        this.parkingInfo = parkingInfo;
        fdistance = distance;
        for(int i =0;i<parkingInfo.size();i++) {
            String avs = "";
            for(Map.Entry<String,Boolean> key: parkingInfo.get(i).spots.entrySet()){
                if(key.getValue()){
                    avs = avs + key.getKey() + " ";
                }
            }
            avSpots.add(avs);
        }
    }
    public int getCount() {
        if(parkingInfo.size() == 0){
            return 1;
        }
        else {
            return parkingInfo.size();
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(fcontext);
        convertView = inflater.inflate(mylayout,parent,false);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fcontext, parkingInfo.get(position).name, Toast.LENGTH_SHORT).show();
                LatLng latLng = new LatLng(parkingInfo.get(position).lat,parkingInfo.get(position).lng);
                ((MapsActivity)fcontext).updateview(latLng);
            }
        });
        TextView name = (TextView) convertView.findViewById(R.id.nameFilterTextView);
        TextView address = (TextView) convertView.findViewById(R.id.addressFilterTextView);
        TextView status = (TextView) convertView.findViewById(R.id.statusFilterTextView);
        TextView distance = (TextView) convertView.findViewById(R.id.distanceTextView2);
        TextView textView1 = (TextView) convertView.findViewById(R.id.distanceTextView);
        TextView availablespots = (TextView) convertView.findViewById(R.id.availableSpotTextView2);
        TextView textView2 = (TextView) convertView.findViewById(R.id.availableSpotTextView);
        TextView noResult = (TextView) convertView.findViewById(R.id.noResultTextView);
        if(parkingInfo.size() > 0) {
            name.setText(parkingInfo.get(position).name);
            address.setText(parkingInfo.get(position).address);
            String stat = null;
            if (Collections.frequency(parkingInfo.get(position).spots.values(), true) > 0) {
                stat = "Available";
            } else {
                stat = "Not Available";
            }
            status.setText("Status: " + stat);
            distance.setText(fdistance.get(position) + " m");
            availablespots.setText(avSpots.get(position));
        }
        else{
            name.setText("");
            address.setText("");
            status.setText("");
            distance.setText("");
            availablespots.setText("");
            textView1.setText("");
            textView2.setText("");
            noResult.setText("No Available Parking Lot");
        }
        return convertView;
    }
}
