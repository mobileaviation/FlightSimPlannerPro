package com.mobileaviationtools.flightsimplannerpro.Weather;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 28-11-2016.
 */

public class Metars extends ArrayList<Metar>{
    public Metars(Context context)
    {
        this.context = context;
    }

    private Context context;
    private final String TAG = "Metars";

    public void processMetarXml(String Xml)
    {
        Log.i(TAG, "Metar XML: " + Xml);

        XmlPullParser parser = android.util.Xml.newPullParser();
        try {
            parser.setInput(new StringReader(Xml));
            int eventType = parser.getEventType();
            Metar metar = null;
            String name = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.TEXT:
                    {
                        if (metar != null) {
                            if (name != null) try {
                                if (name.equals("raw_text"))
                                    metar.raw_text = parser.getText();
                                if (name.equals("station_id"))
                                    metar.setStation_id(parser.getText());
                                if (name.equals("observation_time"))
                                    metar.observation_time = parser.getText();

                                name = "";
                            } catch (Exception ee) {
                                Log.i(TAG, "Parse Metar Value XML Error: " + ee.getMessage());
                            }
                        }
                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        //name = null;
                        name = parser.getName();
                        if (name.equals("METAR")) metar = new Metar(context);

                        break;
                    }
                    case XmlPullParser.END_TAG:
                    {
                        if (parser.getName().equals("METAR")) {
                            this.add(metar);
                            metar = null;
                        }
                        break;
                    }

                }

                eventType = parser.next();
            }
            Log.i(TAG, "End XML Parsing : " + this.size());
        }
        catch (Exception ee)
        {
            Log.e(TAG, "Metar XML Parse error: " + ee.getMessage());
        }
    }
}
