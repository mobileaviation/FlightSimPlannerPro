package com.mobileaviationtools.flightsimplannerpro.TileWorkers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.io.ByteArrayOutputStream;
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
        Double lat_rad = Math.toRadians(request.bounds.min.latitude);
        Double y = (1 - Math.log(Math.tan(lat_rad) + (1 / Math.cos(lat_rad))) / Math.PI ) / 2 * n;

        Long zi = Math.round(z1);
        Long xi = Math.round(x);
        Long yi = Math.round(y-1);

        Log.i("WmsTileWorker", "After round: Zoom: " + n + " X: " + x + " Y: " + y);

        //String u = "http://wms.chartbundle.com/tms/1.0.0/sec_3857/#Z#/#X#/#Y#.png?type=google";
        String u = "http://wms.chartbundle.com/tms/v1.0/sec_3857/#Z#/#X#/#Y#.png?type=google";
        u = u.replace("#Z#", zi.toString());
        u = u.replace("#X#", xi.toString());
        u = u.replace("#Y#", yi.toString());
        Log.i("WmsTileWorker", u);


        try {

            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap image = this.getBitmapFromURL(u, s);
            //image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            //request.image = new BitmapSimple();
            //request.image.width = 256;
            //request.image.height = 256;
            //request.image.imageData = stream.toByteArray();
            request.image = new BitmapSimple(image);

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
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inMutable = true;
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            //myBitmap.setHasAlpha(true);
            //Bitmap myBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
            //Canvas c = new Canvas(myBitmap);
            //Paint p = new Paint();

            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inMutable = true;
            //Bitmap downloadBitmap = BitmapFactory.decodeStream(input, null, options);
            //downloadBitmap.setHasAlpha(true);
            ////p.setAlpha(250);
            //c.drawBitmap(downloadBitmap, 0,0, p);

            //Integer cc = myBitmap.getPixel(10,10);
            //Log.i("WmsTileWorker", "Color: " + Integer.toHexString(cc));

            //myBitmap = setTransparentColor(0xFAFFFFFF, 0xFAFEFEFF, 0xFAFFFFFF, myBitmap);

            return myBitmap;
        } catch (IOException e) {
            // Log exception
            Log.e("", "Error downloading: " + src);
            //Log.e("", "Error downloading: " + wmsSrc);
            return Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        }
    }


    private Bitmap setTransparentColor(Integer c, Integer c1, Integer c2, Bitmap bitmap)
    {
        for (int x = 0; x<bitmap.getWidth(); x++)
        {
            for (int y = 0; y<bitmap.getHeight(); y++)
            {
                //Integer cb = bitmap.getPixel(x,y);
                if ((bitmap.getPixel(x,y) == c))// || (cb == c1))
                    bitmap.setPixel(x,y, android.graphics.Color.TRANSPARENT);
                if ((bitmap.getPixel(x,y) == c1))// || (cb == c1))
                    bitmap.setPixel(x,y, android.graphics.Color.TRANSPARENT);
                if ((bitmap.getPixel(x,y) == c2))// || (cb == c1))
                    bitmap.setPixel(x,y, android.graphics.Color.TRANSPARENT);
            }
        }

        return bitmap;
    }

}
