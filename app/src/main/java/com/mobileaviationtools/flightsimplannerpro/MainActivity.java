package com.mobileaviationtools.flightsimplannerpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.PolylineOptions;
import com.mobileaviationtools.flightsimplannerpro.Airspaces.LoadAirspacesAsync;
import com.mobileaviationtools.flightsimplannerpro.Database.AirportDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.DBFilesHelper;
import com.mobileaviationtools.flightsimplannerpro.Database.PropertiesDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.RouteDataSource;
import com.mobileaviationtools.flightsimplannerpro.Route.Route;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteActivateActivity;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteVisuals;
import com.mobileaviationtools.flightsimplannerpro.Track.LocationTracking;

import java.util.ArrayList;

import us.ba3.me.Location3D;

public class MainActivity extends AppCompatActivity implements LocationListener,
		GoogleApiClient.ConnectionCallbacks{

	@Override
	protected void onDestroy() {
		Log.i("MainActivity", "OnDestroy");
		super.onDestroy();
	}

	private RouteVisuals routeVisuals;
	private Route route;
	private String TAG = "MainActivity";
	private MyMapView mapView;
	LocationManager service;
	String locationProvider;
	PropertiesDataSource properties;
	private LocationTracking locationTracking;
	private Boolean locationConnected;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        //Get the map view and add a map.
		//final MapView mapView = (MapView)this.findViewById(R.id.mapView1);
        //final MyMapView mapView
		locationConnected = false;

		mapView = new MyMapView(this);
		mapView.Init(null);
		mapView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
		));
		LinearLayout baseLayout = (LinearLayout)this.findViewById(R.id.mapLayout);
		baseLayout.addView(mapView);

		//mapView.setMultithreaded(true);

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

		//String p = DBFilesHelper.CopyAirspaceMap(this.getApplicationContext());
		//mapView.addVectorMap("Airspaces", p + "Airspaces.sqlite", p + "Airspaces.map");

		routeVisuals = new RouteVisuals(this, mapView);
		mapView.Init(routeVisuals);

		mapView.setMaximumZoom(2000000);
		mapView.setMinimumZoom(20000);

		Log.i(TAG, "OnCreate");

		setupButtons();

		//DBFilesHelper.CopyNavigationDatabase(this, "userairnav.db");

		AirportDataSource airportDataSource = new AirportDataSource(this);
		airportDataSource.open(0);
		Log.i(TAG, "Airport Count: " + airportDataSource.GetAirportsCount());
		airportDataSource.close();

		properties = new PropertiesDataSource(this);
		properties.open(true);
		properties.FillProperties();
		Log.i(TAG, "Default Airport: " + properties.InitAirport.name);
		properties.close(true);

		RouteDataSource routeDataSource = new RouteDataSource(this);
		routeDataSource.open();
		Log.i(TAG, "Flightplan Count: " + routeDataSource.GetFlightplanCount());
		routeDataSource.close();



    }

	@Override
	protected void onResume() {
		Log.i(TAG, "OnResume");
		super.onResume();

//		if (routeVisuals != null)
//		{
//			if (route != null)
//				if (route.reloaded)
//				{
//					routeVisuals.setRoute(route);
//					route.reloaded = false;
//				}
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}

	private void setupButtons()
	{
		Button activateRouteBtn = (Button)this.findViewById(R.id.activateRouteBtn);
		Button addNewRouteBtn = (Button)this.findViewById(R.id.addNewRouteBtn);
		Button connectBtn = (Button)this.findViewById(R.id.connectBtn);

		activateRouteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showActivateRouteActivity();
			}
		});

		addNewRouteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		connectBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!locationConnected) {
					if (properties.getConnectionType() == ConnectionType.gps) {
						service = (LocationManager) getSystemService(LOCATION_SERVICE);
						boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
						if (!enabled) {
							Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(intent);
						} else {
							Criteria criteria = new Criteria();
							locationProvider = service.getBestProvider(criteria, false);
							Location location = service.getLastKnownLocation(locationProvider);
							service.requestLocationUpdates(locationProvider, 400, 1, MainActivity.this);
							if (location != null) {
								onLocationChanged(location);
							}
						}
					}
				}
				else
				{
					locationConnected = false;
					setConnectionBtnImage(locationConnected);
				}
			}
		});
	}

