package com.team19.smartpark.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class Parking {
    public String address;
    public double lat;
    public double lng;
    public double fees;
    public String name;
    public String operatingHour;
    public HashMap<String, Boolean> spots;
    public boolean status;

    public Parking() {
    }

    public Parking(String address, double lat, double lng, String name, HashMap<String, Boolean> spots, boolean status, double fees) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.spots = spots;
        this.status = status;
        this.fees = fees;
    }

    @Override
    public String toString() {
        return "Parking{" +
                "address='" + address + '\'' +
                "Operating Hours='" + operatingHour + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", name='" + name + '\'' +
                ", spots=" + spots +
                ", status=" + status +
                ", fees=" + fees +
                '}';
    }


    public String getOperatingHour() {
        return operatingHour;
    }

    public void setOperatingHour(String operatingHour) {
        this.operatingHour = operatingHour;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Boolean> getSpots() {
        return spots;
    }

    public void setSpots(HashMap<String, Boolean> spots) {
        this.spots = spots;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getfees() {
        return fees;
    }

    public void setfees(double fees) {
        this.fees = fees;
    }

    public void setLatLng(LatLng latLng) {
        lat = latLng.latitude;
        lng = latLng.longitude;
    }
}
