package com.isuri.ambulancetracker.apptracker;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LocationManager locationManager;
    private GoogleMap mMap;
    private static String JSONObjLocation;
    String userName;
    String userID;
    String userLatitude;
    String userLongitude;
    String jsonObject;

    GoogleApiClient mGoogleApiClient; //last known position
    Location mLastLocation; //last known position

    // this stringbuilder will hold messages
    static StringBuilder locationMessage = new StringBuilder();

    final Pubnub pubnub = new Pubnub("pub-c-21f78b4a-1b44-4af2-9786-a0f32e626750", "sub-c-b9c15196-0960-11e6-bbd9-02ee2ddab7fe");

    int localUserID;
    String localUsername;
    double localuserLatitude;
    double localuserLongitude;

    List<Marker> list = new ArrayList<Marker>(); //marker list


    boolean passenger = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.isuri.ambulancetracker.apptracker.R.layout.activity_maps);

        passenger = getIntent().getExtras().getBoolean("IsPassenger");

        if (!passenger) { //if user logged in as a passenger this code block will skip
            userName = getIntent().getStringExtra("Username");
            userID = getIntent().getStringExtra("UserID");

            TextView tvDetails = (TextView) findViewById(com.isuri.ambulancetracker.apptracker.R.id.textViewDetails);
            tvDetails.setText(getIntent().getStringExtra("UserDetails"));
        } else {
            TextView tvDetails = (TextView) findViewById(com.isuri.ambulancetracker.apptracker.R.id.textViewDetails);
            tvDetails.setText(com.isuri.ambulancetracker.apptracker.R.string.passengerHint);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(com.isuri.ambulancetracker.apptracker.R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "error perm 1", Toast.LENGTH_LONG);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

//        mMap = ((SupportMapFragment) ((SupportMapFragment) getSupportFragmentManager().findFragmentById(com.isuri.ambulancetracker.apptracker.R.id.map))).getMapAsync();





        //To get the last known position
        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();

        //////////////////////////////begining of pubnub///////////////////////////////////////


        // subscribe to channel: android_club
        // you can set any channel you like
        try {
            pubnub.subscribe("android_club", new Callback() {
                public void successCallback(String channel, Object message) {
                    // this will be called each time, when android_club subsriber get message
                    //showMessage(message.toString());
//                    String JSON_STRING=message.toString();
                    Log.w("getJsonmsg", message.toString());
//                    try{
//                        JSONObject emp=(new JSONObject(JSON_STRING)).getJSONObject("Employee");
//                        int empid = emp.getInt("id");
//                        String empname=emp.getString("name");
//                        int empsalary=emp.getInt("salary");
//
//                        String str="Employee ID:"+empid+"\nEmployee Name:"+empname+"\n"+"Employee Salary:"+empsalary;
//                        showMessage(str);
//
//                    }catch (Exception e) {e.printStackTrace();}


                    if (message instanceof JSONObject) {
                        JSONObject json = (JSONObject) message;
                        try {
                            JSONObject emp = json.getJSONObject("userLocation");
                            localUserID = emp.getInt("UserID");
                            localUsername = emp.getString("Username");
                            localuserLatitude = emp.getDouble("userLatitude");
                            localuserLongitude = emp.getDouble("userLongitude");

                            String str = "user ID:" + localUserID + "\nusr Name:" + localUsername + "\n" + "user lat:" + localuserLatitude + "\n" + "user long:" + localuserLongitude;
                            //String str="Employee Name:"+empname;
                            showMessage(str);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (message instanceof String) {
                        String str = (String) message;
                        // Handle String
                    }
                    Log.d("PUBNUB", "Channel: " + channel + " Msg: " + message.toString());


                }

                public void errorCallback(String channel, PubnubError error) {
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }

        // button to send message
//        Button bSend = (Button) findViewById(R.id.bSend);
//        bSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Callback callback = new Callback() {
//                    public void successCallback(String channel, Object response) {
//                    }
//                    public void errorCallback(String channel, PubnubError error) {
//                    }
//                };
//
//
//                // Okey, let's publish(send) message
//                JSONObject ff = null;
//                try {
//                    ff = new JSONObject(jsonObject);
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//                pubnub.publish("android_club", ff, callback);
//            }
//        });

        /////////////////////////////////////end of pubnub///////////////////////////////////


    }

    private void showMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                locationMessage.append(message);
                locationMessage.append("\n\n");
//
//                // let's set all text to text view
//                TextView tvMessages = (TextView) findViewById(R.id.tvMessages);
//                // yes, each time!
//                tvMessages.setText(locationMessage.toString());
//
//                // let's scroll to down, down, down......
//                ScrollView svMessages = (ScrollView) findViewById(R.id.svMessages);
//                svMessages.fullScroll(View.FOCUS_DOWN);

                LatLng newUserLoc = new LatLng(localuserLatitude, localuserLongitude);
//                mMap.addMarker(new MarkerOptions().position(newUserLoc).title(userName));
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(newUserLoc));


/////////////////////////////////////////getting and setting a marker////////////////////////////////////////////
                //Marker markerS = (Marker)markersStore.get(localUserID+"");

                for (Marker m : list) {
                    if (m.getTitle().equals(localUserID + " " + localUsername)) {
                        // do something with the marker
                        m.remove();
                    }


                }


                Marker marker = mMap.addMarker(new MarkerOptions().position(newUserLoc).title(localUserID + " " + localUsername));
                marker.setIcon(BitmapDescriptorFactory.fromResource(com.isuri.ambulancetracker.apptracker.R.drawable.markerbusicon));
                list.add(marker);


            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        LatLng sydney = new LatLng(6.927079,79.861244);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//        mMap.setMyLocationEnabled(true);
//
//
//        LatLng myLoc = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(myLoc).title(userName));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!passenger) { //if user logged in as a passenger this code block will skip
            userLatitude = location.getLatitude() + "";
            userLongitude = location.getLongitude() + "";

            jsonObject = "{ \"userLocation\" :{\"UserID\":\"" + userID + "\",\"Username\":\"" + userName + "\",\"userLatitude\":\"" + userLatitude + "\",\"userLongitude\":\"" + userLongitude + "\"} }";


//        TextView asdas = (TextView) findViewById(R.id.textView);
//                // yes, each time!
//        asdas.setText(jsonObject);


            Callback callback = new Callback() {
                public void successCallback(String channel, Object response) {
                }

                public void errorCallback(String channel, PubnubError error) {
                }
            };


            // Okey, let's publish(send) message
            JSONObject ff = null;
            try {
                ff = new JSONObject(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pubnub.publish("android_club", ff, callback);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "error perm 2", Toast.LENGTH_LONG);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            //tvLatlong.setText("Latitude: "+ String.valueOf(mLastLocation.getLatitude())+"Longitude: "+
                   // String.valueOf(mLastLocation.getLongitude()));

            mMap.setMyLocationEnabled(true);


            LatLng myLoc = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());

            if(!passenger) { //if user logged in as a passenger this code block will skip
                Marker m = mMap.addMarker(new MarkerOptions().position(myLoc).title(userID + " " + userName));
                m.showInfoWindow();
                m.setIcon(BitmapDescriptorFactory.fromResource(com.isuri.ambulancetracker.apptracker.R.drawable.markerbusicon));
                list.add(m);
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLoc)      // Sets the center of the map to Mountain View
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

}
