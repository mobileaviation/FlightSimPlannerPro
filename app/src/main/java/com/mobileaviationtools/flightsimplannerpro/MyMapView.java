package com.mobileaviationtools.flightsimplannerpro;

import android.content.Context;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.mobileaviationtools.flightsimplannerpro.Airports.AirportMarkerHit;
import com.mobileaviationtools.flightsimplannerpro.Airspaces.Airspaces;
import com.mobileaviationtools.flightsimplannerpro.Airspaces.WithinAirspaceCheck;
import com.mobileaviationtools.flightsimplannerpro.Database.MarkerProperties;
import com.mobileaviationtools.flightsimplannerpro.Route.RouteVisuals;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.AirportsTileWorker;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.TileProviderFormats;
import com.mobileaviationtools.flightsimplannerpro.TileWorkers.ZXYTileWorker;
import com.vividsolutions.jts.geom.Coordinate;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import us.ba3.me.;
import us.ba3.me.Location;
import us.ba3.me.Location3D;
import us.ba3.me.LocationReceiver;
import us.ba3.me.MapLoadingStrategy;
import us.ba3.me.MapView;
import us.ba3.me.virtualmaps.TileFactory;
import us.ba3.me.virtualmaps.VirtualMapInfo;

/**
 * Created by Rob Verhoef on 20-6-2016.
 */
public class MyMapView extends MapView {
    private String TAG = "MyMapView";
    public MyMapView(Context context) {


        super(context);
    }

    public void Init(RouteVisuals route)
    {
        this.routeVisuals = route;
        placingRoutePoint = false;
        movingRoutePoint = false;
        scrolling = false;


    }

    private RouteVisuals routeVisuals;
    private Boolean placingRoutePoint;
    private Boolean movingRoutePoint;
    private Boolean scrolling;
    private Integer PID;
    private Context context;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        Log.i(TAG, "onSurfaceCreated");
//        if (routeVisuals != null)
//		{
//			routeVisuals.drawRoute();
//		}
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                //Log.i("OnTouchEvent", "This is a OnTouch (ACTION_DOWN) event...");
                //route.UnSelectWaypoint();
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                //Log.i("OnTouchEvent", "This is a OnTouch (ACTION_MOVE) event...");
                if (movingRoutePoint) {
                    // update the route with the new location..
                    //event.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                //Log.i("OnTouchEvent", "This is a OnTouch (ACTION_UP) event...");
                routeVisuals.UnSelectWaypoint();
                placingRoutePoint = false;
                movingRoutePoint = false;
                break;
            }

        }

//        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new LocationReceiver() {
//            @Override
//            public void receiveLocation(Location location) {
//
//            }
//        });
        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new LocationReceiver(){
            @Override
            public void receiveLocation(Location loc) {
                //Log.w("OnTouchEvent", "lon:" + loc.longitude + " lat:" + loc.latitude);
                if (movingRoutePoint)
                    routeVisuals.dragSelectedWaypoint(loc);

            }});
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        Log.i("onScroll", "Action arg0: " + arg0.getAction() + " arg1: "  +
                arg1.getAction());
        Location3D location3D = this.getLocation3D();

        super.getLocationForPoint(new PointF(arg1.getX(), arg1.getY()), new LocationReceiver() {
            @Override
            public void receiveLocation (Location location) {
                scrolling = true;
                // Get bounds of current screen
                //MyMapView.this.get
            }
        });

        if (onVisualsChanged != null) onVisualsChanged.onScrolled(location3D, this);

        return super.onScroll(arg0, arg1, arg2, arg3);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Location3D location3D = this.getLocation3D();

        if (onVisualsChanged != null) onVisualsChanged.onScaled(location3D, this);

        super.onScale(detector);
    }

    @Override
    public void onLongPress(MotionEvent event) {

        // Place a new Route Point
        //
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                //Log.i("onLongPress", "This is a onLongPress (ACTION_DOWN) event...");
                if (routeVisuals.selectedWaypoint != null) {
                    movingRoutePoint = true;
                    placingRoutePoint = false;
                }
                else {
                    placingRoutePoint = true;
                    movingRoutePoint = false;
                }

                super.getLocationForPoint(new PointF(event.getX(), event.getY()), new LocationReceiver() {
                    @Override
                    public void receiveLocation(Location location) {
                        Log.w("onLongPress", "lon:" + location.longitude + " lat:" + location.latitude);
                        Coordinate c = new Coordinate(location.longitude, location.latitude);
                        Airspaces airspaces = new Airspaces(MyMapView.this.context);
                        WithinAirspaceCheck check = new WithinAirspaceCheck(MyMapView.this.context, airspaces, c);
                        check.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    }
                });

                break;
            }
        }

