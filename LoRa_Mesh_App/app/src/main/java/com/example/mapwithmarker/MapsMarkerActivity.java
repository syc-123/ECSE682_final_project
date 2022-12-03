// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.mapwithmarker;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
// [START maps_marker_on_map_ready]
public class MapsMarkerActivity extends AppCompatActivity
        implements
//        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback
{


    private GoogleMap mMap;

    // [START_EXCLUDE]
    // [START maps_marker_get_map_async]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.BLUETOOTH_SCAN },
                1);
        ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.BLUETOOTH_CONNECT },
                1);
        ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.ACCESS_BACKGROUND_LOCATION },
                1);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    // [END maps_marker_get_map_async]
    // [END_EXCLUDE]

    // [START_EXCLUDE silent]
    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    // [END_EXCLUDE]
    // [START maps_marker_on_map_ready_add_marker]
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setOnInfoWindowClickListener(this);
        // [START_EXCLUDE silent]
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        // [END_EXCLUDE]

        // [START_EXCLUDE silent]

        // [END_EXCLUDE]

        LatLng McConnell = new LatLng(45.50607169886453, -73.57643327010648);
        Marker markerMc = googleMap.addMarker(new MarkerOptions()
                .position(McConnell)
                .title("McConnell Engineering Building")
                .snippet("Some basic info \n Click for more info"));

        LatLng YUL = new LatLng(45.49457773520816, -73.57384545605059);
        Marker markerY = googleMap.addMarker(new MarkerOptions()
                .position(YUL)
                .title("YUL")
                .snippet("Some basic info \n Click for more info"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(McConnell, (float)14.5));

    }
    // [END maps_marker_on_map_ready_add_marker]

//    @Override
//    public void onInfoWindowClick(Marker marker) {
////        Toast.makeText(this, "Info window clicked",
////                Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, InfoActivity.class);
//        startActivity(intent);
//    }

    public void ListWindow(View v){
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }


}
// [END maps_marker_on_map_ready]
