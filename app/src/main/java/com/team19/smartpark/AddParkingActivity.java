package com.team19.smartpark;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.team19.smartpark.models.FirebaseHelper;
import com.team19.smartpark.models.Parking;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddParkingActivity extends AppCompatActivity {

    final String TAG = "AddParkingActivity";
    private Button saveBtn;
    private EditText nameTextView;
    private EditText addressTextView;
    private EditText latTextView;
    private EditText lngTextView;
    private EditText spotsTextView;
    private EditText feesTextView;
    private TextView openTextView;
    private TextView closeTextView;

    private int t1Hour,t1Minute,t2Hour,t2Minute;
    private boolean t1State,t2State;

    private FloatingActionButton actionBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        saveBtn = findViewById(R.id.saveBtn);
        nameTextView = findViewById(R.id.nameTextView);
        addressTextView = findViewById(R.id.addressTextView);
        latTextView = findViewById(R.id.latTextView);
        lngTextView = findViewById(R.id.lngTextView);
        spotsTextView = findViewById(R.id.spotsTextView);
        feesTextView =findViewById(R.id.feesTextView);
        openTextView = findViewById(R.id.openTextView);
        closeTextView = findViewById(R.id.closeTextView);

        String udata="Select Opening Hour";
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        openTextView.setHint(content);

        udata="Select Closing Hour";
        content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        closeTextView.setHint(content);

        openTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDiaLog("open");
            }
        });
        closeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDiaLog("close");
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameTextView.getText().toString();
                String address = addressTextView.getText().toString();
                String strSpots = spotsTextView.getText().toString();
                strSpots = strSpots.replace(" ", "");
                String[] spots = strSpots.split(",");
                String operatingHour = t1Hour + ":" +t1Minute +"-"+t2Hour+":"+t2Minute;
                String fee=feesTextView.getText().toString();
                double fees = Double.parseDouble(fee);
                HashMap<String, Boolean> hashspot = new HashMap<>();
                for (String spot :
                        spots) {
                    hashspot.put(spot, true);
                }


                double lat = 0.0;
                double lng = 0.0;
                try {
                    lat = Double.parseDouble(latTextView.getText().toString());
                    lng = Double.parseDouble(lngTextView.getText().toString());

                } catch (NumberFormatException ignored) {

                }

                if (!name.isEmpty() && !address.isEmpty() && lat != 0.0 && lng != 0.0 && (t1State && t2State) &&!fee.isEmpty()) {
                    Parking parking = new Parking();
                    parking.address = address;
                    parking.lat = lat;
                    parking.lng = lng;
                    parking.name = name;
                    parking.operatingHour = operatingHour;
                    parking.spots = hashspot;
                    parking.fees=fees;
                    FirebaseHelper.addParkingLots(parking);
                    Toast.makeText(getApplicationContext(), "Parking added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });


    }
    private void timeDiaLog(String text){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                AddParkingActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = null;
                        if(text == "open") {
                            t1Hour = hourOfDay;
                            t1Minute = minute;
                            time = t1Hour+ ":" + t1Minute;
                        }
                        else{
                            t2Hour = hourOfDay;
                            t2Minute = minute;
                            time = t2Hour+ ":" + t2Minute;
                        }
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);
                            if(text == "open") {
                                if(timeSelectionGuard() && t2State){
                                    String udata = "Select Opening Hour";
                                    SpannableString content = new SpannableString(udata);
                                    content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
                                    openTextView.setText(content);
                                    t1State = false;
                                    Toast.makeText(AddParkingActivity.this,"Please Enter a Valid Opening Hour",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                String sourceString = "<b>"+"Opening Hour: "+"</b>"+ f24Hours.format(date);
                                openTextView.setText(Html.fromHtml(sourceString, Html.FROM_HTML_MODE_LEGACY));
                            }
                            else{
                                if(timeSelectionGuard() && t1State){
                                    String udata = "Select Closing Hour";
                                    SpannableString content = new SpannableString(udata);
                                    content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
                                    closeTextView.setText(content);
                                    t2State = false;
                                    Toast.makeText(AddParkingActivity.this,"Please Enter a Valid Closing Hour",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                String sourceString = "<b>"+"Closing Hour: "+"</b>" + f24Hours.format(date);
                                closeTextView.setText(Html.fromHtml(sourceString, Html.FROM_HTML_MODE_LEGACY));
                            }
                        } catch (ParseException e){
                            e.printStackTrace();
                        }
                        if(text == "open") {
                            t1State = true;
                        }
                        else{
                            t2State = true;
                        }
                    }
                },24,0,true
        );
        if(t1State && text == "open"){
            timePickerDialog.updateTime(t1Hour, t1Minute);
        }
        else{
            timePickerDialog.updateTime(t2Hour, t2Minute);
        }
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();
    }
    private boolean timeSelectionGuard() {
        if(t1Hour > t2Hour ) {
            return true;
        }
        else if(t1Hour == t2Hour && t1Minute >= t2Minute){
            return true;
        }
        return false;
    }
}
