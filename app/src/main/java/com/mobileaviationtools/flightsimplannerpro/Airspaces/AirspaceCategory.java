package com.mobileaviationtools.flightsimplannerpro.Airspaces;

import android.graphics.Color;
import android.graphics.Interpolator;

/**
 * Created by Rob Verhoef on 15-6-2016.
 */
//        polygonStyle.outlineColor = Color.GREEN;
//        polygonStyle.outlineWidth = 2;
//        polygonStyle.strokeColor = Color.YELLOW;
//        polygonStyle.strokeWidth = 20;
//        polygonStyle.fillColor = Color.TRANSPARENT;

public enum AirspaceCategory {
    // outlineColor, outlineWidth, strokeColor, strokeWidth, fillColor
    A (Color.BLACK, 0, 0xAF000000,4, Color.TRANSPARENT),
    AWY (Color.GREEN, 2, Color.YELLOW,5, Color.TRANSPARENT),
    B (Color.BLACK, 0, 0xBF000000,3, Color.TRANSPARENT),
    C (0xFFE4A19E, 2, 0x60E4A19E,5, Color.TRANSPARENT),
    CTR (Color.BLACK, 0, 0xFFE4A19E ,15, 0x60E4A19E),
    D (Color.BLACK, 0, 0xFFE4A19E ,5, 0x60E4A19),
    DANGER (Color.BLACK, 0, 0xFFFBA642,10, 0x30FBA642),
    Q (Color.BLACK, 0, 0xFFC06E8E,5, 0x50C06E8E),
    E (Color.BLACK, 0, 0xAF000000,3, Color.TRANSPARENT),
    F (Color.BLACK, 0, 0xAF000000,3, Color.TRANSPARENT),
    G (Color.BLACK, 0, 0xAF000000,3, Color.TRANSPARENT),
    GP (Color.BLACK, 0, Color.YELLOW,5, Color.TRANSPARENT),
    GLIDING (Color.BLACK, 0, Color.YELLOW,5, Color.TRANSPARENT),
    OTH (Color.BLACK, 0, Color.YELLOW,5, Color.TRANSPARENT),
    RESTRICTED (Color.BLACK, 0, 0xFFFBA642,10, 0x30FBA642),
    R (Color.BLACK, 0, 0xFFFBA642,10, 0x30FBA642),
    TMA (Color.BLACK, 0, 0xFF7C6D92,5, Color.TRANSPARENT),
    TMZ (Color.BLACK, 0, 0xFFC3819E,10, Color.TRANSPARENT),
    TSA (Color.BLACK, 0, 0xFF7C6D92,5, Color.TRANSPARENT),
    WAVE (Color.BLACK, 0, Color.YELLOW,5, Color.TRANSPARENT),
    W (Color.BLACK, 0, Color.YELLOW,5, Color.TRANSPARENT),
    PROHIBITED (Color.BLACK, 0, 0xFFFBA642,10, 0x30FBA642),
    P (Color.BLACK, 0, 0xFFFBA642,10, 0x30FBA642),
    FIR (Color.BLACK, 0, 0xAF000000,3, Color.TRANSPARENT),
    UIR (Color.BLACK, 0, 0xAF000000,3, Color.TRANSPARENT),
    RMZ (Color.BLACK, 0, 0xFFC06E8E,10, Color.TRANSPARENT),
    Z (Color.BLACK, 0, Color.YELLOW,5, Color.TRANSPARENT),
    ZP (Color.BLACK, 0, Color.YELLOW,5, Color.TRANSPARENT),
    UKN (Color.BLACK, 0, Color.YELLOW,5, Color.TRANSPARENT);

    private Integer outlineColor;
    private Integer outlineWidth;
    private Integer strokeColor;
    private Integer strokeWidth;
    private Integer fillColor;
    AirspaceCategory(Integer outlineColor,
                     Integer outlineWidth,
                     Integer strokeColor,
                     Integer strokeWidth,
                     Integer fillColor)
    {
        this.outlineColor = outlineColor;
        this.outlineWidth = outlineWidth;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.fillColor = fillColor;
    }

    public Integer getOutlineColor(){return outlineColor;}
    public Integer getOutlineWidth(){return outlineWidth;}
    public Integer getFillColor() {return fillColor;}
    public Integer getStrokeColor(){return strokeColor;}
    public Integer getStrokeWidth(){return strokeWidth;}

    @Override
    public String toString() {
        return super.toString();
    }
}
