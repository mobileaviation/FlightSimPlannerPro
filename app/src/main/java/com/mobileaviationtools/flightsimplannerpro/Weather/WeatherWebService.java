package com.mobileaviationtools.flightsimplannerpro.Weather;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Rob Verhoef on 27-11-2016.
 */

public class WeatherWebService extends AsyncTask {
    public WeatherWebService(String url)
    {
        this.url = url;
    }

    private String url;
    private String r;

    private OnWeatherLoaded onWeatherLoaded;
    public void SetOnWeatherLoaded(OnWeatherLoaded d) { onWeatherLoaded = d; }

    @Override
    protected Object doInBackground(Object[] params) {
        r = readFromUrl(this.url);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if(onWeatherLoaded != null) onWeatherLoaded.OnWeatherXMLString(r);
        super.onPostExecute(o);
    }

    public interface OnWeatherLoaded
    {
        public void OnWeatherXMLString(String xml);
    }

    private String readFromUrl(String Url)
    {

        URL url = null;
        String Result = "";

        try {
            url = new URL(Url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return Result;
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream input = conn.getInputStream();
            Result = inputStreamToString(input).toString();

        } catch (IOException e) {
            e.printStackTrace();
            return Result;
        }

        return Result;
    }

    private StringBuilder inputStreamToString(InputStream is)
    {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        try
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            while ((rLine = rd.readLine()) != null)
            {
                answer.append(rLine);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return answer;
    }
}


