package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by Tanupriya on 11-Jan-18.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<QuakeInfo>> {

    private static String urls;
    private static String LOG_TAG_LOADER = EarthquakeLoader.class.getSimpleName();

    public EarthquakeLoader(Context context, String localUrl) {
        super(context);
        if ((localUrl == null) || TextUtils.isEmpty(localUrl)) {
            urls = "";
        } else urls = localUrl;

        Log.v(LOG_TAG_LOADER,"--------->EarthquakeLoader constructor called");
    }

@Override
    public List<QuakeInfo> loadInBackground() {
        /** Code to delay the request
    try {
        Thread.sleep(2000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }*/
    // Don't perform the request if there are no URLs, or the first URL is null.
        if (urls == "") {
                return null;
        }

        List<QuakeInfo> result = QueryUtils.fetchEarthquakeData(urls);
        Log.v(LOG_TAG_LOADER,"loadInBackground()--------->http request is processed on a background thread");
        return result;

    }
    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG_LOADER,"onStartLoading()--------->forceLoad() is called");
        forceLoad();
    }
}
