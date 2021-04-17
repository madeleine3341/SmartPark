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


    /**
     * Parking constructor to create a new parking instance to be pushed to Firebse RTDB
     *
     * @param address
     * @param lat
     * @param lng
     * @param name
     * @param spots
     * @param status
     * @param fees
     */
    public Parking(String address, double lat, double lng, String name, HashMap<String, Boolean> spots, boolean status, double fees) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.spots = spots;
        this.status = status;
        this.fees = fees;
    }

    /**
     * @return A String representation of a Parking object
     */
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


    /**
     * @return Operating hours of the parking in String format
     */
    public String getOperatingHour() {
        return operatingHour;
    }

    /**
     * Set the operator hours of the Parkin object
     *
     * @param operatingHour
     */
    public void setOperatingHour(String operatingHour) {
        this.operatingHour = operatingHour;
    }

    /**
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return latitude of Parking object as a double
     */
    public double getLat() {
        return lat;
    }

    /**
     * set the latitude of the Parking object
     *
     * @param lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * @return latitude of the Parking object
     */
    public double getLng() {
        return lng;
    }

    /**
     * @param lng
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    /**
     * @return name of the Parking object
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Parking spot IDs and their statuses in a HashMap representation
     */
    public HashMap<String, Boolean> getSpots() {
        return spots;
    }

    /**
     * set the Parking object parking spots as HashMap with keys as the IDs of the spots and
     * status as the value
     *
     * @param spots
     */
    public void setSpots(HashMap<String, Boolean> spots) {
        this.spots = spots;
    }

    /**
     * @return the status of the parking as a boolean
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Returns the status of the parking
     *
     * @param status
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * @return the fees for this Parking object
     */
    public double getfees() {
        return fees;
    }

    /**
     * Set the fees of this Parking object
     *
     * @param fees
     */
    public void setfees(double fees) {
        this.fees = fees;
    }

    /**
     * Set the position of the parking
     *
     * @param latLng
     */
    public void setLatLng(LatLng latLng) {
        lat = latLng.latitude;
        lng = latLng.longitude;
    }
}
