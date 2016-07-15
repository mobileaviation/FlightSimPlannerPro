package com.mobileaviationtools.flightsimplannerpro.LocationService;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;

/**
 * Created by Rob Verhoef on 15-7-2016.
 */
public class NativeLocation implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    public NativeLocation(Context context)
    {
        this.context = context;

        if (client == null)
        {
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(1000);
            locationRequest.setInterval(500);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            client = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            connected = false;
        }
    }

    private final String TAG = "NativeLocation";
    private Context context;
    private GoogleApiClient client;
    LocationRequest locationRequest;

    public Location currectLocation;
    public Boolean connected;

    public void connect()
    {
        client.connect();
    }

    public void disconnect()
    {
        client.disconnect();
        connected = false;
        if (onConnectionChanged != null) onConnectionChanged.Disconnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        currectLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        Log.i(TAG, "Connected to GPS at: " + currectLocation.getLongitude() + " : " + currectLocation.getLatitude());
        connected = true;
        if (onConnectionChanged != null) onConnectionChanged.Connected();
        this.onLocationChanged(currectLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currectLocation = location;
        Log.i(TAG, "Current GPS location: " + currectLocation.getLongitude() + " : " + currectLocation.getLatitude());
        if (onLocationChangedListener != null) onLocationChangedListener.onLocationChanged(currectLocation);
    }

    private LocationSource.OnLocationChangedListener onLocationChangedListener;
    public void setOnLocationChangedListener(final LocationSource.OnLocationChangedListener l)
    {
        onLocationChangedListener = l;
    }

    private OnConnectionChanged onConnectionChanged;
    public void setOnConnectionChanged(final OnConnectionChanged c)
    {
        onConnectionChanged = c;;
    }
    public interface OnConnectionChanged
    {
        public void Connected();
        public void Disconnected();
    }
}
