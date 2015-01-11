package ca.bc.vancouver.smsz.somespots.somespots;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ca.bc.vancouver.smsz.somespots.somespots.ObjectModels.BikePump;
import ca.bc.vancouver.smsz.somespots.somespots.ObjectModels.BikeRack;
import ca.bc.vancouver.smsz.somespots.somespots.ObjectModels.Fountain;
import ca.bc.vancouver.smsz.somespots.somespots.ObjectModels.Park;


public class DisplayMapActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;


    private List<Fountain> fountainList = new ArrayList<Fountain>();
    private List<BikeRack> bikeRackList = new ArrayList<BikeRack>();
    private List<BikePump> bikePumpList = new ArrayList<BikePump>();
    private List<Park> parkList = new ArrayList<Park>();

    private String whichItem = null;
    private String jsonBikeWay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_map_activity);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();





        Intent intent = this.getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            whichItem = intent.getStringExtra(Intent.EXTRA_TEXT);

            switch(whichItem){
                case "Water Fountains (City of Vancouver) ":
                    ReadFountainFileTask readFountainFileTask = new ReadFountainFileTask();
                    readFountainFileTask.execute();
                    break;
                case "Parks with Public Washrooms (City of Vancouver)":
                    ReadParkFileTask readParkFileTask = new ReadParkFileTask();
                    readParkFileTask.execute();
                    break;
                case "Bike Racks (City of Vancouver)" :
                    ReadBikeRackFileTask readBikeRackFileTask = new ReadBikeRackFileTask();
                    readBikeRackFileTask.execute();
                    break;
                case "Bikeways in City of Vancouver" :
                    ReadBikeWayFileTask readBikeWayFileTask = new ReadBikeWayFileTask();
                    readBikeWayFileTask.execute();
                    break;
                case "Bike Pumps in City of Vancouver" :
                    ReadBikePumpFileTask readBikePumpFileTask = new ReadBikePumpFileTask();
                    readBikePumpFileTask.execute();
                    break;
            }

        }


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng initialLocation;

        map = googleMap;

        if(mLastLocation != null )
        initialLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        else
        initialLocation= new LatLng(49.2827, -123.121);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);

        map.setMyLocationEnabled(true);

        if(whichItem != null){
            switch(whichItem){
                case "Water Fountains (City of Vancouver) ":

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15) );
                    break;
                case "Parks with Public Washrooms (City of Vancouver)":

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 14) );
                    break;
                case "Bike Racks (City of Vancouver)" :

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 16) );
                    break;
                case "Bikeways in City of Vancouver" :

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 14) );

                    break;
                case "Bike Pumps in City of Vancouver" :

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12) );
                    break;
            }
        }





        //map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //map.addMarker(new MarkerOptions().title("Vancouver").snippet("LALALALAL").position(vancouver));


    }

    public class ReadBikeWayFileTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            readBikeWayFile();

            return 0;
        }

        @Override
        protected void onPostExecute(Integer val) {

            startDrawingBikeWay();

        }

        public void startDrawingBikeWay(){
            if (map != null) {
                if (jsonBikeWay != null) {
                    try {
                        drawBikeWayPolyline(jsonBikeWay);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }else {
                startDrawingBikeWay();
            }

        }



        public void readBikeWayFile() {
            try {
                InputStream resourceReader = getResources().openRawResource(R.raw.bikeway);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                int abyte;
                abyte = resourceReader.read();

                while (abyte != -1) {
                    byteArrayOutputStream.write(abyte);
                    abyte = resourceReader.read();
                }

                resourceReader.close();

                String in = byteArrayOutputStream.toString();

                jsonBikeWay = in;





            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void drawBikeWayPolyline( String in) throws JSONException{

            JSONObject jsonFile = new JSONObject(in);
            JSONObject jsonkml = jsonFile.getJSONObject("kml");

            JSONObject jsonDocument = jsonkml.getJSONObject("Document");

            JSONObject jsonFolder = jsonDocument.getJSONObject("Folder");

            JSONArray jsonPlacemark = jsonFolder.getJSONArray("Placemark");


            for(int l =0 ; l< jsonPlacemark.length() ; l++) {
                JSONObject jsonPATH = jsonPlacemark.getJSONObject(l);

                JSONObject multigeometry = jsonPATH.getJSONObject("MultiGeometry");



                try {
                    List<Double> latList = new ArrayList<Double>();
                    List<Double> lonList = new ArrayList<Double>();
                    JSONObject linestrings = multigeometry.getJSONObject("LineString");
                    String multicoordinates = linestrings.getString("coordinates");
                    String[] coordinatesArray = multicoordinates.split(",");
                    for (int j = 0; j < coordinatesArray.length - 1; j = j + 2) {
                        if (j != 0) {
                            coordinatesArray[j] = coordinatesArray[j].substring(2);
                        }
                        lonList.add(Double.parseDouble(coordinatesArray[j]));
                    }
                    for (int k = 1; k < coordinatesArray.length - 1; k = k + 2) {
                        latList.add(Double.parseDouble(coordinatesArray[k]));
                    }
                    PolylineOptions wayOptions = new PolylineOptions().geodesic(true).color(Color.BLUE).width(10);

                    for (int x = 0; x < latList.size(); x++) {
                        wayOptions.add(new LatLng(latList.get(x), lonList.get(x)));
                    }
                    Polyline polyline = map.addPolyline(wayOptions);



                } catch(JSONException e){

                    JSONArray linestrings = multigeometry.getJSONArray("LineString");
                    for (int i = 0; i < linestrings.length(); i++) {
                        List<Double> latList = new ArrayList<Double>();
                        List<Double> lonList = new ArrayList<Double>();
                        JSONObject oneline = linestrings.getJSONObject(i);
                        String multicoordinates = oneline.getString("coordinates");
                        String[] coordinatesArray = multicoordinates.split(",");
                        for (int j = 0; j < coordinatesArray.length - 1; j = j + 2) {
                            if (j != 0) {
                                coordinatesArray[j] = coordinatesArray[j].substring(2);
                            }
                            lonList.add(Double.parseDouble(coordinatesArray[j]));
                        }
                        for (int k = 1; k < coordinatesArray.length - 1; k = k + 2) {
                            latList.add(Double.parseDouble(coordinatesArray[k]));
                        }

                        PolylineOptions wayOptions = new PolylineOptions().geodesic(true).color(Color.BLUE).width(10);

                        for (int x = 0; x < latList.size(); x++) {
                            wayOptions.add(new LatLng(latList.get(x), lonList.get(x)));
                        }
                        Polyline polyline = map.addPolyline(wayOptions);




                    }
                }


            }
        }

    }


    public class ReadBikeRackFileTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                readBikeRackFile();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer val) {
            startPlottingBikeRack();

        }

        public void startPlottingBikeRack(){
            if (map != null) {
                if (bikeRackList.size() > 0) {
                    for (BikeRack b : bikeRackList) {
                        map.addMarker(new MarkerOptions().title("Bike Rack").snippet("Location: " + b.getLocation()).position(b.getLatLng()));
                    }

                }

            }else{
                startPlottingBikeRack();
            }
        }

        public void readBikeRackFile() throws JSONException {
            try {
                InputStream resourceReader = getResources().openRawResource(R.raw.bikerack);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                int abyte;
                abyte = resourceReader.read();

                while (abyte != -1) {
                    byteArrayOutputStream.write(abyte);
                    abyte = resourceReader.read();
                }

                resourceReader.close();

                String in = byteArrayOutputStream.toString();


                JSONArray jsonBikeRackList = new JSONArray(in);
                for (int i = 0; i < jsonBikeRackList.length(); i++) {
                    JSONObject jsonBikeRack = jsonBikeRackList.getJSONObject(i);
                    LatLng latLng = new LatLng(jsonBikeRack.getDouble("latitude"), jsonBikeRack.getDouble("longitude"));

                    String location = jsonBikeRack.getString("location");

                    BikeRack bikeRack = new BikeRack(latLng, location);
                    bikeRackList.add(bikeRack);

                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();


            } catch (IOException e) {
                e.printStackTrace();

            }
        }


    }


    public class ReadFountainFileTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                readFountainFile();
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer val) {
            startPlottingFountains();

        }

        public void startPlottingFountains(){
            if (map != null) {
                if(fountainList.size() > 0){
                    for(Fountain f : fountainList){

                        map.addMarker(new MarkerOptions().title("Fountain").snippet(f.getLocation()).position(f.getLatLng()));
                    }
                }

            }else {
                startPlottingFountains();
            }
        }

        public void readFountainFile() throws JSONException {

            //BufferedReader reader = null;
            try {
                InputStream resourceReader = getResources().openRawResource(R.raw.drinking_fountains);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                int abyte;
                abyte = resourceReader.read();
                // reader = new BufferedReader((new InputStreamReader(resourceReader,"UFT-8")));
                //StringBuilder jsonFountains = new StringBuilder();
                String singleline;
                //while((singleline = reader.readLine()) != null){
                while (abyte != -1) {
                    byteArrayOutputStream.write(abyte);
                    abyte = resourceReader.read();
                    // jsonFountains.append(singleline);

                }
                resourceReader.close();
                // reader.close();

                // String in = jsonFountains.toString();

                String in = byteArrayOutputStream.toString();




                JSONObject allFountains = new JSONObject(in);
                JSONArray jsonfountainList = allFountains.getJSONArray("features");

                for (int i = 0; i < jsonfountainList.length(); i++) {
                    JSONObject jsonFountain = jsonfountainList.getJSONObject(i);
                    JSONObject geo = jsonFountain.getJSONObject("geometry");
                    JSONArray coordinates = geo.getJSONArray("coordinates");
                    LatLng latLng = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));

                    JSONObject properties = jsonFountain.getJSONObject("properties");

                    String location = properties.getString("NAME");

                    Fountain fountain = new Fountain(latLng, location);
                    fountainList.add(fountain);

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }




        }

    }


    public class ReadBikePumpFileTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                readbikePumpFile();
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return 0;
        }
        @Override
        protected void onPostExecute(Integer val) {
            startPlottingBikePump();

        }

        public void startPlottingBikePump(){
            if (map != null) {
                if (bikePumpList.size() > 0) {
                    for (BikePump b : bikePumpList) {
                        map.addMarker(new MarkerOptions().title("Bike Pump").position(b.getLatLng()));
                    }

                }

            }else {
                startPlottingBikePump();
            }
        }

        public void readbikePumpFile() throws JSONException {
            try {
                InputStream resourceReader = getResources().openRawResource(R.raw.bikepump);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                int abyte;
                abyte = resourceReader.read();

                while (abyte != -1) {
                    byteArrayOutputStream.write(abyte);
                    abyte = resourceReader.read();
                }

                resourceReader.close();

                String in = byteArrayOutputStream.toString();

                JSONArray jsonBikePumpList = new JSONArray(in);
                for (int i = 0; i < jsonBikePumpList.length(); i++) {
                    JSONObject jsonBikePump = jsonBikePumpList.getJSONObject(i);
                    LatLng latLng = new LatLng(jsonBikePump.getDouble("latitude"), jsonBikePump.getDouble("longitude"));



                    BikePump bikePump = new BikePump(latLng);
                    bikePumpList.add(bikePump);
                }

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }

    public class ReadParkFileTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                readParkWithWashroomFile();
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer val) {
            startPlottingPark();

        }

        public void startPlottingPark(){
            if (map != null) {
                if(parkList.size()>0){
                    for(Park p : parkList){
                        map.addMarker(new MarkerOptions().title(p.getParkName() + " Washroom Hours: ").snippet(p.getWashroomSummerHour() + ", "+ p.getWashroomWinterHour() ).position(p.getLatLng()));
                    }
                }


            }else {
                startPlottingPark();
            }
        }

        public void readParkWithWashroomFile() throws JSONException {
            try {
                InputStream resourceReader = getResources().openRawResource(R.raw.park_with_washroom);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                int abyte;
                abyte = resourceReader.read();

                while (abyte != -1) {
                    byteArrayOutputStream.write(abyte);
                    abyte = resourceReader.read();
                }

                resourceReader.close();

                String in = byteArrayOutputStream.toString();

                JSONArray jsonParkList = new JSONArray(in);
                for (int i = 0; i < jsonParkList.length(); i++) {
                    JSONObject jsonPark = jsonParkList.getJSONObject(i);
                    String jsonlatLng = jsonPark.getString("GoogleMapDest");
                    String[] latlngArray = jsonlatLng.split(",");

                    Double lat = Double.parseDouble(latlngArray[0]);
                    Double lng = Double.parseDouble(latlngArray[1]);
                    LatLng latLng = new LatLng(lat,lng);

                    int streetNum = jsonPark.getInt("StreetNumber");
                    String streetName = jsonPark.getString("StreetName");

                    String parkName = jsonPark.getString("parkName");
                    String washroomLocationInPark;
                    try {
                        washroomLocationInPark = jsonPark.getString("WashroomLocation");
                    }catch (JSONException e){
                        washroomLocationInPark = null;
                    }
                    String washroomSummerHours = jsonPark.getString("WashroomSummerHours");
                    String washroomWinterHours = jsonPark.getString("WashroomWinterHours");




                    Park park = new Park(latLng, streetNum,streetName,parkName,washroomLocationInPark,washroomSummerHours,washroomWinterHours);
                    parkList.add(park);

                }


            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();

            }

        }

    }











    @Override
    public void onConnected(Bundle bundle) {



        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {


    }
}