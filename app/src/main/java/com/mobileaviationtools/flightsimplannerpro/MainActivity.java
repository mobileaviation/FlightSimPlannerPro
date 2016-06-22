package com.mobileaviationtools.flightsimplannerpro;

import android.graphics.Color;
import android.graphics.PointF;
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
import com.mobileaviationtools.flightsimplannerpro.Database.AirspacesDB;
import com.mobileaviationtools.flightsimplannerpro.Database.DBFilesHelper;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteTest;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.TileProviderFormats;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.WmsTileWorker;

import us.ba3.me.Location3D;
import us.ba3.me.MapInfo;
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
		TileFactory yandexFactory = new TileFactory(mapView);
		WmsTileWorker yandexWorker = new WmsTileWorker(
				TileProviderFormats.MAPPY_FORMAT,
				"",
				"");

		yandexFactory.addWorker(yandexWorker);

		VirtualMapInfo yandexmapInfo = new VirtualMapInfo();
		yandexmapInfo.name = "Mappy";
		yandexmapInfo.zOrder = 7;
		yandexmapInfo.maxLevel = 20;
		yandexmapInfo.isSphericalMercator = false;
		yandexmapInfo.setTileProvider(yandexFactory);
		mapView.addMapUsingMapInfo(yandexmapInfo);

		// Primary location Netherlands
		Location3D loc = new Location3D();
		loc.altitude = 600000;
		loc.longitude = 5.6129;
		loc.latitude = 52.302;
		mapView.setLocation3D(loc, 1);


		DBFilesHelper.CopyDatabases(this.getApplicationContext());
		AirspacesDB airspacesDB = new AirspacesDB(this);
		airspacesDB.Open("airspaces.db");
		Airspaces airspaces = new Airspaces();
		airspaces.readFromDatabase(airspacesDB.GetAirspaces("NL"));

		airspaces.createAirspacesLayer(mapView);

		RouteTest routeTest = new RouteTest(this, mapView);
		routeTest.placeInitialRouteOnMap();
		mapView.Init(routeTest);



    }


}
