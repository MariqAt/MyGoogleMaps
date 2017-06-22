package com.ittalents.mygooglemaps.model;

import java.util.List;

/**
 * Created by ASUS on 22.6.2017 г..
 */

public interface IDirectionFinderListener {

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
