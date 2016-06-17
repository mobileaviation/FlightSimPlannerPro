package com.mobileaviationtools.flightsimplannerpro;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

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
import us.ba3.me.styles.PolygonStyle;
import us.ba3.me.virtualmaps.TileFactory;
import us.ba3.me.virtualmaps.VirtualMapInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the map view and add a map.
        //final MapView mapView = (MapView)this.findViewById(R.id.mapView1);

		final MapView mapView = new MapView(this){
			@Override
			public  boolean onTouchEvent(MotionEvent event)
			{

				return false;
			}
		};

		mapView.addInternetMap("MapQuest Aerial",
				"http://otile1.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg",
				"", 		//Subdomains
				20,			//Max Level
				2,			//zOrder
				3,			//Number of simultaneous downloads
				true,		//Use cache
				true		//No alpha
				);

        mapView.addInternetMap("SkylinesAirspaces",
                "https://skylines.aero/mapproxy/tiles/1.0.0/airspace/{z}/{x}/{y}.png",
                "",
                20,
                5,
                3,
                true,
                true);
//
//        //http://wms.chartbundle.com/tms/1.0.0/sec/{$z}/{$x}/{$y}.png?origin=nw
//
//        mapView.addInternetMap("FAA Sectional",
//                "http://wms.chartbundle.com/tms/1.0.0/sec_3857/{z}/{x}/{y}.png?origin=nw",
//                "",
//                20,
//                6,
//                3,
//                false,
//                true);

		TileFactory chartBundleFactory = new TileFactory(mapView);
		WmsTileWorker chartBundleWorker = new WmsTileWorker(
				TileProviderFormats.CHARTBUNDLE_FORMAT,
				"",
				TileProviderFormats.chartBundleLayer.sec_4326.toString());

        chartBundleFactory.addWorker(chartBundleWorker);

        VirtualMapInfo chartBundlemapInfo = new VirtualMapInfo();
        chartBundlemapInfo.name = "Chartbundle";
        chartBundlemapInfo.zOrder = 7;
        chartBundlemapInfo.maxLevel = 20;
        chartBundlemapInfo.isSphericalMercator = false;
        chartBundlemapInfo.setTileProvider(chartBundleFactory);
        //mapView.addMapUsingMapInfo(chartBundlemapInfo);

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

		PolygonStyle polygonStyle = new PolygonStyle();
		polygonStyle.outlineColor = Color.GREEN;
		polygonStyle.outlineWidth = 1;
		polygonStyle.strokeColor = Color.YELLOW;
		polygonStyle.strokeWidth = 4;
		polygonStyle.fillColor = Color.TRANSPARENT;

		VectorMapInfo airspacesInfo = new VectorMapInfo();
		airspacesInfo.name = "Airspaces";
		airspacesInfo.zOrder = 8;
		airspacesInfo.maxLevel = 20;
		mapView.addMapUsingMapInfo(airspacesInfo);

		for (Airspace airspace: airspaces) {
			mapView.addPolygonToVectorMap("Airspaces", airspace.getAirspaceB3aLocations(), polygonStyle);
		}

		RouteTest routeTest = new RouteTest(this);
		routeTest.placeRouteOnMap(mapView);



    }
}
