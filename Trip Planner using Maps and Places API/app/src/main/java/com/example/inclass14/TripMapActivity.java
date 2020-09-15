package com.example.inclass14;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class TripMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Places> placesArrayList = new ArrayList<>();
    ArrayList<LatLng> latLngPoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_map);

        setTitle("Trip Map");

        placesArrayList = (ArrayList<Places>) getIntent().getExtras().getSerializable("Details");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        String[] names = new String[placesArrayList.size()];
        int i = 0;

        for(Places place : placesArrayList){
            LatLng latLng1 = new LatLng(place.getLat(), place.getLng());
            latLngPoints.add(latLng1);
            Log.d("demo", "Points: "+ latLngPoints.toString());
            names[i] = place.getName();
            i++;
        }
        i = 0;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng latLng : latLngPoints){
            String s = names[i];
            mMap.addMarker(new MarkerOptions().position(latLng))
                    .setTag(s);
            builder.include(latLng);
            i++;
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(TripMapActivity.this, marker.getTag().toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        final LatLngBounds latLngBounds = builder.build();

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 40));
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 40));
            }
        });
    }
}
