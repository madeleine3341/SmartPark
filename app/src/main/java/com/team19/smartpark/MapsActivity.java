package com.team19.smartpark;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team19.smartpark.adapters.filterListAdapter;
import com.team19.smartpark.models.Parking;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final Map<String, Marker> mMarkerMap = new HashMap<>();
    private LinkedHashMap<String, Parking> parkingsList = new LinkedHashMap<String, Parking>();
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private DatabaseReference mDatabase;
    private GoogleMap mMap;
    private SearchView searchView;
    private ImageView closeButton;

    private Context context;
    private LinearLayout linearLayout;
    private ListView filterListView;
    private LinearLayoutManager llm;
    private Button nearbyButton, clearButton;
    private ToggleButton sASButton,sFeesButton,openButton;
    private Spinner sDistanceButton;
    private TextView textView;
    private LatLng cameraLatLng;
    private BottomSheetBehavior mbottomSheetBehavior;
    private int spinnerPosition;
    private HorizontalScrollView horizontalScrollView;
    private Circle myCircle;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    //ArrayList to hold all parking objects
    private LinkedHashMap<String, Parking> parkings;
    private FloatingActionButton fab;
    private FloatingActionButton myLocationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        filterUISetup();
        fab = findViewById(R.id.floatingActionButton);
        myLocationButton = findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ParkingListActivity.class);
                startActivity(intent);
            }
        });
        try {
            searchView = findViewById(R.id.search_bar);
            int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView textView = searchView.findViewById(id);
            textView.setTextColor(Color.BLACK);


        } catch (NullPointerException ignore) {

        }
        searchView.onWindowFocusChanged(false);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilterUI(false);
                nearbyButton.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                cameraLatLng = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                updateFilterUI(false);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = null;
                    try {
                        address = addressList.get(0);

                    } catch (IndexOutOfBoundsException ignore) {

                    }
                    if (address != null) {
                        nearbyButton.setVisibility(View.VISIBLE);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        cameraLatLng = latLng;
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    } else {
                        Toast.makeText(getApplicationContext(), "Adress not found", Toast.LENGTH_SHORT).show();
                    }
                    searchView.clearFocus();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateFilterUI(false);
                nearbyButton.setVisibility(View.GONE);
                return false;
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

