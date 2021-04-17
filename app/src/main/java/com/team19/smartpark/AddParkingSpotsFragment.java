package com.team19.smartpark;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.team19.smartpark.models.FirebaseHelper;

import java.util.HashMap;

public class AddParkingSpotsFragment extends DialogFragment {

    protected EditText iDsEditText;
    protected Button savebtn;
    protected Button cancelbtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // bind view and its layout elements
        View v = inflater.inflate(R.layout.fragment_add_parking_spot, container, false);
        iDsEditText = v.findViewById(R.id.parkingSpotsIDTExtView);
        savebtn = v.findViewById(R.id.addIDsbtn);
        cancelbtn = v.findViewById(R.id.cancelbtn);
        // save the spot when the save button is clicked
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //valide if the entry field is not null
                if (!iDsEditText.getText().toString().isEmpty()) {
                    //process user entry to parse different parking spots entered delimited with a comma
                    String strSpots = iDsEditText.getText().toString();
                    strSpots = strSpots.replace(" ", "");
                    String[] spots = strSpots.split(",");
                    for (int i = 0; i < spots.length; i++) {
                        spots[i] = "Id" + spots[i]; // "Id" is added as safeguard to a known bug in Firebase
                    }
                    HashMap<String, Object> hashspot = new HashMap<>();
                    // for each spot in the string array put them in the hashmap
                    for (String spot :
                            spots) {
                        hashspot.put(spot, true);
                    }
                    //push the hashmap to the Fireabse database
                    FirebaseHelper.addParkingSpot(ParkingSpotsActivity.parkingPath, hashspot);
                    Toast.makeText(getContext(), "Spots added successfuly", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
            }
        });

        // dismiss the frfagment when the cancel button is clicked
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();

            }
        });
        return v;
    }
}
