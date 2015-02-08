package com.example.maoz.hellowworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

public class ListAdaptor extends ArrayAdapter<stations> {

    public ListAdaptor(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        // TODO Auto-generated constructor stub
    }

    private List<stations> items;

    public ListAdaptor(Context context, int resource, List<stations> items) {

        super(context, resource, items);

        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        TextView tt1 = null;
        TextView tt2 = null;
        TextView tt3 = null;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row, null);

            tt1 = (TextView) v.findViewById(R.id.stations);
            tt2 = (TextView) v.findViewById(R.id.lat);
            tt3 = (TextView) v.findViewById(R.id.lng);
        }

        stations p = items.get(position);

        if (p != null) {


            if (tt1 != null) {

                tt1.setText(""+p.getStations());
            }
            if (tt2 != null) {

                tt2.setText(""+p.getLat());
            }

            if (tt3 != null) {

                tt3.setText(""+p.getLng());
            }
        }



        return v;

    }
}