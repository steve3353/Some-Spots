package ca.bc.vancouver.smsz.somespots.somespots.ObjectModels;


import com.google.android.gms.maps.model.LatLng;

public class BikePump {

    private LatLng latLng;


    public BikePump(LatLng latLng){
        this.latLng = latLng;

    }

    public LatLng getLatLng(){
        return latLng;
    }
}
