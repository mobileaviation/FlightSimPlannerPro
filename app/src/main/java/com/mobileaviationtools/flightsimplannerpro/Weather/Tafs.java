package com.mobileaviationtools.flightsimplannerpro.Weather;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 28-11-2016.
 */

public class Tafs extends ArrayList<Taf> {
    public Tafs(Context context)
    {
        this.context = context;
    }

    private Context context;
    private String TAG = "Tafs";

    public void processTafXml(String Xml)
    {
        Log.i(TAG, "Taf XML: " + Xml);
        XmlPullParser parser = android.util.Xml.newPullParser();
        try {
            parser.setInput(new StringReader(Xml));
            int eventType = parser.getEventType();
            Taf taf = null;
            String name = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.TEXT: {
                        if (taf != null) {
                            if (name != null) {
                                try {
                                    // TAF data Fields --------------------
                                    if (name.equals("raw_text"))
                                        taf.raw_text = parser.getText();
                                    if (name.equals("station_id"))
                                        taf.setStation_id(parser.getText());
                                    if (name.equals("issue_time"))
                                        taf.issue_time = parser.getText();

                                    name = "";
                                } catch (Exception ee) {
                                    Log.i(TAG, "Parse Taf Value XML Error: " + ee.getMessage());
                                }
                            }
                        }
                        break;
                    }
                    case XmlPullParser.START_TAG: {
                        //name = null;
                        name = parser.getName();

                        if (name.equals("TAF")) taf = new Taf(context);


                        break;
                    }
                    case XmlPullParser.END_TAG:
                    {
                        if (parser.getName().equals("TAF")) {
                            this.add(taf);
                            taf = null;
                        }

                        break;
                    }
                }
                eventType = parser.next();
            }
        }
        catch (Exception ee)
        {
            Log.e(TAG, "Taf XML Parse error: " + ee.getMessage());
        }
    }
}
