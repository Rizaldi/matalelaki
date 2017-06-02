package com.radiomatalelaki;

/**
 * Created by rianpradana on 5/26/17.
 */


import java.util.ArrayList;

import com.radiomatalelaki.api.Events;
import com.radiomatalelaki.util.EventsDataFragment;
import com.radiomatalelaki.util.EventsListAdapter;
import com.radiomatalelaki.util.JSONSharedPreferences;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;


/**
 * The type Events list fragment.
 */
public class ListFragment extends Fragment implements EventsDataFragment {

    // Custom ArrayListAdapter for the Event class
    private EventsListAdapter adapter;

    // ListView with the events list
    private ListView lv;

    // Events List
    private ArrayList<Events> events;

    private Context context;

    /**
     * Instantiates a new Events list fragment.
     */
    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater
                .inflate(R.layout.fragment_events_list, container, false);

        // Initialize events
        events = new ArrayList<Events>();

        /* Add OnItemClickListener to the listview */
        lv = (ListView) rootView.findViewById(R.id.list);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Save a Json string to sharedpreferences and launch the EventDetailsActivity */
                JSONSharedPreferences.saveEvent(events.get(position), context);
                Intent intent = new Intent(context, EventDetailsActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        /* refresh listview */
        events = ((EventActivity)context).getEvents();
        adapter = new EventsListAdapter(context, events, R.layout.event_list_item);
        lv.setAdapter(adapter);
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

    /* Method implementation for EventsDataFragment interface */
    @Override
    public void refreshData(ArrayList<Events> events) {
        this.events = events;

        /* refresh listview */
        adapter = new EventsListAdapter(context, events, R.layout.event_list_item);
        lv.setAdapter(adapter);
    }
}
