package ca.bc.vancouver.smsz.somespots.somespots.ObjectModels;

import com.google.android.gms.maps.model.LatLng;

public class BikeRack {

    private LatLng latLng;
    private String location;

    public BikeRack(LatLng latLng, String location){
        this.latLng = latLng;
        this.location = location;
    }

    public LatLng getLatLng(){
        return latLng;
    }

    public String getLocation(){
        return location;
    }



}