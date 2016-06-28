package com.mobileaviationtools.flightsimplannerpro.TileWorkers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import us.ba3.me.BitmapSimple;
import us.ba3.me.virtualmaps.TileProviderRequest;
import us.ba3.me.virtualmaps.TileProviderResponse;
import us.ba3.me.virtualmaps.TileWorker;

/**
 * Created by Rob Verhoef on 1-6-2016.
 */
public class WmsTileWorker extends TileWorker {
    public WmsTileWorker(String Url, String Style, String Layer, String Mapname, String BaseCacheDir, Context context)
    {
        m_url = Url;
        m_style = Style;
        m_layer = Layer;
        m_mapName = Mapname;
        m_basePath = BaseCacheDir;
        this.context = context;
    }

    private String m_url;
    private String m_style;
    private String m_layer;
    private String m_mapName;
    private String m_basePath;
    private Context context;

    final private Long cacheTimeOut = Long.valueOf(14 * 24); // in hours

    @Override
    public void doWork(TileProviderRequest request){

        //Log.i("WmsTileWorker", "URL: " + m_url);

        Double zd = (360/(request.bounds.max.longitude - request.bounds.min.longitude));
        Double z1 = Math.log(zd) / Math.log(2);
        Double n = Math.pow(2, z1);
        Double x = ((request.bounds.min.longitude + 180) / 360 * n) ;
        Double lat_rad = Math.toRadians(request.bounds.min.latitude);
        Double y = (1 - Math.log(Math.tan(lat_rad) + (1 / Math.cos(lat_rad))) / Math.PI ) / 2 * n;

        Long zi = Math.round(z1);
        Long xi = Math.round(x);
        Long yi = Math.round(y-1);

        //Log.i("WmsTileWorker", "After round: Zoom: " + n + " X: " + x + " Y: " + y);

        String u = m_url;

        u = u.replace("#Z#", zi.toString());
        u = u.replace("#X#", xi.toString());
        u = u.replace("#Y#", yi.toString());
        u = u.replace("#LAYER#", m_layer);

        String cacheFilename = m_mapName + "-" +
                zi.toString() + "-" +
                xi.toString() + "-" +
                yi.toString() + ".png";

        //Log.i("WmsTileWorker", u);

        try {
            Bitmap image = this.getBitmapFromURL(u, cacheFilename);
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

    private void checkDirectory(String Path)
    {
        File p = new File(Path);
        if (!p.exists()) p.mkdir();
    }

    private boolean checkCache(String cacheFile)
    {
        try{
            File p = new File(cacheFile);
            if (p.exists())
            {
                Date today = new Date();
                long diff = TimeUnit.HOURS.convert(today.getTime() - p.lastModified(), TimeUnit.MILLISECONDS);

                if (diff < cacheTimeOut)
                    return true;
                else
                {
                    p.delete();
                    return false;
                }
            }
            else
                return false;
        }
        catch (Exception ee)
        {
            return false;
        }
    }

    public Bitmap getBitmapFromURL(String src, String cacheFilename) {
        checkDirectory(m_basePath + m_mapName + "/");
        String cacheFile = m_basePath + m_mapName + "/" + cacheFilename;

        Bitmap myBitmap = null;

        try {
            if (checkCache(cacheFile))
            {
                Log.i("WMSTileWorker", "Found cache file: " + cacheFile);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                myBitmap = BitmapFactory.decodeFile(cacheFile, options);
            }
            else
            {
                Log.i("WMSTileWorker", "No cache file: " + cacheFile + " found");
                Log.i("WMSTileWorker", "Load from URL: " + src);
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
                FileOutputStream stream = new FileOutputStream(cacheFile);
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
            }

            //File p = new File(cacheFile);



//            if (p.exists()) {
//                Log.i("WMSTileWorker", "Found cache file: " + cacheFile);
//                myBitmap = BitmapFactory.decodeStream(context.openFileInput(cacheFile));
//            }
//            else {
//                Log.i("WMSTileWorker", "No cache file: " + cacheFile + " found");
//
//                URL url = new URL(src);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
                //BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inMutable = true;
//                myBitmap = BitmapFactory.decodeStream(input);

//                FileOutputStream stream = new FileOutputStream(cacheFile);
//                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                stream.close();
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
//            }
//
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            Log.e("", "Error (down)loading: " + src);
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
