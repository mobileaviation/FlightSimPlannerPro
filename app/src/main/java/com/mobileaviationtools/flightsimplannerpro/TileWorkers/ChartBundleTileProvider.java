package com.mobileaviationtools.flightsimplannerpro.TileWorkers;

import java.io.File;

import us.ba3.me.virtualmaps.InternetTileProvider;

/**
 * Created by Rob Verhoef on 5-6-2016.
 */
public class ChartBundleTileProvider extends InternetTileProvider{
    public ChartBundleTileProvider(File cacheDir, String tilesUrl, String shortName, String tileFileExtension) {
        super(cacheDir, tilesUrl, shortName, tileFileExtension);
    }
}
