package com.team19.smartpark.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.team19.smartpark.AvailableSpotsFragment;
import com.team19.smartpark.MapsActivity;
import com.team19.smartpark.R;
import com.team19.smartpark.models.Parking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class filterListAdapter extends ArrayAdapter<String> {
    private final int mylayout;
    private final Context fcontext;
    private final ArrayList<String> avSpots = new ArrayList<String>();
    private final ArrayList<String> SPOTS = new ArrayList<String>();
    Bundle bundle = new Bundle();
    private ArrayList<Parking> parkingInfo = new ArrayList<Parking>();
    private ArrayList<String> fdistance = new ArrayList<String>();
    private Button directions;
    private Button showspotslist;


    public filterListAdapter(Context context, int resource, ArrayList<Parking> parkingInfo, ArrayList<String> distance) {
        super(context, resource);
        mylayout = resource;
        fcontext = context;
        this.parkingInfo = parkingInfo;
        fdistance = distance;
        for (int i = 0; i < parkingInfo.size(); i++) {
            String avs = "";
            for (Map.Entry<String, Boolean> key : parkingInfo.get(i).spots.entrySet()) {
                if (key.getValue()) {
                    avs = avs + key.getKey() + "\n";
                }
            }
            avSpots.add(avs);
        }
    }

    public int getCount() {
        if (parkingInfo.size() == 0) {
            return 1;
        } else {
            return parkingInfo.size();
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(fcontext);
        convertView = inflater.inflate(mylayout, parent, false);

        directions = convertView.findViewById(R.id.directionsbutton);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MapsActivity) fcontext).DisplayTrack(parkingInfo.get(position).address);

            }
        });

        TextView name = convertView.findViewById(R.id.nameFilterTextView);
        TextView address = convertView.findViewById(R.id.addressFilterTextView);
        TextView distance = convertView.findViewById(R.id.distanceTextView2);
        TextView fees = convertView.findViewById(R.id.FeesFilterTextView);
        TextView distanceINbetween = convertView.findViewById(R.id.distanceTextView2);
        TextView operatingHours = convertView.findViewById(R.id.OperatingHours);
        showspotslist = convertView.findViewById(R.id.availableSpotsList);

        showspotslist.setOnClickListener(new View.OnClickListener() {
            //fragementlist
            @Override
            public void onClick(View v) {
                SPOTS.clear();
                SPOTS.add("Avaliable Spots:");
                // add list of aval. spots
                //set in a bundle
                //send it available spots fragment
                if (parkingInfo.size() > 0 && Collections.frequency(parkingInfo.get(position).spots.values(), true) > 0) {
                    SPOTS.add(avSpots.get(position).replace("Id", ""));
                    bundle.putStringArrayList(fcontext.getString(R.string.AVspots), SPOTS);
                }
                AvailableSpotsFragment fragobj = new AvailableSpotsFragment();
                fragobj.setArguments(bundle);

                FragmentActivity activity = (FragmentActivity) (getContext());
                FragmentManager fm = activity.getSupportFragmentManager();
                fragobj.show(fm, "Spots list");

            }
        });
        TextView noResult = convertView.findViewById(R.id.noResultTextView);
        if (parkingInfo.size() > 0 && Collections.frequency(parkingInfo.get(position).spots.values(), true) > 0) {
            name.setText(parkingInfo.get(position).name);
            fees.setText("Fees: " + parkingInfo.get(position).fees + "$/hr");
            operatingHours.setText("Operating Hours: " + parkingInfo.get(position).getOperatingHour());
            address.setText("Address: " + parkingInfo.get(position).address);
            distance.setText("Distance to your destination: " + fdistance.get(position) + " m");
        } else {
            name.setText("");
            address.setText("");
            distance.setText("");
            operatingHours.setText("");
            fees.setText("");
            showspotslist.setVisibility(View.GONE);
            directions.setVisibility(View.GONE);
            noResult.setText("No Available Parking Lot");
        }
        return convertView;
    }
}


