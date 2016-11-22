package com.mobileaviationtools.flightsimplannerpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.LocationSource;
import com.mobileaviationtools.flightsimplannerpro.Airspaces.LoadAirspacesAsync;
import com.mobileaviationtools.flightsimplannerpro.Database.AirportDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.DBFilesHelper;
import com.mobileaviationtools.flightsimplannerpro.Database.MarkerProperties;
import com.mobileaviationtools.flightsimplannerpro.Database.PropertiesDataSource;
import com.mobileaviationtools.flightsimplannerpro.Database.RouteDataSource;
import com.mobileaviationtools.flightsimplannerpro.Info.InfoPanelFragment;
import com.mobileaviationtools.flightsimplannerpro.LocationService.NativeLocation;
import com.mobileaviationtools.flightsimplannerpro.LocationService.PlaneMarker;
import com.mobileaviationtools.flightsimplannerpro.Route.Leg;
import com.mobileaviationtools.flightsimplannerpro.Route.Route;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteActivateActivity;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteVisuals;
import com.mobileaviationtools.flightsimplannerpro.Test.TrackTest;
import com.mobileaviationtools.flightsimplannerpro.Track.LocationTracking;

import java.util.ArrayList;

import us.ba3.me.LightingType;
import us.ba3.me.Location3D;
import us.ba3.me.LocationType;
import us.ba3.me.MapView;

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
	private MapView mapView1;
	PropertiesDataSource properties;
	private NativeLocation nativeLocation;
	private LocationTracking locationTracking;
	private PlaneMarker planeMarker;
	private InfoPanelFragment infoPanel = null;
	private Integer pid;
	private InfoWindow infoWindow;

	// ****************************************************
	// ********* Map orders
	// Route - Markers				141
	// Route - Edit Buttons			140
	// Route - Line					130
	// Track - Line					120
	// Airport Markers				102
	// Navaids Markers				101
	// Fixes Markers				100
	// Airspaces					50
	// Overlay Maps - PDF maps		11
	// Overlay Maps - AAF maps		10
	// Bottom layers map			1
	// *****************************************************

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		infoWindow = new InfoWindow(this);

		Log.i(TAG, "OnCreate");
		ScreendensityInfo();

		pid = android.os.Process.myPid();
		Log.i(TAG, "Flight sim plannen Pro PID: " + pid);

		//mapView = new MyMapView(getApplication());
//		mapView1 = new MapView(this);

		//mapView = (MyMapView) this.findViewById(R.id.mapView);
		mapView = new MyMapView(this);
		mapView.Init(null);
//		mapView.setLayoutParams(new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT,
//				LinearLayout.LayoutParams.MATCH_PARENT
//		));
//
//		//Set lighting type to classic
		mapView.setLightingType(LightingType.kLightingTypeClassic);
//
//		//Set sun relative to observer
		mapView.setSunLocation(new us.ba3.me.Location(0,0), LocationType.kLocationTypeRelative);
//
//		mapView.setBackgroundColor(Color.BLACK);
//
		mapView.setZOrderOnTop(false);
		LinearLayout baseLayout = (LinearLayout) this.findViewById(R.id.mapLayout);
		baseLayout.addView(mapView);


		DrawerLayout drawerLayout = (DrawerLayout)this.findViewById(R.id.drawerLayout);
		drawerLayout.setScrimColor(Color.argb(50,0,0,0));



		SetupNavigationMenu();

		Log.i(TAG, "Map added...................");
//
		mapView.setMultithreaded(true);
//
		//mapView.AddMappyMap();
		//mapView.AddMappyMap();
		mapView.AddOpenstreetMap();

