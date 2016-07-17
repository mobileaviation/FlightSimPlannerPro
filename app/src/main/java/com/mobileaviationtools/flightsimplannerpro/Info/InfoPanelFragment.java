package com.mobileaviationtools.flightsimplannerpro.Info;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileaviationtools.flightsimplannerpro.R;

public class InfoPanelFragment extends android.support.v4.app.Fragment  {
    private String TAG = "InfoPanelFragment";
    private Location curlocation;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_info_panel, container, false);
        return view;
    }

    public void setLocation(Location location)
    {
        TextView latTxt = (TextView) view.findViewById(R.id.latitudeTxt);
        TextView lonTxt = (TextView) view.findViewById(R.id.longitudeTxt);
        TextView headingTxt = (TextView) view.findViewById(R.id.headingTxt);
        TextView gsTxt = (TextView) view.findViewById(R.id.groundspeedTxt);
        TextView altitudeTxt = (TextView) view.findViewById(R.id.infoAltitudeTxt);

        String latStr = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
        String lonStr = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);

        latTxt.setText(latStr);
        lonTxt.setText(lonStr);

        headingTxt.setText(
                String.format("%03d dg", Math.round(location.getBearing())));
        // Calculate m/s to knots
        gsTxt.setText(
                String.format("%d kt", Math.round(location.getSpeed()*1.9438444924574f)));
        // Calculate Meters to Feet
        altitudeTxt.setText(
                String.format("%04d ft", Math.round(location.getAltitude() * 3.2808399d)));

        curlocation = location;
    }
}
