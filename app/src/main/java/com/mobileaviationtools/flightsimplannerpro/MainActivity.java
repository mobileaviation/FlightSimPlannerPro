package com.mobileaviationtools.flightsimplannerpro;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.mobileaviationtools.flightsimplannerpro.Airspaces.LoadAirspacesAsync;
import com.mobileaviationtools.flightsimplannerpro.Database.DBFilesHelper;
import com.mobileaviationtools.flightsimplannerpro.Route.Route;

import java.util.ArrayList;

import us.ba3.me.Location3D;

public class MainActivity extends AppCompatActivity{

	@Override
	protected void onDestroy() {
		Log.i("MainActivity", "OnDestroy");
		super.onDestroy();
	}

	private Route route;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the map view and add a map.
		//final MapView mapView = (MapView)this.findViewById(R.id.mapView1);
        final MyMapView mapView;
		mapView = new MyMapView(this);
		mapView.Init(null);
		mapView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
		));
		LinearLayout baseLayout = (LinearLayout)this.findViewById(R.id.baseLayout);
		baseLayout.addView(mapView);

		mapView.setMultithreaded(true);

		mapView.AddMappyMap();

		// Primary location Netherlands
		Location3D loc = new Location3D();
		loc.altitude = 600000;
		loc.longitude = 5.6129;
		loc.latitude = 52.302;
		mapView.setLocation3D(loc, 1);

		ArrayList<String> airspacedbFiles = DBFilesHelper.CopyDatabases(this.getApplicationContext());

		for (String a: airspacedbFiles)
		{
			LoadAirspacesAsync loadAirspacesAsync = new LoadAirspacesAsync();
			loadAirspacesAsync.context = this;
			loadAirspacesAsync.databaseName = a;
			loadAirspacesAsync.mapView = mapView;
			loadAirspacesAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}


		route = new Route(mapView, "Route", this);
		mapView.Init(route);

		Log.i("MainActivity", "OnCreate");

    }


}
