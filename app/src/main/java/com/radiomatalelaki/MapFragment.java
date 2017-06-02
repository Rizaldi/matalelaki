package com.radiomatalelaki;

/**
 * Created by rianpradana on 5/26/17.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.radiomatalelaki.api.Events;
import com.radiomatalelaki.util.EventsDataFragment;
import com.radiomatalelaki.util.JSONSharedPreferences;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The type Map fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, EventsDataFragment {

    private static View view;

    // The activity container
    private Context context;

    // The map
    private GoogleMap mMap;

    // Correlation between markers and events
    private HashMap<Marker, Events> eventMarkerMap;

    // Events list
    private ArrayList<Events> events;

    /**
     * Instantiates a new Map fragment.
     */
    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
            SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        return view;
    }

    private void setUpMapIfNeeded(GoogleMap map) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = map;
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);

                // load events locations
                refreshMap();

				/* Add click listener to info window */
                mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker marker) {
						/* Retrieve Json event, save in the shared preferences and start activity */
                        Events eventInfo = eventMarkerMap.get(marker);
                        JSONSharedPreferences.saveEvent(eventInfo, context);
                        Intent intent = new Intent(context, EventDetailsActivity.class);
                        startActivity(intent);
                    }

                });

				/* Check gps permission. If granted, enable user location indicator on map */
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onResume() {
        super.onResume();

		/* reload events on map */
        events = ((EventActivity)context).getEvents();
        refreshMap();
    }

    /* Refresh map, adding markers */
    private void refreshMap () {
        Log.d("check", (events != null)+" - "+(mMap!=null));
        if (events!=null && mMap!=null) {
            mMap.clear();
            eventMarkerMap = new HashMap<Marker, Events>();

            // Initializes location builder, useful to fit map to markers area
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < events.size(); i++) {
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(events.get(i).getLat(),
                                events.get(i).getLng()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker1))
                        .title(events.get(i).getTitle().trim())
                        .snippet(events.get(i).getAddress().trim()));

                // add marker to hashmap, useful to retrieve event data
                eventMarkerMap.put(m, events.get(i));

                builder.include(m.getPosition());
            }

            if (events.size() > 0) {
                // move camera ccording to marker area
                LatLngBounds bounds = builder.build();
                final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                try {
                    this.mMap.moveCamera(cu);
                } catch (IllegalStateException e) {
                    // layout not yet initialized
                    final View mapView = getChildFragmentManager().findFragmentById(R.id.map).getView();
                    if (mapView.getViewTreeObserver().isAlive()) {
                        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                            @SuppressLint("NewApi")
                            @SuppressWarnings("deprecation")
                            // We check which build version we are using.
                            @Override
                            public void onGlobalLayout() {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                } else {
                                    mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                                mMap.moveCamera(cu);
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // initialize the map
        setUpMapIfNeeded(googleMap);
    }

    /* Method implementation for EventsDataFragment interface */
    @Override
    public void refreshData(ArrayList<Events> events) {
        this.events = events;
        refreshMap();
    }
}
