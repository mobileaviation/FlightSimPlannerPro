package com.mobileaviationtools.flightsimplannerpro.Route;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.flightsimplannerpro.R;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 8-7-2016.
 */
public class RouteAdapter extends BaseAdapter {
    private static int selectedIndex = -1;
    private ArrayList<Route> routes;
    public RouteAdapter(ArrayList<Route> routes)
    {
        this.routes = routes;
        selectedIndex = -1;
    }

    public void setSelectedIndex(int selectedIndex) {
        RouteAdapter.selectedIndex = selectedIndex;
    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public Route GetRoute(int i)
    {
        return routes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.route_item, viewGroup, false);

        TextView textView = (TextView) view.findViewById(R.id.country_text_item);
        Route route = routes.get(i);
        textView.setText(route.name);

        if (i == selectedIndex) {
            view.setBackgroundColor(Color.parseColor("#7ecce8"));
        }
        else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }
}