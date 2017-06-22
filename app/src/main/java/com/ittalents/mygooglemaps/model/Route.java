package com.ittalents.mygooglemaps.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;


/**
 * Created by ASUS on 22.6.2017 г..
 */

public class Route {

    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