//        super.getLocationForPoint(new PointF(event.getX(), event.getY()), new ConvertPointCallback(){
//            @Override
//            public void convertComplete(Location loc) {
//                //Log.w("onLongPress", "lon:" + loc.longitude + " lat:" + loc.latitude);
//
//                if (placingRoutePoint) {
//                    String name = loc.longitude + "," + loc.latitude;
//                    routeVisuals.AddWaypoint(name, loc);
//
//                    //placingRoutePoint = false;
//                    movingRoutePoint = true;
//                    // insert the new point
//
//                }
//
//
//            }});

        super.onLongPress(event);
    }


    public void AddSkylineAirspacesMap()
    {
//        this.addInternetMap("SkylinesAirspaces",
//        "https://skylines.aero/mapproxy/tiles/1.0.0/airspace/{z}/{x}/{y}.png",
//        "",
//        20,
//        5,
//        3,
//        true,
//        true);
    }

    public void AddFAASectionalsMap()
    {
        String mapname = "FAA_sec_3857";
        TileFactory chartBundleFactory = new TileFactory(this);
        ZXYTileWorker chartBundleWorker = new ZXYTileWorker(
                TileProviderFormats.CHARTBUNDLE_XYZ_FORMAT,
                "",
                TileProviderFormats.chartBundleLayer.sec_3857.toString(),
                mapname,
                getBaseDir(),
                getContext());

        chartBundleFactory.addWorker(chartBundleWorker);

        VirtualMapInfo chartBundlemapInfo = new VirtualMapInfo();
        chartBundlemapInfo.name = mapname;
        chartBundlemapInfo.zOrder = 180;
        chartBundlemapInfo.maxLevel = 20;
        //chartBundlemapInfo.isSphericalMercator = false;
        chartBundlemapInfo.compressTextures = true;
        chartBundlemapInfo.tileProvider = chartBundleFactory;
        //chartBundlemapInfo.setTileProvider(chartBundleFactory);
        this.addMapUsingMapInfo(chartBundlemapInfo);
    }

    private Boolean added = false;
    public void AddMarkersMap(MarkerProperties markerProperties, Integer PID, Context context)
    {
        this.PID = PID;
        this.context = context;
        added = true;
        String mapname = "airportMarkerMap";
        TileFactory markersMapFactory = new TileFactory(this);

        AirportsTileWorker markersTileWorker = new AirportsTileWorker(this, context, mapname, markerProperties, PID);
        markersTileWorker.SetOnAirportTap(new AirportMarkerHit.OnAirportTap() {
            @Override
            public void onTap(String Ident) {
                Log.i(TAG, "Airport: " + Ident + " Tapped");
                if (onAirportTap != null) onAirportTap.onTap(Ident);
            }
        });
        markersMapFactory.addWorker(markersTileWorker);

        VirtualMapInfo markersTestMapInfo = new VirtualMapInfo();
        markersTestMapInfo.name = mapname;
        markersTestMapInfo.zOrder = 102;
        markersTestMapInfo.maxLevel = 20;
        markersTestMapInfo.alpha = 0;
        //markersTestMapInfo.isSphericalMercator = false;
        markersTestMapInfo.mapLoadingStrategy = MapLoadingStrategy.kHighestDetailOnly;
        //markersTestMapInfo.setTileProvider(markersMapFactory);
        markersTestMapInfo.tileProvider = markersMapFactory;
        this.addMapUsingMapInfo(markersTestMapInfo);

        Log.i(TAG, "Markersmap added!!");

    }

    public void AddMappyMap()
    {
        String mapname = "Mappy";
        TileFactory mappyFactory = new TileFactory(this);
        ZXYTileWorker mappyWorker = new ZXYTileWorker(
                TileProviderFormats.MAPPY_FORMAT,
                "",
                "",
                mapname,
                getBaseDir(),
                getContext());

        mappyFactory.addWorker(mappyWorker);

        VirtualMapInfo mappymapInfo = new VirtualMapInfo();
        mappymapInfo.name = mapname;
        mappymapInfo.zOrder = 1;
        mappymapInfo.maxLevel = 20;
        mappymapInfo.mapLoadingStrategy = MapLoadingStrategy.kHighestDetailOnly;
        mappymapInfo.tileProvider = mappyFactory;
        this.addMapUsingMapInfo(mappymapInfo);
    }

    public void AddOpenstreetMap()
    {
        String mapname = "Openstreet";
        TileFactory openstreetFactory = new TileFactory(this);
        ZXYTileWorker openstreetWorker = new ZXYTileWorker(
                TileProviderFormats.OPENSTREET_FORMAT,
                "",
                "",
                mapname,
                getBaseDir(),
                getContext());

        openstreetFactory.addWorker(openstreetWorker);

        VirtualMapInfo openstreetMapInfo = new VirtualMapInfo();
        openstreetMapInfo.name = mapname;
        openstreetMapInfo.zOrder = 1;
        openstreetMapInfo.maxLevel = 20;
        openstreetMapInfo.mapLoadingStrategy = MapLoadingStrategy.kHighestDetailOnly;
        openstreetMapInfo.tileProvider = openstreetFactory;
        this.addMapUsingMapInfo(openstreetMapInfo);
    }

    private String getBaseDir()
    {
        String p = this.getContext().getFilesDir().getPath() + "/";
        return p;
    }

    private OnVisualsChanged onVisualsChanged = null;
    public void setOnVisualsChanged( final OnVisualsChanged d) { onVisualsChanged = d; }
    public interface OnVisualsChanged {
        public void onScrolled(Location3D location3D, MyMapView myMapView);
        public void onScaled(Location3D location3D,MyMapView myMapView);
    }

    private AirportMarkerHit.OnAirportTap onAirportTap = null;
    public void setOnAirportTap(final AirportMarkerHit.OnAirportTap d) { onAirportTap = d; }
}
