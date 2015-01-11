package ca.bc.vancouver.smsz.somespots.somespots.ObjectModels;

import com.google.android.gms.maps.model.LatLng;

public class Park {

    private LatLng latLng;
    private String address;
    private String parkName;
    private String washroomLocationInPark;
    private String washroomSummerHour;
    private String washroomWinterHour;

    public Park(LatLng latLng, int streetNum,String streetName, String parkName, String washroomLocationInPark, String washroomSummerHour, String washroomWinterHour){
        this.latLng = latLng;
        this.address = streetNum + streetName;
        this.parkName = parkName;
        this.washroomLocationInPark = washroomLocationInPark;
        this.washroomSummerHour = washroomSummerHour;
        this.washroomWinterHour = washroomWinterHour;
    }

    public LatLng getLatLng(){
        return latLng;
    }

    public String getAddress(){
        return address;
    }

    public String getParkName(){
        return parkName;
    }
    public String getWashroomLocationInPark(){
        return washroomLocationInPark;
    }
    public String getWashroomSummerHour(){
        return washroomSummerHour;
    }

    public String getWashroomWinterHour(){
        return washroomWinterHour;
    }
}
