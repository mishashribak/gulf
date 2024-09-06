package com.app.khaleeji.Response.nearByLocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dcube on 11-10-2018.
 */

public class NearByLocationViewport {

    @SerializedName("northeast")
    @Expose
    private NearByLocationNortheast northeast;
    @SerializedName("southwest")
    @Expose
    private NearByLocationSouthwest southwest;

    public NearByLocationNortheast getNortheast() {
        return northeast;
    }

    public void setNortheast(NearByLocationNortheast northeast) {
        this.northeast = northeast;
    }

    public NearByLocationSouthwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(NearByLocationSouthwest southwest) {
        this.southwest = southwest;
    }


}
