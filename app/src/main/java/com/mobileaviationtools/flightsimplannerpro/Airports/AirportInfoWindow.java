package com.mobileaviationtools.flightsimplannerpro.Airports;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.mobileaviationtools.flightsimplannerpro.Airports.Airport;
import com.mobileaviationtools.flightsimplannerpro.Database.AirportDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.FrequenciesDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.RunwaysDataSource;
import com.mobileaviationtools.flightsimplannerpro.Navaids.Navaid;
import com.mobileaviationtools.flightsimplannerpro.R;

/**
 * Created by Rob Verhoef on 21-11-2016.
 */

public class AirportInfoWindow {
    public AirportInfoWindow(Context context)
    {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.airport_info_window);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

    private Context context;
    private Dialog dialog;

    public void ShowAirportInfoWindow(String airportIdent)
    {
        AirportDataSource airportDataSource = new AirportDataSource(context);
        airportDataSource.open(-1);
        Airport airport = airportDataSource.GetAirportByIDENT(airportIdent);
        airportDataSource.close();

        if (airport != null)
        {
            TextView infoTxt = (TextView) dialog.findViewById(R.id.infoWindowInfoTxt);
            TextView runwaysTxt = (TextView) dialog.findViewById(R.id.infoWindowRunwaysTxt);
            TextView frequenciesTxt = (TextView) dialog.findViewById(R.id.infoWindowFrequenciesTxt);

            RunwaysDataSource runwaysDataSource = new RunwaysDataSource(context);
            runwaysDataSource.open();
            airport.runways = runwaysDataSource.loadRunwaysByAirport(airport);
            runwaysDataSource.close();

            FrequenciesDataSource frequenciesDataSource = new FrequenciesDataSource(context);
            frequenciesDataSource.open();
            airport.frequencies = frequenciesDataSource.loadFrequenciesByAirport(airport);
            frequenciesDataSource.close();

            String info = airport.getAirportInfoString();
            infoTxt.setText(info);

            info = airport.getRunwaysInfo();
            runwaysTxt.setText(info);

            info = airport.getFrequenciesInfo();
            frequenciesTxt.setText(info);
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.85);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.85);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);

        dialog.show();

    }
}
