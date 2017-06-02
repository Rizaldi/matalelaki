package com.radiomatalelaki;

/**
 * Created by rianpradana on 5/26/17.
 */


import com.radiomatalelaki.api.Events;
import com.radiomatalelaki.api.Response;
import com.radiomatalelaki.util.EventsDataFragment;
import com.radiomatalelaki.util.Global;
import com.radiomatalelaki.util.UserLocation;
import com.radiomatalelaki.util.Utils;
import com.radiomatalelaki.util.database.DatabaseHelper;
import com.radiomatalelaki.util.database.EventDistanceComparator;
import com.radiomatalelaki.util.database.EventTitleComparator;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * The type Main activity.
 */
public class EventActivity extends AppCompatActivity {

    // set to false if you don't want to show ads
    private boolean showAd = false;

    /* REQUEST_CODE for android 6.0 gps permission */
    private static final int REQUEST_PERMISSION_CODE = 255;

    /* Data model. Use sqlite local database or remote json api */
    private static final int MODE_SQLITE = 100;
    private static final int MODE_JSON_API = 101;

    // data model variable. You can set it via setDataMode method
    private int dataMode = MODE_SQLITE;

    /**
     * The distance radius.
     */
    public int radius;

    /* TabHost: this main activity has 2 tabs. List (ListFragment) and map (MapFragment) */
    private FragmentTabHost mTabHost;
    private TabWidget tabWidget;

    /* DB SQLite helper */
    private DatabaseHelper myDbHelper;

    /* Radius progerssbar unchanged */
    private boolean radiusProgressIdle = true;

    /* Events list */
    private ArrayList<Events> events;

    /* Overlay help view */
    private View help;

    /* Distance labels and slider */
    private TextView actDistLabel;
    private SeekBar radiusBar;
    private TextView maxDistLabel;

    /* Search button and text field */
    private Button searchButton;
    private EditText searchEditText;

    /* Last known user locatiom */
    private LatLng currentLocation;

    /* True if helpView is visible */
    private boolean isInfoVisible;

