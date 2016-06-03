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
				true,		//Use cache
				true		//No alpha
				);

        mapView.addInternetMap("Airspaces",
                "https://skylines.aero/mapproxy/tiles/1.0.0/airspace/{z}/{x}/{y}.png",
                "",
                20,
                5,
                3,
                true,
                true);

        //http://wms.chartbundle.com/tms/1.0.0/sec/{$z}/{$x}/{$y}.png?origin=nw

        mapView.addInternetMap("FAA Sectional",
                "http://wms.chartbundle.com/tms/1.0.0/sec_3857/{z}/{x}/{y}.png?origin=nw",
                "",
                20,
                6,
                3,
                false,
                true);

		TileFactory chartBundleFactory = new TileFactory(mapView);
		WmsTileWorker chartBundleWorker = new WmsTileWorker(
				TileProviderFormats.CHARTBUNDLE_FORMAT,
				"",
				TileProviderFormats.chartBundleLayer.sec_4326.toString());

        chartBundleFactory.addWorker(chartBundleWorker);

        VirtualMapInfo chartBundlemapInfo = new VirtualMapInfo();
        chartBundlemapInfo.name = "Chartbundle";
        chartBundlemapInfo.zOrder = 3;
        chartBundlemapInfo.maxLevel = 20;
        chartBundlemapInfo.isSphericalMercator = false;
        chartBundlemapInfo.setTileProvider(chartBundleFactory);
        //mapView.addMapUsingMapInfo(chartBundlemapInfo);



    }
}