// ...

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        setMarkers();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                Intent intent = new Intent(getApplicationContext(), ParkingInfoActivity.class);
                String reference = arg0.getTitle();
                intent.putExtra("reference", reference);

                // Starting the  Activity
                startActivity(intent);
            }
        });

    }

    private void setMarkers() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> parkings = dataSnapshot.getChildren();
                //array list to hold all parking lots
                for (DataSnapshot parking :
                        parkings) {
                    parking.getKey();
                    //put all parking lots in the array list
                    parkingsList.put(parking.getKey(), parking.getValue(Parking.class));
                }
                //for each parking create a marker and populate it with its stats, if marker already created, just updated
                for (Map.Entry<String, Parking> parkingSet :
                        parkingsList.entrySet()) {
                    Parking parking = parkingSet.getValue();
                    int count = parking.spots.size();
                    int available = Collections.frequency(parking.spots.values(), true);
                    String parkingId = parkingSet.getKey();
                    Marker previousMarker = mMarkerMap.get(parkingId);
                    // if previous marker already exists just update its availability
                    if (previousMarker != null) {
                        Log.d(TAG, "onDataChange: previous marker exists, update availability");
                        previousMarker.setSnippet(available + "/" + count + " available");
                        if (previousMarker.isInfoWindowShown()) {
                            previousMarker.hideInfoWindow();
                            previousMarker.showInfoWindow();
                        }
                    }
                    // else its a new marker to be added (which means a new parking lot has been added)
                    else {
                        Log.d(TAG, "onDataChange: create new marker");
                        LatLng parkingLocation = new LatLng(parking.lat, parking.lng);
                        MarkerOptions parkingMarker = new MarkerOptions().position(parkingLocation).title(parking.name).snippet(available + "/" + count + " available");
//                    parkingMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_icon));
                        //put the new marker on the map to display it
                        Marker marker = mMap.addMarker(parkingMarker);
                        // add the market to the hashmap to remember it next time (keep track of it)
                        mMarkerMap.put(parking.name, marker);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(true);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.getTitle();
                        marker.getSnippet();
                        return false;
                    }
                });
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                cameraLatLng = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void filterUISetup(){
        horizontalScrollView = findViewById(R.id.scrollViewFilter);
        linearLayout = (LinearLayout) findViewById(R.id.bottomSheet);
        filterListView = (ListView) findViewById(R.id.filterListView);
        mbottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        nearbyButton = findViewById(R.id.nearbyButton);
        textView = findViewById(R.id.sortTextView);
        sDistanceButton = findViewById(R.id.sortDistanceButton);
        sASButton = findViewById(R.id.sortASButton);
        clearButton = findViewById(R.id.clearButton);
        openButton = findViewById(R.id.openButton);
        sFeesButton = findViewById(R.id.sortFeesButton);
        updateFilterUI(false);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.string, R.layout.spinner_custom_drop_down_menu);
        adapter.setDropDownViewResource(R.layout.spinner_custom_drop_down_menu);
        sDistanceButton.setAdapter(adapter);
        sDistanceButton.setOnItemSelectedListener(this);
        sASButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sASButton.isChecked() && spinnerPosition == 0 && !openButton.isChecked() && !sFeesButton.isChecked()) {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else if(!sASButton.isChecked()){
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sortAlgorithm();
                }
                else{
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(235,236,246)));
                    sortAlgorithm();
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sASButton.isChecked() && spinnerPosition == 0 && !openButton.isChecked()) {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else if(!openButton.isChecked()){
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sortAlgorithm();
                }
                else{
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(235,236,246)));
                    sortAlgorithm();
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        sFeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sASButton.isChecked() && spinnerPosition == 0 && !openButton.isChecked() && !sFeesButton.isChecked()) {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                else if(!sFeesButton.isChecked()){
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sortAlgorithm();
                }
                else{
                    sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(235,236,246)));
                    sortAlgorithm();
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        nearbyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilterUI(true);
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myCircle!= null) {
                    myCircle.remove();
                    myCircle = null;
                }
                updateFilterUI(false);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                sDistanceButton.setSelection(0);
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerPosition = position;
        if(parent.getItemAtPosition(position) != null && position != 0) {
            String text = parent.getItemAtPosition(position).toString();
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            sortAlgorithm();
            mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        else if(position == 0 && !sASButton.isChecked() && !sFeesButton.isChecked() && !openButton.isChecked()){
            if(myCircle!= null) {
                myCircle.remove();
                myCircle = null;
            }
            mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void updateFilterUI(Boolean state){
        if(state){
            nearbyButton.setVisibility(View.GONE);
            horizontalScrollView.setVisibility(View.VISIBLE);
            clearButton.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

        }
        else{
            mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            nearbyButton.setVisibility(View.VISIBLE);
            horizontalScrollView.setVisibility(View.GONE);
            clearButton.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            sASButton.setChecked(false);
            openButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            openButton.setChecked(false);
            sFeesButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            sFeesButton.setChecked(false);
        }
    }
    private void sortAlgorithm() {
        //Create two float to store distance between two locations
        float result1[] = new float[1];
        float result2[] = new float[1];
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm");
        String currentTime = simpleDateFormat.format(calendar.getTime());
        float currentHour = Float.valueOf(currentTime.substring(0,currentTime.indexOf(":")))+Float.valueOf(currentTime.substring(currentTime.indexOf(":")+1,currentTime.length()))/60;
        int radius;
            switch (spinnerPosition) {
                case 1:
                    radius = 100;
                    break;
                case 2:
                    radius = 200;
                    break;
                case 3:
                    radius = 300;
                    break;
                case 4:
                    radius = 400;
                    break;
                case 5:
                    radius = 500;
                    break;
                default:
                    if(sASButton.isChecked() || openButton.isChecked() || sFeesButton.isChecked()){
                        radius = 150;
                    }
                    else {
                        radius = 0;
                    }
                    break;
            }
        //Create an ArrayList of Parking object
        ArrayList<Parking> filter = new ArrayList<Parking>();
        //Create an ArrayList of String to store distance information
        ArrayList<String> distance = new ArrayList<String>();
        for (Map.Entry<String, Parking> parkingSet : parkingsList.entrySet()) {
            Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, parkingSet.getValue().lat, parkingSet.getValue().lng, result1);
            if(result1[0] < radius){
                filter.add(parkingSet.getValue());
            }
        }
        Log.i("My Circle State", String.valueOf(myCircle));
        if(myCircle != null){
            myCircle.remove();
            myCircle = null;
        }
            CircleOptions circleOptions = new CircleOptions()
                    .center(cameraLatLng)   //set center
                    .radius(radius)   //set radius in meters
                    .fillColor(Color.argb(150, 235, 236, 246))  //default
                    .strokeColor(Color.BLUE)
                    .strokeWidth(5);
            myCircle = mMap.addCircle(circleOptions);
        // Bubble Sorting
            for (int i = 0; i < filter.size(); i++) {
                for (int j = i + 1; j < filter.size(); j++) {
                    float spot1 = 0;
                    float spot2 = 0;
                    double fee1 = 0;
                    double fee2 = 0;
                    float operatingHour1 = 0;
                    float operatingHour2 = 0;
                    if(sASButton.isChecked()){
                        spot1 = Collections.frequency(filter.get(i).spots.values(), true)/filter.get(i).spots.size();
                        spot2 = Collections.frequency(filter.get(j).spots.values(), true)/filter.get(j).spots.size();
                    }
                    else{
                        spot1 = 0;
                        spot2 = 0;
                    }
                    if(sFeesButton.isChecked()){
                        fee1 = filter.get(i).fees;
                        fee2 = filter.get(j).fees;
                    }
                    if(openButton.isChecked()){
                        String a = filter.get(i).operatingHour;
                        String b = filter.get(j).operatingHour;
                        float ch1 = Float.valueOf(a.substring(a.indexOf("-")+1,a.indexOf(":",a.indexOf("-"))))+Float.valueOf(a.substring(a.indexOf(":",a.indexOf("-"))+1,a.length()))/60;
                        float ch2 = Float.valueOf(b.substring(b.indexOf("-")+1,b.indexOf(":",b.indexOf("-"))))+Float.valueOf(b.substring(b.indexOf(":",b.indexOf("-"))+1,b.length()))/60;
                        operatingHour1 = (ch1-currentHour)/currentHour;
                        operatingHour2 = (ch2-currentHour)/currentHour;
                        Log.i("Spot1 Hour: ", String.valueOf(operatingHour1));
                        Log.i("Spot2 Hour: ", String.valueOf(operatingHour2));
                    }
                    if(spinnerPosition>0){
                        Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(i).lat, filter.get(i).lng, result1);
                        Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(j).lat, filter.get(j).lng, result2);
                    }
                    else{
                        result1[0] = 0;
                        result2[0] = 0;
                    }
                        float score1 = (float) (0.7*(result1[0]/2000) + 0.1*spot1 + 0.1*(1-operatingHour1) + 0.1*(fee1));
                        float score2 = (float) (0.7*(result2[0]/2000) + 0.1*spot2 + 0.1*(1-operatingHour2)+ 0.1*(fee2));
                        if(score1>score2){
                            Collections.swap(filter,j,i);
                        }
                    }
                }
        //Get the distance information of the sorted parking array list
        for (int i = 0; i < filter.size(); i++) {
            Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(i).lat, filter.get(i).lng, result1);
            distance.add(String.format("%.0f", result1[0]));
        }
        //Set the adapter for the List View
        filterListAdapter adapter = new filterListAdapter(this, R.layout.adapter_bottom_sheet_list_view, filter, distance);
        filterListView.setAdapter(adapter);
    }

    public void updateview(LatLng latLng){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    }
}

