package com.team19.smartpark;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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
    private ToggleButton sDistanceButton, sASButton, sFeesButton;
    private TextView textView;
    private LatLng cameraLatLng;
    private BottomSheetBehavior mbottomSheetBehavior;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    //ArrayList to hold all parking objects
    private LinkedHashMap<String, Parking> parkings;
    private FloatingActionButton fab;


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
                        //    double lat=address.getLatitude();
                        //   double lon=address.getLongitude();

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


    //send to google map show directions
        public void DisplayTrack(String destination) {
            getDeviceLocation();
            Double la=lastKnownLocation.getLatitude();
            Double lo=lastKnownLocation.getLongitude();
//            for (Map.Entry<Double, Double> userLocationn: CurrentLocationCoordinates().entrySet()){
//            la=userLocationn.getValue();
//            lo=userLocationn.getKey();}


        try {
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + la +","+ lo + "/" + destination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        } catch (ActivityNotFoundException e) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

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
//              String What=  mMap.getUiSettings().toString();
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
//                            double lat = lastKnownLocation.getLatitude();
//                            double lon = lastKnownLocation.getLongitude();

                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                cameraLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

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
        linearLayout = (LinearLayout) findViewById(R.id.bottomSheet);
        filterListView = (ListView) findViewById(R.id.filterListView);
        mbottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        nearbyButton = findViewById(R.id.nearbyButton);
        textView = findViewById(R.id.sortTextView);
        sDistanceButton = findViewById(R.id.sortDistanceButton);
        sFeesButton = findViewById(R.id.sortFeesButton);
        sASButton = findViewById(R.id.sortPriceButton);
        clearButton = findViewById(R.id.clearButton);
        updateFilterUI(false);


        sDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sDistanceButton.isChecked() && !sASButton.isChecked()) {
                    sDistanceButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (!sDistanceButton.isChecked()) {
                    sDistanceButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                } else {
                    sDistanceButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(235, 236, 246)));
                    sortAlgorithm();
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        sASButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sDistanceButton.isChecked() && !sASButton.isChecked()) {
                    sDistanceButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                    mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (!sASButton.isChecked()) {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                } else {
                    sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(235, 236, 246)));
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
                updateFilterUI(false);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            }
        });
    }

    private void updateFilterUI(Boolean state) {
        if (state) {
            nearbyButton.setVisibility(View.GONE);
            sDistanceButton.setVisibility(View.VISIBLE);
            sASButton.setVisibility(View.VISIBLE);
            sFeesButton.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            clearButton.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);

        } else {
            nearbyButton.setVisibility(View.VISIBLE);
            sDistanceButton.setVisibility(View.GONE);
            sASButton.setVisibility(View.GONE);
            sFeesButton.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            clearButton.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            sASButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            sASButton.setChecked(false);
            sDistanceButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            sDistanceButton.setChecked(false);
            mbottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
    private void sortAlgorithm() {
        float result1[] = new float[1];
        float result2[] = new float[1];
        float ressult3[]= new float[1];
        ArrayList<Parking> filter = new ArrayList<Parking>();
        ArrayList<String> distance = new ArrayList<String>();
//        TreeMap<String, Parking> fList = new TreeMap<>();

        for (Map.Entry<String, Parking> parkingSet : parkingsList.entrySet()) {
            Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, parkingSet.getValue().lat, parkingSet.getValue().lng, result1);
            if (result1[0] < 2000) {
                filter.add(parkingSet.getValue());
            }
        }
        // Distance Sort
        for (int i = 0; i < filter.size(); i++) {
            for (int j = i + 1; j < filter.size(); j++) {
                if (sDistanceButton.isChecked() && !sASButton.isChecked()) {
                    Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(i).lat, filter.get(i).lng, result1);
                    Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(j).lat, filter.get(j).lng, result2);
                    if (result1[0] > result2[0]) {
                        Collections.swap(filter, j, i);
                    }
                } else if (!sDistanceButton.isChecked() && sASButton.isChecked()) {
                    if (Collections.frequency(filter.get(i).spots.values(), true) < Collections.frequency(filter.get(j).spots.values(), true)) {
                        Collections.swap(filter, i, j);
                    }
                } else if (sDistanceButton.isChecked() && sASButton.isChecked()) {
                    Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(i).lat, filter.get(i).lng, result1);
                    float spot1 = Collections.frequency(filter.get(i).spots.values(), true) / filter.get(i).spots.size();
                    Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(j).lat, filter.get(j).lng, result2);
                    float spot2 = Collections.frequency(filter.get(j).spots.values(), true) / filter.get(j).spots.size();
                    float score1 = (float) (0.8 * (1 - result1[0] / 2000) + 0.2 * spot1);
                    float score2 = (float) (0.8 * (1 - result2[0] / 2000) + 0.2 * spot2);
                    if (score1 > score2) {
                        Collections.swap(filter, j, i);
                    }
                }

            }
        }

        for (int i = 0; i < filter.size(); i++) {
            Location.distanceBetween(cameraLatLng.latitude, cameraLatLng.longitude, filter.get(i).lat, filter.get(i).lng, result1);
            distance.add(String.format("%.0f", result1[0]));
        }
        filterListAdapter adapter = new filterListAdapter(this, R.layout.adapter_bottom_sheet_list_view, filter, distance);
        filterListView.setAdapter(adapter);
    }

    public void updateview(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
    }
}

