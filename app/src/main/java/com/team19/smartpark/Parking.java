package com.team19.smartpark;

import java.util.ArrayList;
import java.util.HashMap;

public class Parking {
    public String address;
    public double lat;
    public double lng;
    public String name;
    public HashMap<String, Boolean> spots;
    public boolean status;

    public Parking() {
    }

    public Parking(String address, double lat, double lng, String name, HashMap<String, Boolean> spots, boolean status) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.spots = spots;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Parking{" +
                "address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", name='" + name + '\'' +
                ", spots=" + spots +
                ", status=" + status +
                '}';
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
}
