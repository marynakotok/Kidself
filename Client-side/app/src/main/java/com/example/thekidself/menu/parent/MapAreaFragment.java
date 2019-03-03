package com.example.thekidself.menu.parent;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekidself.R;
import com.example.thekidself.helper.BackgroundTaskSelectDB;
import com.example.thekidself.map.AvailableChildServicesFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;


public class MapAreaFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
LocationListener{

    static final String TAGLISTEN = "ListenChild";
    View view;
    String family, child, IP, codeParent;
    String[] codeChild, genderChildDatabase, nameChildDatabase,codeChildDatabase;
    ImageView[] mImageChild;
    ImageView mImageMapZone, mImageMapListen;
    TextView[] mNameChild;
    SharedPreferences mSharedPreferences;
    int length;
    SupportMapFragment mSupportMapFragment;
    private Location lastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    private int LOCATION_PERMISSION_CODE = 1;
    private GoogleApiClient googleApiClient;
    private GoogleMap map;



    public MapAreaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map_area, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.stringMapArea));
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        length = 6;
        mImageChild = new ImageView[length];
        mNameChild = new TextView[length];
        IP = getString(R.string.stringIP);
        mImageChild[0] = view.findViewById(R.id.image_child1);
        mImageChild[1] = view.findViewById(R.id.image_child2);
        mImageChild[2] = view.findViewById(R.id.image_child3);
        mImageChild[3] = view.findViewById(R.id.image_child4);
        mImageChild[4] = view.findViewById(R.id.image_child5);
        mImageChild[5] = view.findViewById(R.id.image_child6);

        mNameChild[0] = view.findViewById(R.id.text_child1);
        mNameChild[1] = view.findViewById(R.id.text_child2);
        mNameChild[2] = view.findViewById(R.id.text_child3);
        mNameChild[3] = view.findViewById(R.id.text_child4);
        mNameChild[4] = view.findViewById(R.id.text_child5);
        mNameChild[5] = view.findViewById(R.id.text_child6);

        mImageMapListen = view.findViewById(R.id.image_map_listen);
        mImageMapZone = view.findViewById(R.id.image_map_zone);

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        if (mSupportMapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.fragment_map, mSupportMapFragment).commit();
        }


        mSupportMapFragment.getMapAsync(this);

        //Get name of the children

        mSharedPreferences = getActivity().getSharedPreferences("Parent",MODE_PRIVATE);
        codeParent = mSharedPreferences.getString("codeParent","");

        String sqlFamily = "SELECT parent_1,parent_2, child_1, child_2, child_3, child_4, child_5, child_6 FROM family WHERE parent_1='" + codeParent + "' OR parent_2='" + codeParent + "';";
        String methodGetData = "Selection";
        String tableFamily = "family";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetChildren = new BackgroundTaskSelectDB(view.getContext());

        family = null;
        JSONArray jsonArrayFamily = null;
        JSONObject objFamily = null;
        try {
            family = backgroundTaskSelectDBGetChildren.execute(IP, methodGetData, sqlFamily, tableFamily).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            jsonArrayFamily = new JSONArray(family);
            objFamily = jsonArrayFamily.getJSONObject(0);
            codeChild = new String[length];
            codeChild[0] = objFamily.getString("child_1");
            codeChild[1] = objFamily.getString("child_2");
            codeChild[2] = objFamily.getString("child_3");
            codeChild[3] = objFamily.getString("child_4");
            codeChild[4] = objFamily.getString("child_5");
            codeChild[5] = objFamily.getString("child_6");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (codeChild[0].equals("null") &&  codeChild[1].equals("null") &&  codeChild[2].equals("null") &&  codeChild[3].equals("null") &&  codeChild[4].equals("null") &&  codeChild[5].equals("null"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("You have to create at least one child to continue working with a map!").setCancelable(false).setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            goToFamilyArea();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.setTitle("Important!");
            alert.show();
        }

        //Get name of the Child

        String sqlNameChild = "SELECT code_child, name, sex, bd, created_on, last_login FROM child WHERE code_child='" +
                codeChild[0] + "' OR code_child='" + codeChild[1] + "' OR code_child='" + codeChild[2]
                + "' OR code_child='" + codeChild[3] + "' OR code_child='" + codeChild[4] + "' OR code_child='" + codeChild[5] + "';";
        String tableChild = "child";
        BackgroundTaskSelectDB backgroundTaskSelectDBGetNameChild = new BackgroundTaskSelectDB(view.getContext());

        child = null;
        try {
            child = backgroundTaskSelectDBGetNameChild.execute(IP, methodGetData, sqlNameChild, tableChild).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray jsonArrayChild = null;
        JSONObject objChild = null;
        try {
            jsonArrayChild = new JSONArray(child);
            genderChildDatabase = new String[jsonArrayChild.length()];
            nameChildDatabase = new String[jsonArrayChild.length()];
            codeChildDatabase = new String[jsonArrayChild.length()];
            for (int i=0; i<jsonArrayChild.length(); i++)
            {
                objChild = jsonArrayChild.getJSONObject(i);
                genderChildDatabase[i] = objChild.getString("sex");
                nameChildDatabase[i] = objChild.getString("name");
                codeChildDatabase[i] = objChild.getString("code_child");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Show the children on the panel

        for (int i = 0; i < length; i++)
        {
            if (!codeChild[i].equals("null"))
            {
                mImageChild[i].setVisibility(View.VISIBLE);
                if (genderChildDatabase[i].trim().equals("F"))
                {
                    mImageChild[i].setImageResource(R.drawable.girl);
                } else {
                    mImageChild[i].setImageResource(R.drawable.boy);
                }
                mNameChild[i].setVisibility(View.VISIBLE);
                mNameChild[i].setText(nameChildDatabase[i]);
            }
        }

        //------------------------------------------------------------------------

        createGoogleApi();

        mImageMapListen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MapListenChild();
            }
        });

        mImageMapZone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MapSetZone();
            }
        });

        return view;
    }

    public void MapListenChild()
    {
        goToAvailableChildServices();
    }

    public void MapSetZone()
    {


    }

    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d("", "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }

    public void goToFamilyArea()
    {
        FamilyAreaFragment fragment;
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fragment = new FamilyAreaFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_area, fragment).addToBackStack(null);
        ft.commit();
    }

    public void goToAvailableChildServices()
    {
        AvailableChildServicesFragment fragment;
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fragment = new AvailableChildServicesFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fragment_area, fragment).addToBackStack(null);
        ft.commit();
    }

    private void requestLocationPermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed")
                    .setMessage("This permission needed for getting your localization")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else {
                Toast.makeText(getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestLocationPermission();
        }

        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
//
//        mFusedLocationClient.getLastLocation()
//                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) {
//                            double longitude = location.getLongitude();
//                            double latitude = location.getLatitude();
//                            googleMap.addMarker(new MarkerOptions()
//                                    .position(new LatLng(latitude, longitude))
//                                    .title("Me")
//                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),16));
//                        }
//                    }});
    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d("", "getLastKnownLocation()");
        if ( (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.i("", "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w("", "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
    }

    private LocationRequest locationRequest;
    // Defined in mili seconds.
    // This number in extremely low, and should be used only for debug
    private final int UPDATE_INTERVAL =  3 * 60 * 1000; // 3 minutes
    private final int FASTEST_INTERVAL = 30 * 1000;  // 30 secs

    // Start location Updates
    private void startLocationUpdates(){
        Log.i("", "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("", "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);
    }

    // Write location coordinates on UI
    private void writeActualLocation(Location location) {
        markerLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private Marker locationMarker;
    // Create a Location Marker
    private void markerLocation(LatLng latLng) {
        Log.i("", "markerLocation("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        if ( map!=null ) {
            // Remove the anterior marker
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
            float zoom = 14f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
        }
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    // Callback called when Map is touched
    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("", "onMapClick("+latLng +")");
        markerForGeofence(latLng);
    }

    private Marker geoFenceMarker;
    // Create a marker for the geofence creation
    private void markerForGeofence(LatLng latLng) {
        Log.i("", "markerForGeofence(" + latLng + ")");
        String title = "Marek";//latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(title);
        if (map != null) {
            // Remove last geoFenceMarker
            if (geoFenceMarker != null)
                geoFenceMarker.remove();

            geoFenceMarker = map.addMarker(markerOptions);
        }
    }

    // Callback called when Marker is touched
    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("", "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("", "onConnected()");
        getLastKnownLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w("", "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w("", "onConnectionFailed()");
    }


    //GeoFence

//    private static final long GEO_DURATION = 60 * 60 * 1000;
//    private static final String GEOFENCE_REQ_ID = "My Geofence";
//    private static final float GEOFENCE_RADIUS = 500.0f; // in meters
//
//    // Create a Geofence
//    private Geofence createGeofence(LatLng latLng, float radius ) {
//        Log.d("", "createGeofence");
//        return new Geofence.Builder()
//                .setRequestId(GEOFENCE_REQ_ID)
//                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
//                .setExpirationDuration( GEO_DURATION )
//                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
//                        | Geofence.GEOFENCE_TRANSITION_EXIT )
//                .build();
//    }
//
//    // Create a Geofence Request
//    private GeofencingRequest createGeofenceRequest(Geofence geofence ) {
//        Log.d("", "createGeofenceRequest");
//        return new GeofencingRequest.Builder()
//                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
//                .addGeofence( geofence )
//                .build();
//    }
//
//    private PendingIntent geoFencePendingIntent;
//    private final int GEOFENCE_REQ_CODE = 0;
//    private PendingIntent createGeofencePendingIntent() {
//        Log.d("", "createGeofencePendingIntent");
//        if ( geoFencePendingIntent != null )
//            return geoFencePendingIntent;
//
//        Intent intent = new Intent(getContext(), GeofenceTrasitionService.class);
//        return PendingIntent.getService(
//                getContext(), GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
//    }
//
//    // Add the created GeofenceRequest to the device's monitoring list
//    private void addGeofence(GeofencingRequest request) {
//        Log.d("", "addGeofence");
//        if ( (ContextCompat.checkSelfPermission(getContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) )
//            LocationServices.GeofencingApi.addGeofences(
//                    googleApiClient,
//                    request,
//                    createGeofencePendingIntent()
//            ).setResultCallback(this);
//    }
//
//    @Override
//    public void onResult(@NonNull Status status) {
//        Log.i("", "onResult: " + status);
//        if ( status.isSuccess() ) {
//            drawGeofence();
//        } else {
//            // inform about fail
//        }
//    }
//
//    // Draw Geofence circle on GoogleMap
//    private Circle geoFenceLimits;
//    private void drawGeofence() {
//        Log.d("", "drawGeofence()");
//
//        if ( geoFenceLimits != null )
//            geoFenceLimits.remove();
//
//        CircleOptions circleOptions = new CircleOptions()
//                .center( geoFenceMarker.getPosition())
//                .strokeColor(Color.argb(50, 70,70,70))
//                .fillColor( Color.argb(100, 150,150,150) )
//                .radius( GEOFENCE_RADIUS );
//        geoFenceLimits = map.addCircle( circleOptions );
//    }
}

