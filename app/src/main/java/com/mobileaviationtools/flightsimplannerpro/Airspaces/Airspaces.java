package com.mobileaviationtools.flightsimplannerpro.Airspaces;

import android.database.Cursor;
import android.graphics.Color;

import java.util.ArrayList;

import us.ba3.me.MapType;
import us.ba3.me.MapView;
import us.ba3.me.VectorMapInfo;
import us.ba3.me.styles.PolygonStyle;

/**
 * Created by Rob Verhoef on 15-6-2016.
 */
public class Airspaces extends ArrayList<Airspace>  {
    public Airspaces()
    {

    }

    public void Add(Airspace airspace) {
        this.add(airspace);
    }


    public void readFromDatabase(Cursor cursor)
    {
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Airspace airspace = new Airspace();
            airspace.AssignFromCursor(cursor);
            Add(airspace);
            cursor.moveToNext();
        }
    }

    public void createAirspacesLayer(MapView mapView, String Name)
    {
        String mapname = "Airspaces"+Name;

        PolygonStyle polygonStyle = new PolygonStyle();

        VectorMapInfo airspacesInfo = new VectorMapInfo();
        airspacesInfo.name = mapname;
        airspacesInfo.zOrder = 8;
        airspacesInfo.maxLevel = 20;
        airspacesInfo.mapType = MapType.kMapTypeVirtualVector;

        mapView.addMapUsingMapInfo(airspacesInfo);

        for (Airspace airspace: this) {
            //if (airspace.Category == AirspaceCategory.CTR)
            //    polygonStyle.fillColor = Color.argb(100, 228, 161, 158);
            //else polygonStyle.fillColor = Color.TRANSPARENT;

            if (airspace.Category.getVisible()) {
                polygonStyle.outlineColor = airspace.Category.getOutlineColor();
                polygonStyle.outlineWidth = airspace.Category.getOutlineWidth();
                polygonStyle.strokeColor = airspace.Category.getStrokeColor();
                polygonStyle.strokeWidth = airspace.Category.getStrokeWidth();
                polygonStyle.fillColor = airspace.Category.getFillColor();


                mapView.addPolygonToVectorMap(mapname, airspace.getAirspaceB3aLocations(), polygonStyle);
            }
        }
    }
}