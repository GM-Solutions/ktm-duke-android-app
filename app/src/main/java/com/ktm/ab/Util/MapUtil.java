package com.ktm.ab.Util;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

/**
 * Created by nikhil on 4/1/17.
 */

public class MapUtil {

    public static double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = (earthRadius * c) * 1.60934; //converting miles to km

        return dist; // output distance, in Km
    }

    public static double getCurrentRadius(GoogleMap map) {
        LatLng latLng = map.getCameraPosition().target;
        VisibleRegion vr = map.getProjection().getVisibleRegion();
        return distance(latLng.latitude, latLng.longitude, vr.latLngBounds.southwest.latitude, vr.latLngBounds.southwest.longitude);
    }


}
