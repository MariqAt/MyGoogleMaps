package com.ittalents.mygooglemaps;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ittalents.mygooglemaps.model.DirectionFinder;
import com.ittalents.mygooglemaps.model.IDirectionFinderListener;
import com.ittalents.mygooglemaps.model.Route;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, IDirectionFinderListener {

    private GoogleMap mMap;
    private Button searchButton;
    private  LatLng myLocation;
    private  LatLng searchCordinati;
    private String locationSrch;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchButton = (Button) findViewById(R.id.fine_location_button);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        myLocation = new LatLng(42.6711388, 23.2933966);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("My position"));

//        mMap.addCircle(new CircleOptions()
//                .center(myLocation)
//                .radius(400)
//                .strokeColor(Color.BLACK)
//                .fillColor(Color.GREEN));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
//        CameraPosition target = CameraPosition.builder().target(myLocation).zoom(11).build();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

    public void findLocation(View view) {

        EditText searchLocation = (EditText) findViewById(R.id.search_location);
        locationSrch = searchLocation.getText().toString();
        Log.d("test", locationSrch.toString());
        if (locationSrch != null && !locationSrch.isEmpty()) {

            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(locationSrch, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            searchCordinati = new LatLng(address.getLatitude(), address.getLongitude());
            Log.d("test",address.getLatitude()+"");
            Log.d("test",address.getLongitude()+"");
            mMap.addMarker(new MarkerOptions().position(searchCordinati).title("Search Location"));
//            mMap.addCircle(new CircleOptions()
//                    .center(searchCordinati)
//                    .radius(400)
//                    .strokeColor(Color.BLACK)
//                    .fillColor(Color.RED));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(searchCordinati));

//            mMap.addPolyline(new PolylineOptions()
//                    .add(myLocation, searchCordinati)
//                    .width(10)
//                    .color(Color.BLUE)
//            );

            sendRequest();
        }

    }

    private void sendRequest() {

        String origin = "София, бул. България 141";
        String destination = locationSrch;
        Log.d("testLoc1", origin);
        Log.d("testLoc2", destination);
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDirectionFinderStart() {
        Log.d("test2", "onDirectionFinderStart");

//        progressDialog = ProgressDialog.show(this, "Please wait.",
//                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        Log.d("test222", "onDirectionFinderSuccess");
//        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        Log.d("test222", routes.size() + "");
        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            Log.d("test222", route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            Log.d("test222", route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
}