package com.team19.smartpark;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team19.smartpark.adapters.ParkingListAdapter;
import com.team19.smartpark.adapters.filterListAdapter;
import com.team19.smartpark.models.Parking;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AvailableSpotsFragment extends DialogFragment {

   ListView spotsList;
   Button back;


   public AvailableSpotsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_available_spots, container, false);
        spotsList=view.findViewById(R.id.list);
        back=view.findViewById(R.id.exit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

       ArrayList<String>  spots=getArguments().getStringArrayList(getActivity().getString(R.string.AVspots));
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, spots);
        spotsList.setAdapter(itemsAdapter);

        return view;
    }
}