    /* Banner AD Views */
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    // used to retrieve current user location
    private UserLocation.LocationResult locationResult = new UserLocation.LocationResult() {
        @Override
        public void gotLocation(Location location) {
            if (location!=null) {
                // you can move camera
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                // save the last known user location
                SharedPreferences settings = getSharedPreferences(Global.SHARED_PREFERENCES, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat(Global.SHARED_USERLOCATION_LAT, (float) currentLocation.latitude);
                editor.putFloat(Global.SHARED_USERLOCATION_LON, (float) currentLocation.longitude);
                editor.commit();

                search();
            }
        }
    };

    /**
     *  SQLite helper init
     */
    private void initSQLite() {
        if (myDbHelper==null) {
            myDbHelper = new DatabaseHelper(this);
            try {
                myDbHelper.createDataBase();
            } catch (IOException ioe) {
                throw new Error("Unable to create database");
            }
            try {
                myDbHelper.openDataBase();
            } catch (SQLException sqle) {
                Log.e("error", sqle.getMessage());
            }
        } else {
            myDbHelper.openDataBase();
        }
    }

    /**
     *  SQLite helper deinit
     */
    private void deinitSQLite() {
        if (myDbHelper!=null)
            myDbHelper.close();
    }

    /**
     * Gets events.
     *
     * @return the events list
     */
    public ArrayList<Events> getEvents() {
        return events;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

		/* initialize variables */
        isInfoVisible = true;
        events = new ArrayList<Events>();
        radius = 1500;

        // set data mode
        setDataMode(MODE_JSON_API);

		/* Init tabhost and tabwidget */
        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

            public void onTabChanged(String str) {
                InputMethodManager inputManager = (InputMethodManager)
                        EventActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (EventActivity.this.getCurrentFocus() != null)
                    inputManager.hideSoftInputFromWindow(EventActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        tabWidget = (TabWidget) findViewById(R.id.tabs);
        setTabs(); // add list and map fragments

		/*
		HELP VIEW
		By default, his help view is always visible at app startup.
		TODO: You can customize this code to display the helpview only first time, using shared preferences
		 */
        help = findViewById(R.id.helpView);
        help.setVisibility(isInfoVisible ? View.VISIBLE : View.GONE);
        help.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // This is useful to prevent that touch event trespassing the help view
                return true;
            }
        });

		/* View title */
        TextView title = (TextView) findViewById(R.id.barTitle);
        title.setText(getString(R.string.title_activity_events_list).toUpperCase());

		/* Slider radius view and labels */
        actDistLabel = (TextView) findViewById(R.id.actDistanceLabel);
        actDistLabel.setText(String.format("%.2f", 1.5)+" km");
        maxDistLabel = (TextView) findViewById(R.id.maxDistanceLabel);
        radiusBar = (SeekBar) findViewById(R.id.seekBarRange);
        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                String distance = arg1 + " km";
                actDistLabel.setText(distance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                radiusProgressIdle = false;
                search();
            }

        });
        radiusBar.setProgress(radius);
        radiusBar.setEnabled(false); // enable this when you have the max distance value

		/* Startbutton hides the help view */
        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                hideHelp();
            }
        });

		/* Textfield and button used to search by event name or address  */
        searchButton = (Button) findViewById(R.id.buttonSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchText();
            }

        });
        searchEditText = (EditText) findViewById(R.id.searchTextEdit);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL) {
                    hideHelp();
                    search();//match this behavior to your 'Send' (or Confirm) button
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(searchEditText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;
            }

        });
        searchEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                searchEditText.setText("");
                search();
            }

        });
        searchEditText.setEnabled(false);
        searchButton.setEnabled(false);

        // enable user location (request android 6.0 permission)
        enableUserLocation();

        // load events
        search();

		/* Set the banner ad view */
        mAdView = (AdView) findViewById(R.id.adView);
        if (showAd) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            // Create the InterstitialAd and set the adUnitId.
            mInterstitialAd = new InterstitialAd(this);
            // Defined in res/values/strings.xml
            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial));
            if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
                AdRequest adRequestInterst = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequestInterst);
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mInterstitialAd.show();
                    }
                });
            }
        } else {
            mAdView.setVisibility(View.GONE);
        }
    }

    /**
     * Hide help.
     */
    public void hideHelp() {
        isInfoVisible = false;
        help.setVisibility(View.GONE);
        searchEditText.setEnabled(true);
        searchButton.setEnabled(true);
    }

    /**
     * Methid called by searchButton. Search by text field value.
     */
    public void searchText() {
        hideHelp();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchEditText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        search();
    }

    /* load events by radius and textfield parameters */
    private void search() {
        if (currentLocation==null) { // if current location unavailable... search all events
            search(0, 0, -1, searchEditText.getText().toString().trim(), false);
        } else { // else search by radius (if set) and others parameters
            search(currentLocation.latitude, currentLocation.longitude, !radiusProgressIdle?radiusBar.getProgress():-1, searchEditText.getText().toString().trim(), true);
        }
    }

    /**
     * If true (and the user grant permissions), the map display a blue circle
     * at the user location.
     */
    public void enableUserLocation() {
        // request gps permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EventActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_CODE);
            return;
        }

		/* start searching user location */
        final UserLocation userLocation = new UserLocation();
        userLocation.getLocation(EventActivity.this, locationResult);
    }

    /* Load map and list fragments */
    private void setTabs()
    {
        addTab(getString(R.string.tab_list), ListFragment.class);
        addTab(getString(R.string.tab_map), MapFragment.class);
    }

    /* Associate fragments and tabs */
    private void addTab(String labelId, Class<?> c)
    {
        FragmentTabHost.TabSpec spec = mTabHost.newTabSpec("tab" + labelId);

        View tabIndicator = getLayoutInflater().inflate(R.layout.tab_indicator, tabWidget, false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(labelId);

        spec.setIndicator(tabIndicator);
        mTabHost.addTab(spec, c, null);
    }

    @Override
    public void onBackPressed() {
		/*
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventActivity.this);
        alertDialogBuilder.setTitle(R.string.exit);
        alertDialogBuilder.setMessage(R.string.exit_question)
                .setCancelable(false)
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();Ask if the user wants to exit  */
		finish();
    }

    @Override
    public void onDestroy() {
        deinitSQLite();
        if (mAdView != null) {
            mAdView.destroy();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        deinitSQLite();
        if (mAdView != null) {
            mAdView.pause();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        //initSQLite();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Search in the events database.
     *
     * @param lat               the latitude
     * @param lon               the longitude
     * @param distance          the distance
     * @param searchText        the search text
     * @param calculateDistance calculate the distance (in meters)
     */
    public void search(final double lat, final double lon, final int distance, String searchText, final boolean calculateDistance) {

        int distanceRadius = distance;
        Ion.with(EventActivity.this)
                .load(getString(R.string.json_url))
                .as(new TypeToken<Response>(){})
                .setCallback(new FutureCallback<Response>() {
                    @Override
                    public void onCompleted(Exception e, Response response) {
                        if (e!=null) {
                            // manage the exception here
                        } else {
                            List<Events> allEvents = response.getEvents();
                            events = new ArrayList<Events>();
                            for (int i=0; i<allEvents.size(); i++) {
                                Events ev = allEvents.get(i);
                                if (calculateDistance) {
                                    ev.setDistance(Utils.getDistanceBetweenTwoPoints(
                                            new PointF((float) lat, (float) lon),
                                            new PointF((float) ev.getLat(),
                                                    (float) ev.getLng())));
                                }

                                if (distance>=0 && ev.getDistance() < distance * 1000) {
                                    events.add(ev);
                                } else if (distance < 0) {
                                    events.add(ev);
                                }
                            }
                            if(calculateDistance) {
                                // sort by distance
                                Collections.sort(events, new EventDistanceComparator());
                            } else {
                                // sort by title
                                Collections.sort(events, new EventTitleComparator());
                            }
                            updateUIAndData(lat, lon, distance);
                        }
                    }
                });
        /*switch (dataMode) {
            case MODE_SQLITE: {
                initSQLite();
                try {
                    events = myDbHelper.getAllEvents(lat, lon, distanceRadius, searchText, calculateDistance);
                    Log.d("events", "event size is " + events.size());
                } catch (SQLException sqle) {
                    events = new ArrayList<Events>();
                }
                updateUIAndData(lat, lon, distance);
            } break;
            case MODE_JSON_API: {
                Ion.with(EventActivity.this)
                        .load(getString(R.string.json_url))
                        .as(new TypeToken<Response>(){})
                        .setCallback(new FutureCallback<Response>() {
                            @Override
                            public void onCompleted(Exception e, Response response) {
                                if (e!=null) {
                                    // manage the exception here
                                } else {
                                    List<Events> allEvents = response.getEvents();
                                    events = new ArrayList<Events>();
                                    for (int i=0; i<allEvents.size(); i++) {
                                        Events ev = allEvents.get(i);
                                        if (calculateDistance) {
                                            ev.setDistance(Utils.getDistanceBetweenTwoPoints(
                                                    new PointF((float) lat, (float) lon),
                                                    new PointF((float) ev.getLat(),
                                                            (float) ev.getLng())));
                                        }

                                        if (distance>=0 && ev.getDistance() < distance * 1000) {
                                            events.add(ev);
                                        } else if (distance < 0) {
                                            events.add(ev);
                                        }
                                    }
                                    if(calculateDistance) {
                                        // sort by distance
                                        Collections.sort(events, new EventDistanceComparator());
                                    } else {
                                        // sort by title
                                        Collections.sort(events, new EventTitleComparator());
                                    }
                                    updateUIAndData(lat, lon, distance);
                                }
                            }
                        });

            } break;
            default: break;
        }*/
    }

    private void updateUIAndData(double lat, double lon, int distance) {
        radius = distance;

		/* If lat and lon are specified and the user don't filter by distance */
		/* EDITED */
        if (lat!=0 && lon!=0 && !radiusBar.isEnabled() && events.size()>0) {
		/* END EDITED */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    radiusBar.setEnabled(true);
                    maxDistLabel.setText(((int)(events.get(events.size()-1).getDistance()/1000.f)+1)+" km");
                    radiusBar.setMax(((int)(events.get(events.size()-1).getDistance()/1000.f))+1);
                }
            });
        }

		/*
		Refresh current fragment (map or list).
		Fragment has to implement the EventsDataFragment interface
		*/
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.tabcontent);
        if (currentFragment!=null) {
            ((EventsDataFragment) currentFragment).refreshData(events);
        }
    }

    public void setDataMode(int dataMode) {
        this.dataMode = dataMode;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
			/* EDITED */
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission. ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        // user allows to retrieve his location
                        enableUserLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                break;
			/* END EDITED */
            default:
                break;
        }
    }

}

