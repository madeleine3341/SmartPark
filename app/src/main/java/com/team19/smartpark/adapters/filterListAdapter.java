package com.team19.smartpark.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private ArrayList<String> ffees = new ArrayList<String>();
    private Button directions;



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
        return parkingInfo.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(fcontext);
        convertView = inflater.inflate(mylayout,parent,false);
        directions= (Button) convertView.findViewById(R.id.directionsbutton);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MapsActivity)fcontext).DisplayTrack(parkingInfo.get(position).address);

            }
        });


        TextView name = (TextView) convertView.findViewById(R.id.nameFilterTextView);
        TextView address = (TextView) convertView.findViewById(R.id.addressFilterTextView);
        TextView status = (TextView) convertView.findViewById(R.id.statusFilterTextView);
        TextView distance = (TextView) convertView.findViewById(R.id.distanceTextView2);
        TextView fees = (TextView) convertView.findViewById(R.id.FeesFilterTextView);
        TextView availablespots = (TextView) convertView.findViewById(R.id.availableSpotTextView2);

        name.setText(parkingInfo.get(position).name);
        address.setText(parkingInfo.get(position).address);


        String stat = null;
        if(Collections.frequency(parkingInfo.get(position).spots.values(), true) > 0){
            stat = "Available";
        }
        else{
            stat = "Not Available";
        }
        status.setText("Status: " + stat);
        distance.setText(fdistance.get(position) + " m");
        availablespots.setText(avSpots.get(position));
        return convertView;
    }

}
