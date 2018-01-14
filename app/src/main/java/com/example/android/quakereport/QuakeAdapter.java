package com.example.android.quakereport;
import android.support.v4.content.ContextCompat;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Tanupriya on 31-Dec-17.
 */

public class QuakeAdapter extends ArrayAdapter<QuakeInfo>{

    private static final String LOCATION_SEPARATOR = " of ";


    public QuakeAdapter(@NonNull Context context, @NonNull ArrayList<QuakeInfo> quakeInfo) {
        super(context, 0, quakeInfo);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Variables for computing primary and secondary location
        String primaryLocation;
        String secondaryLocation;

        QuakeInfo earthquakeInfo = getItem(position);
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_quake,parent,false);
        }
        //Set the magnitude
        TextView magnitudeTextView = (TextView)listItemView.findViewById(R.id.magnitude);
        //Format the magnitude
        DecimalFormat formatter = new DecimalFormat("0.0");
        String output = formatter.format(earthquakeInfo.getMagnitude());

        magnitudeTextView.setText(output);
        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(earthquakeInfo.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        String place = earthquakeInfo.getPlace();
        //Split the string
        if(place.contains(LOCATION_SEPARATOR)){
            String[] array = place.split(LOCATION_SEPARATOR);
            secondaryLocation = array[0]+LOCATION_SEPARATOR;
            primaryLocation = array[1];
        }
        else{

            secondaryLocation = getContext().getString(R.string.Near_the);
            primaryLocation = place;
        }
        //Set the primary location
        TextView primaryTextView = (TextView)listItemView.findViewById(R.id.primary_location);
        primaryTextView.setText(primaryLocation);
        //Set the secondary location
        TextView secondaryTextView = (TextView)listItemView.findViewById(R.id.secondary_location);
        secondaryTextView.setText(secondaryLocation);

        // Create a new Date object from the time in milliseconds of the earthquake
        Date dateObject = new Date(earthquakeInfo.getTimeInMs());

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDate = formatDate(dateObject);
        // Display the date of the current earthquake in that TextView
        dateView.setText(formattedDate);

        // Find the TextView with view ID time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);
        // Format the time string (i.e. "4:30PM")
        String formattedTime = formatTime(dateObject);
        // Display the time of the current earthquake in that TextView
        timeView.setText(formattedTime);



        return listItemView;
    }
    /*Return the magnitude color*/
    private int getMagnitudeColor(double magnitude) {
        int resourceId ;
        int switchMagnitude = (int) Math.floor(magnitude);
        switch (switchMagnitude){
            case 0:
            case 1: resourceId = R.color.magnitude1;break;
            case 2: resourceId = R.color.magnitude2;break;
            case 3: resourceId = R.color.magnitude3;break;
            case 4: resourceId = R.color.magnitude4;break;
            case 5: resourceId = R.color.magnitude5;break;
            case 6: resourceId = R.color.magnitude6;break;
            case 7: resourceId = R.color.magnitude7;break;
            case 8: resourceId = R.color.magnitude8;break;
            case 9: resourceId = R.color.magnitude9;break;
            default:resourceId = R.color.magnitude10plus;break;

        }
        return ContextCompat.getColor(getContext(), resourceId);
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getTimeInstance();
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        return timeFormat.format(dateObject);
    }



}
