package com.radiomatalelaki;

/**
 * Created by rianpradana on 5/26/17.
 */


import android.text.Html;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.radiomatalelaki.api.Events;
import com.radiomatalelaki.util.Global;
import com.radiomatalelaki.util.JSONSharedPreferences;
import com.radiomatalelaki.util.database.DatabaseHelper;
import com.radiomatalelaki.util.database.EventDateUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Event details activity.<br/>
 * In this activity you can show all the event details. There is distance label, date, address,
 * description, title, etc.
 */
public class EventDetailsActivity extends Activity {

    // set to false if you don't want to show ads
    private boolean showAd = false;

    /* Banner AD View */
    private AdView mAdView;

    // Event type
    private Events event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        /* View title */
        TextView title = (TextView) findViewById(R.id.barTitle);
        title.setText(getString(R.string.title_activity_event_details).toUpperCase());

        // Distance value
        TextView distanceValue = (TextView) findViewById(R.id.mtValueLabel);

        /* Button to open the map with directions */
        Button takeMeThereButton = (Button) findViewById(R.id.buttonTakeMeThere);
        takeMeThereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeMeThere();
            }
        });

        /* Button, back to home */
        ImageButton backButton = (ImageButton) findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* Initialize Event: read Jsom SHARED_EVENT from SharedPreferences */
        JSONObject o = null;
        event = new Events();
        try {
            o = JSONSharedPreferences.loadJSONObject(this, Global.SHARED_PREFERENCES, Global.SHARED_EVENT);
            Gson gson = new Gson();
            String json = o.toString();
            event = gson.fromJson(json, Events.class);
        } catch (JSONException eJson) {
            eJson.printStackTrace();
        }

		/*
		Show the distance value in the label.
		You can change the measure unit and the scale...
		*/
        int distance = (int)event.getDistance();
        TextView mtLabel = (TextView) findViewById(R.id.mtLabel);
        if (distance<1000) {
            distanceValue.setText(""+distance);
            mtLabel.setText("MT");
        } else {
            distanceValue.setText(""+distance/1000);
            mtLabel.setText("KM");
        }

        /* Show the event title */
        TextView titleRow = (TextView) findViewById(R.id.titleRow);
        titleRow.setText(event.getTitle().trim());

        /* Show the event short description */
        TextView subtitle = (TextView) findViewById(R.id.subtitleRow);
        subtitle.setText(event.getBrief_description().trim());

        /* Show the address */
        TextView address = (TextView) findViewById(R.id.addressRow);
        address.setText(event.getAddress().trim());

        /*
        Show the clickable info.
        In this case we choose to display the website, but you can customize the intent: you can
        display a phone number or a mail and launch the right intent.
        */
        TextView infoTextView = (TextView) findViewById(R.id.infoTextView);
        infoTextView.setText(event.getUrl().trim());
        infoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailsActivity.this, WebViewActivity.class);
                intent.putExtra(Global.EXTRA_URL_WEBVIEW, event.getUrl().trim());
                startActivity(intent);
            }
        });

        /* Show a pretty-printed date for the event */
        TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        try {
            dateTextView.setText(EventDateUtils.getPrettyString(event));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* Scrollable description view */
        TextView descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(Html.fromHtml(event.getDescription()));

		/* Set the banner ad view */
        mAdView = (AdView) findViewById(R.id.adView);
        if (showAd) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.event_details, menu);
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Take me there.
     */
    public void takeMeThere () {

        SharedPreferences settings = getSharedPreferences(Global.SHARED_PREFERENCES, 0);
        Intent newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+settings.getFloat(Global.SHARED_USERLOCATION_LAT, 44.834f)+","+
                settings.getFloat(Global.SHARED_USERLOCATION_LON, 11.619f)+"&daddr="+
                event.getLat()+","+event.getLng()+"&dirflg=w"));
        startActivity(newIntent);
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

}