//	private com.google.android.gms.location.LocationClient mLocationClient;
//	private void connectToGps() {
//		if(servicesConnected())
//		{
//			if (mLocationClient == null)
//			{
//				mLocationClient = new LocationClient(
//						this, this, this
//				);
//
//
//			}
//
//			mLocationClient.connect();
//		}
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_flightplan_activate: {
				showActivateRouteActivity();
				return true;
			}
			case R.id.action_flightplan:
			{
				//routeVisuals.setRoute(route);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 301)
		{
			final Integer route_id = data.getIntExtra("id", 0);

			if (requestCode == 300)
			{
				//LoadFlightplan(id);

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
						dialog.dismiss();
						LoadRoute(route_id);
					}
				});

				builder.setMessage("To activate this route push the 'TakeOff' button!");
				builder.setTitle("Activate route!");

				AlertDialog closePlanDialog = builder.create();
				closePlanDialog.show();


				//routeVisuals.markerTestje();
			}
		}
	}

	public void LoadRoute(Integer id)
	{
		if (route == null)
			route = new Route(this);
		route.LoadRoute(this, id);
		routeVisuals.setRoute(route);
	}

	public void showActivateRouteActivity()
	{
		Intent activateRouteIntent = new Intent(MainActivity.this, RouteActivateActivity.class);
		activateRouteIntent.putExtra("key", 1);
		MainActivity.this.startActivityForResult(activateRouteIntent, 300);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i(TAG, "new location: " + location.getLongitude() + " : " + location.getLatitude());
	}

	private void setLocation(Location location)
	{
		setTrackPoints(location);
		setPlaneMarker(location);
		SetInfoPanel(location);
	}

	private void SetInfoPanel(Location location) {
	}

	private void setPlaneMarker(Location location) {
//		LatLng planePosition = new LatLng(position.getLatitude(), position.getLongitude());
//		if (trackingEnabled)
//			map.moveCamera(CameraUpdateFactory.newLatLng(planePosition));
//
//		plane.setPosition(planePosition);
//		plane.setRotation(position.getBearing());
//		plane.UpdateDirectionLine();
	}

	private LatLng oldPoint;
	private float pointDistance = 0;
	private float setTrackPoints(Location newPoint)
	{
		float b = newPoint.getBearing();
		if (oldPoint != null)
		{
			Location loc = new Location("loc old");
			loc.setLatitude(oldPoint.latitude);
			loc.setLongitude(oldPoint.longitude);

			Location locnew = new Location("loc new");
			locnew.setLatitude(newPoint.getLatitude());
			locnew.setLongitude(newPoint.getLongitude());

			float v = loc.distanceTo(locnew);

			if (v>100)
			{
				b = loc.bearingTo(locnew);

				oldPoint = new LatLng(newPoint.getLatitude(), newPoint.getLongitude());
				//map.addPolyline(trackOptions);

				if (locationTracking != null)
					locationTracking.SetLocationPoint(newPoint);
			}

			if (locationTracking != null)
			    locationTracking.SetLocationPoint(newPoint);
		}
		else
		{
			oldPoint = new LatLng(newPoint.getLatitude(), newPoint.getLongitude());
		}

		return b;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	private void setConnectionBtnImage(Boolean connected)
	{
		Button connectBtn = (Button)this.findViewById(R.id.connectBtn);
		if (connected)
		{
			connectBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.connected, null));
		}
		else
		{
			connectBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disconnected, null));
		}
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Toast gpsConnected = Toast.makeText(this, "GPS Enabled!", Toast.LENGTH_SHORT);
		gpsConnected.show();
		locationTracking = new LocationTracking(route, getBaseContext());
		locationConnected = true;
		setConnectionBtnImage(locationConnected);
	}


	@Override
	public void onConnectionSuspended(int i) {
		Toast gpsDisconnected = Toast.makeText(this, "GPS Disabled!", Toast.LENGTH_SHORT);
		gpsDisconnected.show();
		locationConnected = false;
		setConnectionBtnImage(locationConnected);
	}
}
