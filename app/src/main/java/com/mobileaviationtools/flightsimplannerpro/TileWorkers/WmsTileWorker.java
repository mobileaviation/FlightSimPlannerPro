package com.mobileaviationtools.flightsimplannerpro.TileWorkers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import us.ba3.me.BitmapSimple;
import us.ba3.me.virtualmaps.TileProviderRequest;
import us.ba3.me.virtualmaps.TileProviderResponse;
import us.ba3.me.virtualmaps.TileWorker;

/**
 * Created by Rob Verhoef on 1-6-2016.
 */
public class WmsTileWorker extends TileWorker {
    public WmsTileWorker(String Url, String Style, String Layer)
    {
        m_url = Url;
        m_style = Style;
        m_layer = Layer;
    }

    private String m_url;
    private String m_style;
    private String m_layer;

    @Override
    public void doWork(TileProviderRequest request){
        String s = "";
        s = String.format(Locale.US, m_url,
                request.bounds.min.longitude,
                request.bounds.min.latitude,
                request.bounds.max.longitude,
                request.bounds.max.latitude);
        s = s.replace("#LAYER#", m_layer);
        s = s.replace("#STYLE#", m_style);

        Log.i("WmsTileWorker", s);

        request.image = new BitmapSimple(this.getBitmapFromURL(s));
        request.responseType = TileProviderResponse.kTileResponseRenderImage;
        request.isOpaque = false;
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        }
    }

}
