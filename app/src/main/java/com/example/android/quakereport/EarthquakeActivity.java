/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<QuakeInfo>> {


    /** The url for request of dat from USGS site*/
    private static final String USGS_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    /**Log tags for messages*/
    private static final String LOG_TAG = EarthquakeActivity.class.getSimpleName();
    /** Adapter for the list of earthquakes */
    private QuakeAdapter mAdapter;
    /**Loader id*/
    private static int EARTHQUAKE_LOADER_ID = 1;
    /**Instance for the empty text view*/
    private static TextView mEmptyStateTextView ;
    /**Instance of the progress bar*/
    private ProgressBar mStartingProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);


        // Find a reference to the {@link ListView} in the list_quake
        final ListView earthquakeListView = (ListView) findViewById(R.id.list_to_display);

        // Create a new {@link QuakeAdapter} of earthquakes
        mAdapter = new QuakeAdapter(this, new ArrayList<QuakeInfo>());

        /** Set the adapter on the {@link ListView}
         so the list can be populated in the user interface*/
        earthquakeListView.setAdapter(mAdapter);

        /**initialize the progress bar*/
        mStartingProgress = (ProgressBar) findViewById(R.id.loading_spinner);

        /**Set the visibility*/
        //mStartingProgress.setVisibility(View.VISIBLE);
        /**Set the empty text view*/

        mEmptyStateTextView = (TextView)findViewById( R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);


        /**Set up an onclick listener to open a specific url if user taps*/
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuakeInfo touchEarthquake = mAdapter.getItem(position);
                String URL = touchEarthquake.getUrl();
                openWebPage(URL);
            }
        });

        /**Check the network state of the app*/
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            /**Set up the instance of loader manager*/
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
            Log.v(LOG_TAG, "initLoader()--------->Loader initialized and running:::" + EARTHQUAKE_LOADER_ID);
        }
        else{
            /**Set the progress visibility to be gone*/
            mStartingProgress.setVisibility(View.GONE);
            /**Set the required text and color*/
            mEmptyStateTextView.setTextColor(getResources().getColor(R.color.colorBlack));
            mEmptyStateTextView.setText(R.string.no_internet);

        }
}

    @Override
    public Loader<List<QuakeInfo>> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG,"onCreateLoader()-------->New loader is being created:::"+EARTHQUAKE_LOADER_ID);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        Uri baseUri = Uri.parse(USGS_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<QuakeInfo>> loader, List<QuakeInfo> data) {
        /**Set the progress visibility to be gone once the data has loaded*/
        mStartingProgress.setVisibility(View.GONE);
        Log.v(LOG_TAG,"onLoadFinished()--------->Previous Loader has finished loading "+EARTHQUAKE_LOADER_ID);
        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link QuakeInfo}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
           mAdapter.addAll(data);
        }

        mEmptyStateTextView.setText(R.string.no_earthquakes);
        mEmptyStateTextView.setBackgroundResource(R.drawable.nature);

    }

    @Override
    public void onLoaderReset(Loader<List<QuakeInfo>> loader) {
        Log.v(LOG_TAG,"onLoadReset()--------->Loader is reset "+EARTHQUAKE_LOADER_ID);
        mAdapter.clear();
    }

    public void openWebPage(String url) {
        Uri webPage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
  }
