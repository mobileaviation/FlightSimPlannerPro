package com.mobileaviationtools.flightsimplannerpro.TileWorkers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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

        Double zd = (360/(request.bounds.max.longitude - request.bounds.min.longitude));
        Double z1 = Math.log(zd) / Math.log(2);
        Double n = Math.pow(2, z1);
        Double x = ((request.bounds.min.longitude + 180) / 360 * n) ;
        //Double y = n * ((90 - request.bounds.min.latitude) / 180);
        //Double y = n * (1 - (Math.atan(Math.toRadians(request.bounds.min.latitude))));
        double lat_rad = Math.toRadians(request.bounds.min.latitude);
        Double y = (1 - Math.log(Math.tan(lat_rad) + (1 / Math.cos(lat_rad))) / Math.PI ) / 2 * n;

        Integer zi = z1.intValue();
        x = x + 0.5d;
        Integer xi = x.intValue();
        y = y + 0.5d;
        Integer yi = y.intValue() - 1;

        Log.i("WmsTileWorker", " Zoom: " + n + " X: " + x + " Y: " + y);

        //String u = "http://wms.chartbundle.com/tms/1.0.0/sec_3857/#Z#/#X#/#Y#.png?origin=nw";
        String u = "http://wms.chartbundle.com/tms/1.0.0/sec/#Z#/#X#/#Y#.png?origin=nw";
        u = u.replace("#Z#", zi.toString());
        u = u.replace("#X#", xi.toString());
        u = u.replace("#Y#", yi.toString());
        Log.i("WmsTileWorker", u);


        try {
            request.image = new BitmapSimple(this.getBitmapFromURL(u, s));

            if (request.image == null)
                request.image = new BitmapSimple(Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888));
        }
        catch (Exception ee)
        {
            Log.e("WmsTileWorker", "bitmap exception");
        }
        request.responseType = TileProviderResponse.kTileResponseRenderImage;
        request.isOpaque = false;
    }

    public Bitmap getBitmapFromURL(String src, String wmsSrc) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            //Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Bitmap myBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(myBitmap);
            Paint p = new Paint();
            //Path CirclePath = new Path();
            //p.setAntiAlias(true);
            //p.setColor(0xffff0000);
            //CirclePath.addCircle(100,100,100, Path.Direction.CW);

            Bitmap downloadBitmap = BitmapFactory.decodeStream(input);
            //downloadBitmap.eraseColor(0xffffff00);
            p.setAlpha(200);
            c.drawBitmap(downloadBitmap, 0,0, p);
            //c.drawPath(CirclePath, p);

            return myBitmap;
        } catch (IOException e) {
            // Log exception
            Log.e("", "Error downloading: " + src);
            Log.e("", "Error downloading: " + wmsSrc);
            return Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        }
    }

}
