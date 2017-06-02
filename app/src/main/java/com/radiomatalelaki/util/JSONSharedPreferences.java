package com.radiomatalelaki.util;

/**
 * Created by rianpradana on 5/26/17.
 */

import com.radiomatalelaki.api.Events;

import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * The type Json shared preferences.
 */
public class JSONSharedPreferences {

    private static final String PREFIX = "json";

    /**
     * Save json object.
     *
     * @param c        the context
     * @param prefName the shared preference name
     * @param key      the shared preference key
     * @param object   the object
     */
    public static void saveJSONObject(Context c, String prefName, String key, JSONObject object) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(JSONSharedPreferences.PREFIX+key, object.toString());
        editor.commit();
    }

    /**
     * Save json array.
     *
     * @param c        the context
     * @param prefName the shared preference name
     * @param key      the shared preference key
     * @param array    the array
     */
    public static void saveJSONArray(Context c, String prefName, String key, JSONArray array) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(JSONSharedPreferences.PREFIX+key, array.toString());
        editor.commit();
    }

    /**
     * Load json object.
     *
     * @param c        the context
     * @param prefName the shared preference name
     * @param key      the key
     * @return the json object
     * @throws JSONException the json exception
     */
    public static JSONObject loadJSONObject(Context c, String prefName, String key) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return new JSONObject(settings.getString(JSONSharedPreferences.PREFIX+key, "{}"));
    }

    /**
     * Load json array.
     *
     * @param c        the context
     * @param prefName the shared preference name
     * @param key      the key
     * @return the json array
     * @throws JSONException the json exception
     */
    public static JSONArray loadJSONArray(Context c, String prefName, String key) throws JSONException {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        return new JSONArray(settings.getString(JSONSharedPreferences.PREFIX+key, "[]"));
    }

    /**
     * Remove.
     *
     * @param c        the context
     * @param prefName the shared preference name
     * @param key      the key
     */
    public static void remove(Context c, String prefName, String key) {
        SharedPreferences settings = c.getSharedPreferences(prefName, 0);
        if (settings.contains(JSONSharedPreferences.PREFIX+key)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(JSONSharedPreferences.PREFIX+key);
            editor.commit();
        }
    }

    /**
     * Save events.
     *
     * @param events the events
     * @param context the context
     */
    public static void saveEvents (List<Events> events, Context context) {
        JSONArray a = new JSONArray();
        for (int i=0; i<events.size(); i++) {
            Gson gson = new Gson();
            String json = gson.toJson(events.get(i));
            a.put(json);
        }
        saveJSONArray(context, Global.SHARED_PREFERENCES, Global.SHARED_EVENTS, a);
    }

    /**
     * Save event.
     *
     * @param e       the event
     * @param context the context
     */
    public static void saveEvent (Events e, Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(e);
        JSONObject o = new JSONObject();
        try {
            o = new JSONObject(json);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        saveJSONObject(context, Global.SHARED_PREFERENCES, Global.SHARED_EVENT, o);
    }

}

