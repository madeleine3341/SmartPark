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
        View v = inflater.inflate(R.layout.fragment_add_parking_spot, container, false);
        iDsEditText = v.findViewById(R.id.parkingSpotsIDTExtView);
        savebtn = v.findViewById(R.id.addIDsbtn);
        cancelbtn = v.findViewById(R.id.cancelbtn);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!iDsEditText.getText().toString().isEmpty()) {
                    String strSpots = iDsEditText.getText().toString();
                    strSpots = strSpots.replace(" ", "");
                    String[] spots = strSpots.split(",");
                    for (int i = 0; i < spots.length; i++) {
                        spots[i] = "Id" + spots[i];
                    }
                    Log.d("TAG", "onClick: " + ParkingSpotsActivity.parkingPath);
                    HashMap<String, Object> hashspot = new HashMap<>();
                    for (String spot :
                            spots) {
                        hashspot.put(spot, true);
                    }
                    FirebaseHelper.addParkingSpot(ParkingSpotsActivity.parkingPath, hashspot);
                    Toast.makeText(getContext(), "Spots added successfuly", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                }
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();

            }
        });
        return v;
    }
}
