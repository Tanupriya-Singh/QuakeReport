package com.example.android.quakereport;

import java.util.Date;

/**
 * Created by Tanupriya on 31-Dec-17.
 */

public class QuakeInfo {

    private double quakeMagnitude;
    private String quakeplace;
    private long quakeTimeInMs;
    private String quakeUrl;


    public QuakeInfo(double magnitude, String place, long date, String url){
        quakeMagnitude = magnitude;
        quakeplace = place;
        quakeTimeInMs = date;
        quakeUrl = url;
    }

    public double getMagnitude(){
        return quakeMagnitude;
    }
    public String getPlace(){
        return quakeplace;
    }
    public String getUrl(){return quakeUrl;}
    public long getTimeInMs(){
        return quakeTimeInMs;
    }


}