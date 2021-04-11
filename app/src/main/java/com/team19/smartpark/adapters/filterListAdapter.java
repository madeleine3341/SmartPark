package com.team19.smartpark.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.team19.smartpark.AvailableSpotsFragment;
import com.team19.smartpark.MapsActivity;
import com.team19.smartpark.ParkingInfoActivity;
import com.team19.smartpark.ParkingSpotsActivity;
import com.team19.smartpark.R;
import com.team19.smartpark.models.Parking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class filterListAdapter extends ArrayAdapter<String> {
    private int mylayout;
    private Context fcontext;
    private ArrayList<Parking> parkingInfo = new ArrayList<Parking>();
    private ArrayList<String> fdistance = new ArrayList<String>();
    private ArrayList<String> avSpots = new ArrayList<String>();
    private ArrayList<String> SPOTS = new ArrayList<String>();
    private Button directions;
    private Button showspotslist;
    Bundle bundle = new Bundle();


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
                    avs = avs + key.getKey() + "\n";
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
        TextView textView1 = (TextView) convertView.findViewById(R.id.distanceTextView);
        TextView availablespots = (TextView) convertView.findViewById(R.id.availableSpotTextView2);
        TextView numberavailablespots = (TextView) convertView.findViewById(R.id.availableSpotTextView);
        showspotslist= (Button) convertView.findViewById(R.id.availableSpotsList);


        if(parkingInfo.size() > 0 && Collections.frequency(parkingInfo.get(position).spots.values(), true) > 0) {
            name.setText(parkingInfo.get(position).name);
            address.setText(parkingInfo.get(position).address);
            fees.setText("Fees: " + Double.toString(parkingInfo.get(position).fees) + "$");

            status.setText("Status: ");
            distance.setText(fdistance.get(position) + " m");
            availablespots.setText(avSpots.get(0));

            distance.setText(fdistance.get(position) + " m");
            numberavailablespots.setText(avSpots.size() + " available spots");
        }

        showspotslist.setOnClickListener(new View.OnClickListener() {
        //fragementlist
            @Override
            public void onClick(View v) {
//
                SPOTS.clear();
                if(parkingInfo.size() > 0 && Collections.frequency(parkingInfo.get(position).spots.values(), true) > 0) {
                    SPOTS.add(avSpots.get(position));
                    bundle.putStringArrayList(fcontext.getString(R.string.AVspots),  SPOTS);
                }
                AvailableSpotsFragment fragobj = new AvailableSpotsFragment();
                fragobj.setArguments(bundle);

                FragmentActivity activity=(FragmentActivity)(getContext());
                FragmentManager fm= activity.getSupportFragmentManager();
                fragobj.show(fm, "Spots list");

            }
        });
        TextView textView2 = (TextView) convertView.findViewById(R.id.availableSpotTextView);
        TextView noResult = (TextView) convertView.findViewById(R.id.noResultTextView);
        if(parkingInfo.size() > 0 && Collections.frequency(parkingInfo.get(position).spots.values(), true) > 0) {
            name.setText(parkingInfo.get(position).name);
            address.setText(parkingInfo.get(position).address);
            String stat = null;
            stat = "Available";
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

