package com.mobileaviationtools.flightsimplannerpro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobileaviationtools.flightsimplannerpro.TileWorkers.TileProviderFormats;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.WmsTileWorker;

import us.ba3.me.MapView;
import us.ba3.me.virtualmaps.TileFactory;
import us.ba3.me.virtualmaps.VirtualMapInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the map view and add a map.
        MapView mapView = (MapView)this.findViewById(R.id.mapView1);
		mapView.addInternetMap("MapQuest Aerial",
				"http://otile1.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.jpg",
				"", 		//Subdomains
				20,			//Max Level
				2,			//zOrder
				3,			//Number of simultaneous downloads
				false,		//Use cache
				true		//No alpha
				);


		TileFactory tileFactory = new TileFactory(mapView);

		WmsTileWorker chartBundleWorker = new WmsTileWorker(
				TileProviderFormats.CHARTBUNDLE_FORMAT,
				"",
				TileProviderFormats.chartBundleLayer.sec_4326.toString());

		tileFactory.addWorker(chartBundleWorker);

        VirtualMapInfo mapInfo = new VirtualMapInfo();
        mapInfo.name = "Chartbundle";
        mapInfo.zOrder = 3;
        mapInfo.maxLevel = 20;
        mapInfo.isSphericalMercator = false;
        mapInfo.setTileProvider(tileFactory);
        mapView.addMapUsingMapInfo(mapInfo);

    }
}
