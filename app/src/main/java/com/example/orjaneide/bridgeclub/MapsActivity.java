package com.example.orjaneide.bridgeclub;

import android.*;
import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.orjaneide.bridgeclub.model.Club;
import com.example.orjaneide.bridgeclub.util.ClubXmlParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    // Logging tag
    private static final String TAG = MapsActivity.class.getSimpleName();

    // Constants
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 9;

    // Member variables
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private FloatingSearchView mSearchView;
    private LocationRequest mLocationRequest;
    private Geocoder mGeocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        if(mGeocoder == null) {
            mGeocoder = new Geocoder(this);
        }

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        // Read in XML file
        loadXml();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is being created");
        LatLng init = new LatLng(65, 15);
        LatLng bergen = new LatLng(60.4, 5.32);

        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(bergen));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(init, (float) 4.3));
        // TODO addAllMarkersToMap();

        addInfoWindowToMarker();

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                Geocoder gc = new Geocoder(MapsActivity.this);
                List<Address> list;

                try {
                    list = gc.getFromLocationName(currentQuery, 1);
                    if(list.size() > 0) {
                        Address address = list.get(0);
                        double lat = address.getLatitude();
                        double lng = address.getLongitude();
                        goToLocationZoom(lat, lng, 12);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
            Log.d(TAG, "mGoogleApiClient connect method has been called!");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d(TAG, "mGoogleApiClient is disconnected");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected called!");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Location Permission")
                        .setMessage("The user experience will be better if you allow us to use your location")
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MapsActivity.this, "App may not work as expected without the location", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
            }

            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(Location location) {
        if(location != null) {
            Log.d(TAG, location.toString());
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
            mMap.animateCamera(update);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended called!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged called!");
        handleNewLocation(location);
    }

    private void  goToLocationZoom(double lat, double lng, float zoom){
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }

    private void addInfoWindowToMarker() {
        if(mMap != null){
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView clubName = (TextView) v.findViewById(R.id.clubName);
                    TextView place = (TextView) v.findViewById(R.id.place);
                    TextView address = (TextView) v.findViewById(R.id.address);
                    TextView contactPerson = (TextView) v.findViewById(R.id.contactPerson);
                    TextView times = (TextView) v.findViewById(R.id.times);
                    TextView webPage = (TextView) v.findViewById(R.id.webPage);
                    TextView email = (TextView) v.findViewById(R.id.email);
                    TextView phone = (TextView) v.findViewById(R.id.phone);

                    LatLng ll = marker.getPosition();
                    return v;
                }
            });
        }
    }

    private void addAllMarkersToMap(List<Club> clubs) throws IOException {
        List<Address> addresses;
        LatLng current;

        // TODO: Find a way to add each individul club a marker to the map
        for(Club club : clubs) {
            addresses = mGeocoder.getFromLocationName(club.getAddress(), 1);
            double lat = addresses.get(0).getLatitude();
            double lng = addresses.get(0).getLongitude();
            current = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(current));
        }

    }



    // LOADING LOCAL XML FILE CODE
    public void loadXml() {
        new DownloadXmlTask().execute();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<Club>> {
        @Override
        protected List<Club> doInBackground(String... strings) {
            Log.d(TAG, "doInBackground for XML called!");
            try {
                return loadXmlFromFile();
            } catch (IOException e) {
                return new ArrayList<>();
            } catch (XmlPullParserException e) {
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<Club> clubs) {
            Log.d(TAG, "onPostExecute called!");
            if(clubs.size() > 0) {
                try {
                    addAllMarkersToMap(clubs);
                } catch(IOException e) {
                    Toast.makeText(MapsActivity.this, "Errors with displaying clubs on maps", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MapsActivity.this, "Error with downloading the info", Toast.LENGTH_LONG).show();
            }
        }
    }

    private List<Club> loadXmlFromFile() throws IOException, XmlPullParserException {
        InputStream in = getResources().openRawResource(R.raw.mockdata);
        ClubXmlParser clubXmlParser = new ClubXmlParser();

        Log.d(TAG, "Inside XmlFromFile");

        try {
            return clubXmlParser.parse(in);
        } finally {
            in.close();
        }
    }
}