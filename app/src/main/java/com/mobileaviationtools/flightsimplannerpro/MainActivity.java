package com.mobileaviationtools.flightsimplannerpro;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mobileaviationtools.flightsimplannerpro.Airspaces.Airspace;
import com.mobileaviationtools.flightsimplannerpro.Airspaces.Airspaces;
import com.mobileaviationtools.flightsimplannerpro.Airspaces.LoadAirspacesAsync;
import com.mobileaviationtools.flightsimplannerpro.Database.AirspacesDB;
import com.mobileaviationtools.flightsimplannerpro.Database.DBFilesHelper;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteTest;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.TileProviderFormats;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.WmsTileWorker;

import java.util.ArrayList;

import us.ba3.me.Location3D;
import us.ba3.me.MapInfo;
import us.ba3.me.MapLoadingStrategy;
import us.ba3.me.MapType;
import us.ba3.me.MapView;
import us.ba3.me.VectorMapInfo;
import us.ba3.me.markers.MarkerInfo;
import us.ba3.me.markers.MarkerMapDelegate;
import us.ba3.me.styles.PolygonStyle;
import us.ba3.me.virtualmaps.TileFactory;
import us.ba3.me.virtualmaps.VirtualMapInfo;

public class MainActivity extends AppCompatActivity{

	@Override
	protected void onDestroy() {
		Log.i("MainActivity", "OnDestroy");
		super.onDestroy();
	}

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



//		mapView.addInternetMap("MapQuest Aerial",
//				"http://otile1.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg",
//				"", 		//Subdomains
//				20,			//Max Level
//				2,			//zOrder
//				3,			//Number of simultaneous downloads
//				true,		//Use cache
//				true		//No alpha
//				);

//        mapView.addInternetMap("SkylinesAirspaces",
//                "https://skylines.aero/mapproxy/tiles/1.0.0/airspace/{z}/{x}/{y}.png",
//                "",
//                20,
//                5,
//                3,
//                true,
//                true);
//
//        //http://wms.chartbundle.com/tms/1.0.0/sec/{$z}/{$x}/{$y}.png?origin=nw
//
//        mapView.addInternetMap("Mappy",
//				"https://map1.mappy.net/map/1.0/slab/standard/256/{z}/{x}/{y}",
//                "",
//                20,
//                2,
//                3,
//                true,
//                true);

		// ChartBundle FAA Sectionals
		TileFactory chartBundleFactory = new TileFactory(mapView);
		WmsTileWorker chartBundleWorker = new WmsTileWorker(
				TileProviderFormats.CHARTBUNDLE_XYZ_FORMAT,
				"",
				TileProviderFormats.chartBundleLayer.sec_3857.toString());

        chartBundleFactory.addWorker(chartBundleWorker);

        VirtualMapInfo chartBundlemapInfo = new VirtualMapInfo();
        chartBundlemapInfo.name = "Chartbundle";
        chartBundlemapInfo.zOrder = 7;
        chartBundlemapInfo.maxLevel = 20;
        chartBundlemapInfo.isSphericalMercator = false;
		chartBundlemapInfo.compressTextures = true;
        chartBundlemapInfo.setTileProvider(chartBundleFactory);
        //mapView.addMapUsingMapInfo(chartBundlemapInfo);


		// Yandex maps
		// The tiles do not lineup in lateral direction!!!!
		TileFactory mappyFactory = new TileFactory(mapView);
		WmsTileWorker mappyWorker = new WmsTileWorker(
				TileProviderFormats.MAPPY_FORMAT,
				"",
				"");

		mappyFactory.addWorker(mappyWorker);

		VirtualMapInfo mappymapInfo = new VirtualMapInfo();
		mappymapInfo.name = "Mappy";
		mappymapInfo.zOrder = 7;
		mappymapInfo.maxLevel = 20;
		mappymapInfo.isSphericalMercator = false;
		mappymapInfo.mapLoadingStrategy = MapLoadingStrategy.kHighestDetailOnly;
		mappymapInfo.setTileProvider(mappyFactory);
		mapView.addMapUsingMapInfo(mappymapInfo);

		// Primary location Netherlands
		Location3D loc = new Location3D();
		loc.altitude = 600000;
		loc.longitude = 5.6129;
		loc.latitude = 52.302;
		mapView.setLocation3D(loc, 1);

		ArrayList<String> airspacedbFiles = DBFilesHelper.CopyDatabases(this.getApplicationContext());
//		AirspacesDB airspacesDB = new AirspacesDB(this);
//		airspacesDB.Open("airspaces.db");
//
//		Airspaces airspacesNL = new Airspaces();
//		airspacesNL.readFromDatabase(airspacesDB.GetAirspaces("NL"));
//		airspacesNL.createAirspacesLayer(mapView, "NL");
//		Airspaces airspacesBE = new Airspaces();
//		airspacesBE.readFromDatabase(airspacesDB.GetAirspaces("BE"));
//		airspacesBE.createAirspacesLayer(mapView, "BE");
//		Airspaces airspacesDE = new Airspaces();
//		airspacesDE.readFromDatabase(airspacesDB.GetAirspaces("DE"));
//		airspacesDE.createAirspacesLayer(mapView, "DE");


		for (String a: airspacedbFiles)
		{
			LoadAirspacesAsync loadAirspacesAsync = new LoadAirspacesAsync();
			loadAirspacesAsync.context = this;
			loadAirspacesAsync.databaseName = a;
			loadAirspacesAsync.mapView = mapView;
			loadAirspacesAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}


		RouteTest routeTest = new RouteTest(this, mapView);
		routeTest.placeInitialRouteOnMap();
		mapView.Init(routeTest);

		Log.i("MainActivity", "OnCreate");

    }


}
