package com.mobileaviationtools.flightsimplannerpro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.PolylineOptions;
import com.mobileaviationtools.flightsimplannerpro.Airspaces.LoadAirspacesAsync;
import com.mobileaviationtools.flightsimplannerpro.Database.AirportDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.DBFilesHelper;
import com.mobileaviationtools.flightsimplannerpro.Database.PropertiesDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.RouteDataSource;
import com.mobileaviationtools.flightsimplannerpro.Info.InfoPanelFragment;
import com.mobileaviationtools.flightsimplannerpro.LocationService.NativeLocation;
import com.mobileaviationtools.flightsimplannerpro.LocationService.PlaneMarker;
import com.mobileaviationtools.flightsimplannerpro.Route.Route;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteActivateActivity;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteVisuals;
import com.mobileaviationtools.flightsimplannerpro.Test.TrackTest;
import com.mobileaviationtools.flightsimplannerpro.Track.LocationTracking;

import java.util.ArrayList;

import us.ba3.me.Location3D;

public class MainActivity extends AppCompatActivity {

	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	protected void onDestroy() {
		Log.i("MainActivity", "OnDestroy");
		super.onDestroy();
	}

	private RouteVisuals routeVisuals;
	private Route route;
	private String TAG = "MainActivity";
	private MyMapView mapView;
	PropertiesDataSource properties;
	private NativeLocation nativeLocation;
	private LocationTracking locationTracking;
	private PlaneMarker planeMarker;
	private InfoPanelFragment infoPanel = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);

		mapView = new MyMapView(this);
		mapView.Init(null);
		mapView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
		));
		LinearLayout baseLayout = (LinearLayout) this.findViewById(R.id.mapLayout);
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

		for (String a : airspacedbFiles) {
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

		infoPanel = (InfoPanelFragment) getSupportFragmentManager().findFragmentById(R.id.infoPanelFragment);

		mapView.setMaximumZoom(2000000);
		mapView.setMinimumZoom(20000);

		Log.i(TAG, "OnCreate");

		nativeLocation = new NativeLocation(this);
		setupLocationListener();

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


		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

	private void setupButtons() {
		Button activateRouteBtn = (Button) this.findViewById(R.id.activateRouteBtn);
		Button addNewRouteBtn = (Button) this.findViewById(R.id.addNewRouteBtn);
		Button connectBtn = (Button) this.findViewById(R.id.connectBtn);

		activateRouteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showActivateRouteActivity();
			}
		});

		addNewRouteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testTrack();
			}
		});

		connectBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (nativeLocation.connected) nativeLocation.disconnect();
				else nativeLocation.connect();
			}
		});
	}

	private void setupLocationListener() {
		nativeLocation.setOnConnectionChanged(new NativeLocation.OnConnectionChanged() {
			@Override
			public void Connected() {
				locationTracking = new LocationTracking(route, MainActivity.this, mapView);
				if (planeMarker == null) planeMarker = new PlaneMarker(MainActivity.this, mapView);
				setConnectionBtnImage(true);
			}

			@Override
			public void Disconnected() {
				setConnectionBtnImage(false);
			}
		});

		nativeLocation.setOnLocationChangedListener(new LocationSource.OnLocationChangedListener() {
			@Override
			public void onLocationChanged(Location location) {
				setLocation(location);
			}
		});
	}

	private void testTrack()
	{
		locationTracking = new LocationTracking(route, MainActivity.this, mapView);
		if (planeMarker == null) planeMarker = new PlaneMarker(MainActivity.this, mapView);
		TrackTest trackTest = new TrackTest(this);
		trackTest.setOnLocationChangedListener(new LocationSource.OnLocationChangedListener() {
			@Override
			public void onLocationChanged(Location location) {
				Log.i(TAG, "TestTrack Location: " + location.getLongitude() + " : " + location.getLatitude());

				setLocation(location);
			}
		});

		trackTest.LoadTrackpoints();
		Log.i(TAG, "TrackTest points loaded: " + trackTest.trackPoints.size());
		trackTest.startRoute();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_flightplan_activate: {
				showActivateRouteActivity();
				return true;
			}
			case R.id.action_flightplan: {
				//routeVisuals.setRoute(route);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 301) {
			final Integer route_id = data.getIntExtra("id", 0);

			if (requestCode == 300) {

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

			}
		}
	}

	public void LoadRoute(Integer id) {
		if (route == null)
			route = new Route(this);
		route.LoadRoute(this, id);
		routeVisuals.setRoute(route);
	}

	public void showActivateRouteActivity() {
		Intent activateRouteIntent = new Intent(MainActivity.this, RouteActivateActivity.class);
		activateRouteIntent.putExtra("key", 1);
		MainActivity.this.startActivityForResult(activateRouteIntent, 300);
	}


	private void setLocation(Location location) {
		Log.i(TAG, "Setting new location");
		if (locationTracking != null) {
			float b = locationTracking.setTrackPoints(location);
			setPlaneMarker(location, b);
			SetInfoPanel(location);
		}
	}

	private void SetInfoPanel(Location location) {
		infoPanel.setLocation(location);
	}

	private void setPlaneMarker(Location location, float bearing) {
		us.ba3.me.Location l = new us.ba3.me.Location(
				location.getLatitude(), location.getLongitude());
		planeMarker.setLocation(l, (double) bearing);
	}


	private void setConnectionBtnImage(Boolean connected) {
		Button connectBtn = (Button) this.findViewById(R.id.connectBtn);
		if (connected) {
			connectBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.connected, null));
		} else {
			connectBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.disconnected, null));
		}
	}


	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.mobileaviationtools.flightsimplannerpro/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.mobileaviationtools.flightsimplannerpro/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
}
