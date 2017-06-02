package com.radiomatalelaki.api;

/**
 * Created by rianpradana on 5/26/17.
 */

import org.json.*;
import java.util.ArrayList;

public class Response {

    private ArrayList<Events> events;


    public Response () {

    }

    public Response (JSONObject json) {


        this.events = new ArrayList<Events>();
        JSONArray arrayEvents = json.optJSONArray("events");
        if (null != arrayEvents) {
            int eventsLength = arrayEvents.length();
            for (int i = 0; i < eventsLength; i++) {
                JSONObject item = arrayEvents.optJSONObject(i);
                if (null != item) {
                    this.events.add(new Events(item));
                }
            }
        }
        else {
            JSONObject item = json.optJSONObject("events");
            if (null != item) {
                this.events.add(new Events(item));
            }
        }


    }

    public ArrayList<Events> getEvents() {
        return this.events;
    }

    public void setEvents(ArrayList<Events> events) {
        this.events = events;
    }



}

