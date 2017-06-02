package com.radiomatalelaki.app;

/**
 * Created by rianpradana on 5/28/17.
 */

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.radiomatalelaki.EventActivity;
import com.radiomatalelaki.GPS.TrackGPS;
import com.radiomatalelaki.MainActivity;
import com.radiomatalelaki.R;
import com.radiomatalelaki.api.Events;
import com.radiomatalelaki.util.Config;
import com.radiomatalelaki.util.NotificationUtils;
import com.radiomatalelaki.util.Utils;
import com.radiomatalelaki.util.database.EventDistanceComparator;
import com.radiomatalelaki.util.database.EventTitleComparator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MyFirebaseMessagingService extends FirebaseMessagingService implements LocationListener {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private LatLng currentLocation;

    private LocationManager locationManager;
    private String provider;

    private ArrayList<Events> events;
    public TrackGPS gps;
    public float lat;
    public float lng;
    public float lat_api;
    public float lng_api;
    double longitude;
    double latitude;
    private NotificationUtils notificationUtils;
    private SeekBar radiusBar;

    private boolean radiusProgressIdle = true;
    private String jsonResponse;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        gps = new TrackGPS(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
        }

        getLocation(lat,lng,remoteMessage, true);

//        if (remoteMessage == null)
//            return;
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());
//        }
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
//
//            try {
//                JSONObject json = new JSONObject(remoteMessage.getData().toString());
//                handleDataMessage(json);
//            } catch (Exception e) {
//                Log.e(TAG, "Exception: " + e.getMessage());
//            }
//        }
    }

    private void getLocation(final double lat, final double lon, final RemoteMessage remoteMessage, final boolean calculateDistance) {
//        Ion.with(getApplicationContext())
//                .load(getString(R.string.json_url))
//                .as(new TypeToken<com.radiomatalelaki.api.Response>(){})
//                .setCallback(new FutureCallback<com.radiomatalelaki.api.Response>() {
//                    @Override
//                    public void onCompleted(Exception e, com.radiomatalelaki.api.Response response) {
//                        if (e!=null) {
//                            // manage the exception here
//                        } else {
//                            List<Events> allEvents = response.getEvents();
//                            events = new ArrayList<Events>();
//                            for (int i=0; i<allEvents.size(); i++) {
//                                Events ev = allEvents.get(i);
//                                lat_api = (float) ev.getLat();
//                                lng_api = (float) ev.getLng();
//                                double jumlah = Utils.getDistanceBetweenTwoPoints(
//                                        new PointF((float) lat, (float) lon),
//                                        new PointF((float) ev.getLat(),
//                                                (float) ev.getLng()));
//                                Log.e("Jumlah = ","jumlah"+jumlah);
//                                Log.e("locats", "location database: Longitude"+lng_api+" Latitude:"+lat_api+ "  ," +
//                                        "location gw: Longitude" + lng + " Latitude:"+lat);
//
//                                getJarak(lat,lng,lat_api,lng_api);
//
//                            }
//                            if(calculateDistance) {
//                                // sort by distance
//                                Collections.sort(events, new EventDistanceComparator());
//                            } else {
//                                // sort by title
//                                Collections.sort(events, new EventTitleComparator());
//                            }
//                        }
//                    }
//                });
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.POST, "http://api.mantheqabaidha.com/api/event", (String) null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject loc = (JSONObject) response
                                        .get(i);

                                String lats = loc.getString("lat");
                                String longs = loc.getString("lng");
                                int around = Integer.parseInt(loc.getString("around"));

                                Log.e("ls",lats + longs);
                                getJarak(lats,longs,lat,lon,around,remoteMessage);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(req);
    }

    private void getJarak(String lat, String lng, double lat_api, double lng_api, int around, RemoteMessage remoteMessage) {
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + lat + "," + lng + "&destinations=" + lat_api + "," + lng_api + "&key=%20AIzaSyCWpwVwu1hO6TJW1H8x_zlhrLfbSbQ2r3o";
        Log.e("url",url);
        Location distance1 = new Location("");
        distance1.setLatitude(Double.parseDouble(lat));
        distance1.setLongitude(Double.parseDouble(lng));


        Location distance2 = new Location("");
        distance2.setLatitude(Double.parseDouble(String.valueOf(lat_api)));
        distance2.setLongitude(Double.parseDouble(String.valueOf(lng_api)));

        int jarak = Integer.parseInt(String.valueOf(calculateDistance(distance1,distance2)));
            Log.e("dis","jarak"+ jarak + ", around"+ around);
        if (jarak >= around){
            Log.e("status","ngelebihin");
        }else if(around == 30000){
            showNotificationMessage(remoteMessage.getData().get("message"));
        }
        else{
            showNotificationMessage(remoteMessage.getData().get("message"));
        }
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                JSONObject jsonResponse;
//                try {
//                    jsonResponse = new JSONObject(response);
//                    JSONArray rows = jsonResponse.getJSONArray("rows");
//                    for (int i=0; i<rows.length(); i++) {
//                        JSONArray elements = rows.getJSONObject(i).getJSONArray("elements");
//                        for (int j=0; j<elements.length(); j++) {
//                            JSONArray distance = elements.getJSONObject(i).getJSONArray("distance");
//                            Log.e("routes",distance.toString());
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,(String) null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    JSONArray jsonArray = response.getJSONArray("rows");
//                    for (int i = 0; i < jsonArray.length(); i++){
////
//                        JSONObject elements = jsonArray.getJSONObject(0);
//                        JSONArray element1 = elements.getJSONArray("elements");
//
//                        JSONObject distance = element1.getJSONObject(0);
//                        String distance1 = distance.getString("distance");
//
//                        Log.d("legs",distance1.toString());
//                        Log.e("legs",distance1.toString());
////                        String km1 = distance1.getString("");
////                        JSONObject km = distance.getJSONObject("value");
////                        Log.d("legs",distance.toString());
//////                        for (int ii = 0; ii < legs1.length(); ii++){
//////                            JSONObject distance = jsonArray.getJSONObject(i);
//////                            JSONArray distance1 = distance.getJSONArray("distance");
//////                            Log.e("distance", distance1.toString());
//////                        }
//
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        AppController.getInstance().addToRequestQueue(stringRequest);


    }
    private long calculateDistance(Location location, Location location2) {


        double lat1= location.getLatitude();
        double lng1 = location.getLongitude();

        double lat2= location2.getLatitude();
        double lng2 = location2.getLongitude();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMeters = Math.round(6371000 * c);
//        Log.e("dista", String.valueOf(distanceInMeters));
        return distanceInMeters;
    }
    private void showNotificationMessage(String message) {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Mata Lelaki")
                .setContentText(message)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = (float) (location.getLatitude());
        lng = (float) (location.getLongitude());

        Log.e("locatin", "Longitude:"+lng+"\nLatitude:"+lat);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}