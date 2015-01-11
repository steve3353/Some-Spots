package ca.bc.vancouver.smsz.somespots.somespots.ObjectModels;


import com.google.android.gms.maps.model.LatLng;

public class Fountain {

    private LatLng latLng;
    private String location;

    public Fountain(LatLng latLng, String location){
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

