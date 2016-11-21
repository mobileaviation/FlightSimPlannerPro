package com.mobileaviationtools.flightsimplannerpro.TileWorkers;

/**
 * Created by Rob Verhoef on 1-6-2016.
 */
public class TileProviderFormats {
    public static final String CHARTBUNDLE_FORMAT = "http://wms.chartbundle.com/wms" +
            "?service=WMS" +
            "&version=1.1.1" +
            "&request=GetMap" +
            "&layers=#LAYER#" +
            "&transparent=true" +
            "&bbox=%f,%f,%f,%f" +
            "&width=256" +
            "&height=256" +
            "&srs=EPSG:4326" +
            "&format=image/png" +
            "&styles=";

    public static final String CHARTBUNDLE_XYZ_FORMAT = "http://wms.chartbundle.com/tms/v1.0/#LAYER#/#Z#/#X#/#Y#.png?type=google";

    //http://wms.chartbundle.com/wms?service=WMS&version=1.1.1&request=GetCapabilities

    public enum chartBundleLayer {
        sec_4326,
        sec_3857,
        wac_4326,
        tac_4326,
        enrl_4326,
        enrh_4326,
        enra_4326;

        //@Override
        public String readable() {
            switch (this) {
                case sec_4326:
                    return "Sectional";
                case sec_3857:
                    return "Sectional";
                case wac_4326:
                    return "World Aeronautical";
                case tac_4326:
                    return "Terminal Area";
                case enrl_4326:
                    return "IFR Enroute Low";
                case enrh_4326:
                    return "IFR Enroute High";
                case enra_4326:
                    return "IFR Area";

                default:
                    return "";
            }
        }
    }

    public static final String YANDEX_FORMAT = "https://vec03.maps.yandex.net/tiles?l=map&v=4.86.0&x=#X#&y=#Y#&z=#Z#&scale=1&lang=en_US";

    public static final String MAPPY_FORMAT = "https://map1.mappy.net/map/1.0/slab/standard/256/#Z#/#X#/#Y#";

    public static final String OPENSTREET_FORMAT = "http://tile.openstreetmap.org/#Z#/#X#/#Y#.png";

    public static final String SKYLINES_FORMAT = "https://maps.skylines.aero/mapserver/" +
            "?service=WMS" +
            "&version=1.3.0" +
            "&request=GetMap" +
            "&layers=#LAYER#" +
            "&transparent=true" +
            "&bbox=%f,%f,%f,%f" +
            "&width=256" +
            "&height=256" +
            //"&crs=EPSG:3857" +
            "&crs=EPSG:4326" +
            "&format=image/png" +
            "&styles=";

    public enum skylinesLayer
    {
        Airports,
        Airspace;


        //@Override
        public String readable() {
            switch (this) {
                case Airports: return "Airports";
                case Airspace: return "Airspaces";
                default: return "";
            }

        }

    }

    public static final String CANADAWEATHER_FORMAT = "http://geo.weather.gc.ca/geomet/" +
            "?service=WMS" +
            "&version=1.1.1" +
            "&request=GetMap" +
            "&layers=#LAYER#" +
            "&transparent=true" +
            "&bbox=%f,%f,%f,%f" +
            "&width=256" +
            "&height=256" +
            "&crs=EPSG:3857" +
            "&format=image/png" +
            "&styles=#STYLE#";

//    public enum canadamapLayer
//    {
//        ETA_UU,
//        ETA_PN,
//        RADAR_RDBR;
//
//        @Override
//        public String toString()
//        {
//            switch (this) {
//
//                case ETA_UU:
//                    return "GDPS." + super.toString();
//                case ETA_PN:
//                    return "GDPS." + super.toString();
//                case RADAR_RDBR:
//                    return super.toString();
//                default:
//                    return "";
//
//            }
//        }
//
//        public String readable() {
//            switch (this) {
//
//                case ETA_UU:
//                    return "Wind";
//                case ETA_PN:
//                    return "Sea level pressure";
//                case RADAR_RDBR:
//                    return "North America RADAR";
//
//                default:
//                    return "";
//
//            }
//        }
//    }

    public enum canadamapStyle
    {
        WINDARROW,          // UU
        WINDARROWKMH,       // UU
        PRESSURE4_LINE,     // PN
        RADARURPREFLECTR,
        RADAR;

        public String readable() {
            switch (this) {

                case WINDARROW:
                    return "WindArrow knots";
                case WINDARROWKMH:
                    return "WindArrow km/h";
                case PRESSURE4_LINE:
                    return "Pressure 4mb";
                case RADARURPREFLECTR:
                    return "Rain mode reflectivity 14 Colors";
                case RADAR:
                    return "\"Rain mode reflectivity Blueish";
                default:
                    return "";

            }
        }
    }

    public static final String OPENWEATHERMAP_FORMAT = "http://wms.openweathermap.org/service" +
            "?service=WMS" +
            "&version=1.1.1" +
            "&request=GetMap" +
            "&layers=#LAYER#" +
            "&transparent=true" +
            "&bbox=%f,%f,%f,%f" +
            "&width=256" +
            "&height=256" +
            "&srs=EPSG:900913" +
            "&format=image/png" +
            "&styles=";

    public static final String OPENWEATHERTILE_FORMAT = "http://tile.openweathermap.org/map/" +
            "#LAYER#/" +
            "{zoom}/{x}/{y}.png";

    public static final String AIRPORTMAP_FORMAT = "http://www.robenanita.nl/tileserver/tileserver.php?%2Findex.json&callback=_callbacks_._0iepyf5mh?/"+
            "#LAYER#/"+  //EHAM-VAC-1
            "{zoom}/{x}/{y}.png";

    public enum weathermapLayer {
        clouds,
        precipitation,
        pressure_cntr,
        ETA_UU,
        ETA_PN,
        RADAR_RDBR;

        //@Override
        public String readable() {
            switch (this) {
                case clouds:
                    return "Clouds";
                case precipitation:
                    return "Precipitation";
                case pressure_cntr:
                    return "Pressure Contours";
                case ETA_UU:
                    return "Wind";
                case ETA_PN:
                    return "Sea level pressure";
                case RADAR_RDBR:
                    return "North America RADAR";
                default:
                    return "";
            }
        }

        @Override
        public String toString()
        {
            switch (this) {
                case clouds:
                    return "clouds";
                case precipitation:
                    return "precipitation";
                case pressure_cntr:
                    return "pressure_cntr";
                case ETA_UU:
                    return "GDPS." + super.toString();
                case ETA_PN:
                    return "GDPS." + super.toString();
                case RADAR_RDBR:
                    return super.toString();
                default:
                    return "";

            }
        }

        public String layerName()
        {
            switch (this) {
                case clouds:
                    return "clouds";
                case precipitation:
                    return "precipitation";
                case pressure_cntr:
                    return "pressure_cntr";
                case ETA_UU:
                    return super.toString();
                case ETA_PN:
                    return super.toString();
                case RADAR_RDBR:
                    return super.toString();
                default:
                    return "";

            }
        }
    }
}
