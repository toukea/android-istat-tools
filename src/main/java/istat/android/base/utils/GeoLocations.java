package istat.android.base.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class GeoLocations implements LocationListener {
    LocationManager manager;
    List<Location> locations = new ArrayList<Location>();
    Location bestLocation;
    Handler mHandler = new Handler();
    int timeOut = 30000;
    int badLocationLooperAccept = 2, badLocationCount = 0;
    boolean timeOutReached = false;
    float lastBestAccuracy = 100;
    Context context;
    static GeoLocations instance;

    public static GeoLocations geInstance(Context context) {
        if (instance == null) {
            instance = new GeoLocations(context);
        }
        return instance;
    }

    public GeoLocations(Context context) {
        this.context = context;
        instance = this;
        initInstance();
    }

    public void cancel() {

        manager.removeUpdates(this);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onLocationChanged(Location location) {

        locations.add(location);
        if (lastBestAccuracy > location.getAccuracy()) {
            badLocationCount = 0;
            lastBestAccuracy = location.getAccuracy();
        } else {
            badLocationCount++;
            if (badLocationCount >= badLocationLooperAccept)
                onLocationOK();
        }
        if (location.getAccuracy() <= acceptAccuracy) {
            mHandler.removeCallbacks(abortionRunnable);
            onLocationOK();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {


    }

    @Override
    public void onProviderEnabled(String provider) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        if (status == 1)
            cancel();
    }

    void initInstance() {
        manager = (LocationManager) getContext().getSystemService(
                Context.LOCATION_SERVICE);
        locations = new ArrayList<Location>();
        timeOutReached = false;
    }

    public boolean hasLocation() {
        if (locations.size() > 0)
            return true;
        else
            return false;
    }

    public Location[] getLocations() {
        Location[] tmps = new Location[locations.size()];
        return locations.toArray(tmps);
    }

    public void startGeoLocationRequest() {
        bestLocation = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1,
                this);
        mHandler.postDelayed(abortionRunnable
                , timeOut);
    }

    Runnable abortionRunnable = new Runnable() {

        @Override
        public void run() {

            timeOutReached = true;
            cancel();

        }
    };

    public void setLocationTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /*
     * public Location getLocation(){ return bestLocation;
     *
     * } public void onLocationChanged(LocationDialog dialog,Location location){
     * bestLocation=location; mHandler.removeCallbacks(abortionRunnable); cancel();
     * }
     */

    public void onLocationOK() {
        cancel();
    }

    public Location getLocation() {

        if (bestLocation != null)
            return bestLocation;
        for (Location location : locations) {
            if (bestLocation == null)
                bestLocation = location;

            if (location.getAccuracy() < bestLocation.getAccuracy())
                bestLocation = location;
        }
        return bestLocation;
    }

    public void setAcceptAccuracy(float acceptAccuracy) {
        this.acceptAccuracy = acceptAccuracy;
    }

    public boolean isLocationTimeOutReached() {
        return timeOutReached;
    }

    float acceptAccuracy = 2f;

}
