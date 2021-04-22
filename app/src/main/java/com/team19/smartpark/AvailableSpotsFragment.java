package com.team19.smartpark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class AvailableSpotsFragment extends DialogFragment {

    ListView spotsList;
    Button back;


    public AvailableSpotsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_available_spots, container, false);
        spotsList = view.findViewById(R.id.list);
        back = view.findViewById(R.id.exit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // get aval. spots from bundle
        ArrayList<String> spots = getArguments().getStringArrayList(getActivity().getString(R.string.AVspots));
        //set adapter
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, spots);
        spotsList.setAdapter(itemsAdapter);

        return view;
    }
}