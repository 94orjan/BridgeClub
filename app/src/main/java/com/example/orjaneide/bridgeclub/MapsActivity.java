package com.example.orjaneide.bridgeclub;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final String URL = "SomeFile";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private FloatingSearchView mSearchView;
    private LocationRequest mLocationRequest;
    private static final String LOG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Read in XML file
        loadXml();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng init = new LatLng(65, 15);
        LatLng bergen = new LatLng(60.4, 5.32);

        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(bergen));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(init, (float) 4.3));

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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
                List<Address> list = null;
                try {
                    list = gc.getFromLocationName(currentQuery, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = list.get(0);

                double lat = address.getLatitude();
                double lng = address.getLongitude();
                goToLocationZoom(lat, lng, 12);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Toast.makeText(this, "Finner ikke lokasjonen din.", Toast.LENGTH_LONG).show();
        }
        else{
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
            mMap.animateCamera(update);
        }

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

    private void addAllMarkersToMap() {
    }


    public void loadXml() {
        new DownloadXmlTask().execute();
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, List<Club>> {
        @Override
        protected List<Club> doInBackground(String... strings) {
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
            Log.d(TAG, clubs.size() + " clubs!");
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