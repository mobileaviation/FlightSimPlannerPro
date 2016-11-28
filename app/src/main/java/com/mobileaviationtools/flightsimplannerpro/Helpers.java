package com.mobileaviationtools.flightsimplannerpro;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rob Verhoef on 13-7-2016.
 */
public class Helpers {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static LatLng getDutchFormatPosition(String text)
    {
        // Dutch notams
        // PSN 521837N0045613E
        // PSN 5403N00627E
        // PSN 53.18.4N 003.56.8E
        LatLng latLng = null;

        try {
            if (text.contains("PSN")) {
                String[] l = text.substring(text.indexOf("PSN") + 4, text.length()).split("[EW]");
                String loc = text.substring(text.indexOf("PSN") + 4, text.indexOf("PSN") + 4 + l[0].length()+1);
                loc = loc.replace(".", "");
                loc = loc.replace(" ", "");
                String[] ll = loc.split("[NS]");
                if (ll.length>1)
                {
                    String latstr = ll[0].replaceAll("[\\D]", "");
                    String lonstr = ll[1].replaceAll("[\\D]", "");

                    if (latstr.length()==4) latstr = latstr + "00";
                    if (latstr.length()==5) latstr = latstr.substring(0, 4) + "00";

                    if (lonstr.length()==5) lonstr = lonstr + "00";
                    if (lonstr.length()==5) lonstr = lonstr.substring(0, 5) + "00";

                    Double lat = Double.valueOf(latstr.substring(0,2)) +
                            (Double.valueOf(latstr.substring(2,4)) / 60) +
                            (Double.valueOf(latstr.substring(4,6)) / 3600)
                                    * ((loc.contains("S")) ? -1 : 1);
                    Double lon = Double.valueOf(lonstr.substring(0,3)) +
                            (Double.valueOf(lonstr.substring(3,5)) / 60) +
                            (Double.valueOf(lonstr.substring(5,7)) / 3600)
                                    * ((loc.contains("W")) ? -1 : 1);
                    latLng = new LatLng(lat,lon);

                }
            }
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            return null;
        }

        return latLng;
    }
}
