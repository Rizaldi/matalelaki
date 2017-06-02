package com.radiomatalelaki.util.database;

/**
 * Created by rianpradana on 5/26/17.
 */

import com.radiomatalelaki.api.Events;

import java.util.Comparator;

/**
 * The type Event distance comparator.
 */
public class EventDistanceComparator implements Comparator<Events> {

    /* Compare events by distance */
    @Override
    public int compare(Events arg0, Events arg1) {
        return arg0.compareTo(arg1);
    }

}