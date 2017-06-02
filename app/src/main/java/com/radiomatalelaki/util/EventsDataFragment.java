package com.radiomatalelaki.util;


import com.radiomatalelaki.api.Events;

import java.util.ArrayList;

/**
 * Created by rianpradana on 5/26/17.
 */
public interface EventsDataFragment {

    /**
     * Refresh data.
     *
     * @param events the events
     */
    void refreshData(ArrayList<Events> events);

}