//		// Primary location Netherlands
		Location3D loc = new Location3D();
		loc.altitude = 600000;
		loc.longitude = 5.6129;
		loc.latitude = 52.302;
		mapView.setLocation3D(loc, 0);

		SetupMapVisualsChangedListeners();

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

		routeVisuals.setOnRouteDrawn(new RouteVisuals.OnRouteDrawn() {
			@Override
			public void RouteDrawn(Location3D location3D) {
				doOnScaled(location3D, mapView);
			}
		});

		infoPanel = (InfoPanelFragment) getSupportFragmentManager().findFragmentById(R.id.infoPanelFragment);

		mapView.setMaximumZoom(2000000);
		mapView.setMinimumZoom(20000);



		nativeLocation = new NativeLocation(this);
		setupLocationListener();

		//DBFilesHelper.CopyNavigationDatabase(this, "userairnav.db");

		AirportDataSource airportDataSource = new AirportDataSource(this);
		airportDataSource.open(pid);
		Log.i(TAG, "Airport Count: " + airportDataSource.GetAirportsCount());
		airportDataSource.UpdateProgramID();
		airportDataSource.close();

		properties = new PropertiesDataSource(this);
		properties.open(true);
		properties.FillProperties();
		MarkerProperties markerProperties = properties.getMarkersProperties();
		Log.i(TAG, "Default Airport: " + properties.InitAirport.name);
		Log.i(TAG, "Default Airport PID: " + properties.InitAirport.PID);
		properties.close(true);


		RouteDataSource routeDataSource = new RouteDataSource(this);
		routeDataSource.open();
		Log.i(TAG, "Flightplan Count: " + routeDataSource.GetFlightplanCount());
		routeDataSource.close();

		mapView.AddMarkersMap(markerProperties, pid, this);

	}

	private void SetupNavigationMenu() {
		NavigationView navigationView = (NavigationView) this.findViewById(R.id.navigationView);
		Menu navMenu = navigationView.getMenu();

		navMenu.findItem(R.id.item_ActivateFP).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.i(TAG, "Menu item clicked: " + item.toString());
				showActivateRouteActivity();
				return false;
			}
		});

		navMenu.findItem(R.id.item_ConnnectGPS).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.i(TAG, "Menu item clicked: " + item.toString());
				if (nativeLocation.connected) nativeLocation.disconnect();
				else nativeLocation.connect();
				return false;
			}
		});

		navMenu.findItem(R.id.item_CreateFP).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.i(TAG, "Menu item clicked: " + item.toString());
				showNewRouteActivity();
				return false;
			}
		});

		navMenu.findItem(R.id.item_Metar).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.i(TAG, "Menu item clicked: " + item.toString());
				
				infoWindow.ShowInfoWindow("EHLE");

				return false;
			}
		});

		navMenu.findItem(R.id.item_Notam).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.i(TAG, "Menu item clicked: " + item.toString());
				return false;
			}
		});

		navMenu.findItem(R.id.item_Settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.i(TAG, "Menu item clicked: " + item.toString());
				return false;
			}
		});

		navMenu.findItem(R.id.item_Taf).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.i(TAG, "Menu item clicked: " + item.toString());
				return false;
			}
		});

		navMenu.findItem(R.id.item_Track).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.i(TAG, "Menu item clicked: " + item.toString());
				return false;
			}
		});
	}

	private void SetupMapVisualsChangedListeners() {
		mapView.setOnVisualsChanged(new MyMapView.OnVisualsChanged() {
			@Override
			public void onScrolled(Location3D location3D, MyMapView myMapView) {
				Log.i("onScrolled", "3D: lat:" + location3D.latitude + "  lon:" + location3D.longitude + "  alt:" + location3D.altitude);
			}

			@Override
			public void onScaled(Location3D location3D, MyMapView myMapView) {
				Log.i("onScaled", "3D: lat:" + location3D.latitude + "  lon:" + location3D.longitude + "  alt:" + location3D.altitude);

				MainActivity.this.doOnScaled(location3D, myMapView);
			}
		});
	}

	private void doOnScaled(Location3D location3D, MyMapView myMapView)
	{
		if (this.route != null)
		{
			if (this.route.legs.size()>0)
			{
				for (Leg leg: this.route.legs)
				{
					//Log.i("onScaled", "Leg distance: " + leg.getToWaypoint().distance_leg);
					double d = leg.getToWaypoint().distance_leg;
					double c = d / location3D.altitude;
					//Log.i("onScaled", "Calculated: " + c);
					if (c > 0.056f)
					{
						myMapView.showDynamicMarker("coarse_labels", leg.getToWaypoint().name);
					}else
					{
						myMapView.hideDynamicMarker("coarse_labels", leg.getToWaypoint().name);
					}
				}
			}
		}
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


	private void setupLocationListener() {
		nativeLocation.setOnConnectionChanged(new NativeLocation.OnConnectionChanged() {
			@Override
			public void Connected() {
				locationTracking = new LocationTracking(route, MainActivity.this, mapView);
				if (planeMarker == null) planeMarker = new PlaneMarker(MainActivity.this, mapView);
			}

			@Override
			public void Disconnected() {
			//	setConnectionBtnImage(false);
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

	private void showNewRouteActivity()
	{
		Intent startFlightplanIntent = new Intent(MainActivity.this, NewRouteActivity.class);
		startFlightplanIntent.putExtra("key", 1);
		MainActivity.this.startActivityForResult(startFlightplanIntent, 0);
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


	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();


	}

	private void ScreendensityInfo()
	{
		String TAG = "ScreenDensityInfo";
		//Determine screen size
		if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			Log.i(TAG, "Large screen");
		} else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			Log.i(TAG, "Normal sized screen");
		} else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			Log.i(TAG, "Small sized screen" );
		} else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			Log.i(TAG, "XLarge sized screen" );
		} else {
			Log.i(TAG,  "Screen size is neither large, normal or small");
		}

//Determine density
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int density = metrics.densityDpi;
		Log.i(TAG, "Density: "+ metrics.density +
				" HeightPixels: " + metrics.heightPixels +
				" WidthPixels: " + metrics.widthPixels +
				" ScaledDensity: " + metrics.scaledDensity +
				" xdpi: " + metrics.xdpi +
				" ydpi: " + metrics.ydpi);

		if (density==DisplayMetrics.DENSITY_HIGH) {
			Log.i(TAG, "DENSITY_HIGH... Density is " + String.valueOf(density));
		} else if (density==DisplayMetrics.DENSITY_MEDIUM) {
			Log.i(TAG, "DENSITY_MEDIUM... Density is " + String.valueOf(density));
		} else if (density==DisplayMetrics.DENSITY_LOW) {
			Log.i(TAG, "DENSITY_LOW... Density is " + String.valueOf(density));
		} else if (density==DisplayMetrics.DENSITY_XHIGH) {
			Log.i(TAG, "DENSITY_XHIGH... Density is " + String.valueOf(density));
		} else if (density==DisplayMetrics.DENSITY_XXHIGH) {
			Log.i(TAG, "DENSITY_XXHIGH... Density is " + String.valueOf(density));
		} else if (density==DisplayMetrics.DENSITY_XXXHIGH) {
			Log.i(TAG,  "DENSITY_XXXHIGH... Density is " + String.valueOf(density));
		} else {
			Log.i(TAG, "Density is neither HIGH, MEDIUM OR LOW.  Density is " + String.valueOf(density));
		}
	}
}
