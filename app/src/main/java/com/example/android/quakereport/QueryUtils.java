package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<QuakeInfo>fetchEarthquakeData(String requestUrl){
        // Create URL object
        URL url = QueryUtils.createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            //makeHttpRequest method throws IOException. Handle It here.
            jsonResponse = QueryUtils.makeHttpRequest(url);
        } catch (IOException e) {
            // TODO Handle the IOException
            Log.e(EarthquakeActivity.class.getSimpleName(),e.toString());

        }
        //Parse the jsonResponse from the url
        List<QuakeInfo> earthquakes = QueryUtils.extractEarthquakes(jsonResponse);
        //Return the list of earthquakes
        return earthquakes;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Makes Http request
     * @param url
     * @return
     * @throws IOException
     */

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        if(url==null)return jsonResponse;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if(urlConnection.getResponseCode()==200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else
                Log.e(LOG_TAG,"Error response code----------->"+urlConnection.getResponseCode());
        } catch (IOException e) {
            // TODO: Handle the exception
            Log.e(LOG_TAG,"Unable to fetch data from internet",e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link QuakeInfo} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<QuakeInfo> extractEarthquakes(String sampleJsonResponse) {

        Log.v(LOG_TAG,"extractEarthquakes--------->Earthquake data being fetched");
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<QuakeInfo> earthquakes = new ArrayList<>();

        // Try to parse the sample. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {


            // build up a list of Earthquake objects with the corresponding data.
            if(TextUtils.isEmpty(sampleJsonResponse))return null;


            JSONObject root = new JSONObject(sampleJsonResponse);
            JSONArray responseArray = root.optJSONArray("features");
            for(int i=0;i<responseArray.length();i++){
                JSONObject jsonObject = responseArray.getJSONObject(i);
                JSONObject propertiesJsonObject = jsonObject.optJSONObject("properties");
                Double magnitude = propertiesJsonObject.getDouble("mag");
                String name = propertiesJsonObject.optString("place");
                long timeInMilliseconds = propertiesJsonObject.getLong("time");
                //Note down the URL
                String url = propertiesJsonObject.getString("url");
                QuakeInfo quakeInfo = new QuakeInfo(magnitude,name,timeInMilliseconds,url);
                earthquakes.add(quakeInfo);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}