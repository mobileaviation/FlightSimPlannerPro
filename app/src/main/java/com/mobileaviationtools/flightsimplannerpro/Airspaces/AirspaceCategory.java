package com.mobileaviationtools.flightsimplannerpro.Airspaces;

import android.graphics.Color;
import android.graphics.Interpolator;

/**
 * Created by Rob Verhoef on 15-6-2016.
 */
public enum AirspaceCategory {
    A (Color.BLACK,Color.BLUE,1),
    AWY (Color.BLACK,Color.BLUE,1),
    B (Color.BLACK,Color.BLUE,1),
    C (Color.BLACK,Color.BLUE,1),
    CTR (Color.BLACK,Color.BLUE,1),
    D (Color.BLACK,Color.BLUE,1),
    DANGER (Color.BLACK,Color.BLUE,1),
    Q (Color.BLACK,Color.BLUE,1),
    E (Color.BLACK,Color.BLUE,1),
    F (Color.BLACK,Color.BLUE,1),
    G (Color.BLACK,Color.BLUE,1),
    GP (Color.BLACK,Color.BLUE,1),
    GLIDING (Color.BLACK,Color.BLUE,1),
    OTH (Color.BLACK,Color.BLUE,1),
    RESTRICTED (Color.BLACK,Color.BLUE,1),
    R (Color.BLACK,Color.BLUE,1),
    TMA (Color.BLACK,Color.BLUE,1),
    TMZ (Color.BLACK,Color.BLUE,1),
    TSA (Color.BLACK,Color.BLUE,1),
    WAVE (Color.BLACK,Color.BLUE,1),
    W (Color.BLACK,Color.BLUE,1),
    PROHIBITED (Color.BLACK,Color.BLUE,1),
    P (Color.BLACK,Color.BLUE,1),
    FIR (Color.BLACK,Color.BLUE,1),
    UIR (Color.BLACK,Color.BLUE,1),
    RMZ (Color.BLACK,Color.BLUE,1),
    Z (Color.BLACK,Color.BLUE,1),
    ZP (Color.BLACK,Color.BLUE,1),
    UKN (Color.BLACK,Color.BLUE,1);

    private Integer lineColor;
    private Integer fillColor;
    private Integer lineSize;
    AirspaceCategory(Integer lineColor, Integer fillColor, Integer lineSize)
    {
        this.lineColor = lineColor;
        this.fillColor = fillColor;
        this.lineSize = lineSize;
    }

    public Integer getLineColor() {return lineColor;}
    public Integer getfillColor() {return fillColor;}
    public Integer getlineSize() {return lineSize;}

    @Override
    public String toString() {
        return super.toString();
    }
}
