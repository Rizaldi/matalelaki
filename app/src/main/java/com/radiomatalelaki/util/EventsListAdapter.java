package com.radiomatalelaki.util;

/**
 * Created by rianpradana on 5/26/17.
 */
import com.radiomatalelaki.R;
import com.radiomatalelaki.api.Events;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * The type Events list adapter, a custom ArrayAdapter for the Event class type.
 */
public class EventsListAdapter extends ArrayAdapter<Events> {

    private final Context context;

    // events list
    private final List<Events> values;

    // item layout resource id
    private final int level;


    /**
     * Instantiates a new Events list adapter.
     *
     * @param context the context
     * @param values  the values
     * @param level   the level
     */
    public EventsListAdapter(Context context, List<Events> values, int level) {
        super(context, level, values);
        this.context = context;
        this.values = values;
        this.level = level;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(level, parent, false);

        // set the distance value
        TextView distanceValue = (TextView) rowView.findViewById(R.id.mtValueLabel);
        int distance = (int)values.get(position).getDistance();
        TextView mtLabel = (TextView) rowView.findViewById(R.id.mtLabel);
        if (distance<1000) {
            distanceValue.setText(""+distance);
            mtLabel.setText("MT");
        } else {
            distanceValue.setText(""+distance/1000);;
            mtLabel.setText("KM");
        }

        // set the title
        TextView title = (TextView) rowView.findViewById(R.id.titleRow);
        title.setText(values.get(position).getTitle().trim());

        // set the subtitle and address
        TextView subtitle = (TextView) rowView.findViewById(R.id.subtitleRow);
        subtitle.setText(values.get(position).getBrief_description().trim());
        TextView address = (TextView) rowView.findViewById(R.id.addressRow);
        address.setText(values.get(position).getAddress().trim());

        return rowView;
    }

}
