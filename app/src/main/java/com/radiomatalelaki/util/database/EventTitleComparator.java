package com.radiomatalelaki.util.database;

/**
 * Created by rianpradana on 5/26/17.
 */


import com.radiomatalelaki.api.Events;

import java.util.Comparator;

/**
 * The type Event title comparator.
 */
public class EventTitleComparator implements Comparator<Events> {

    /* Compare events by title */
    @Override
    public int compare(Events arg0, Events arg1) {
        return arg0.getTitle().compareTo(arg1.getTitle());
    }

}
