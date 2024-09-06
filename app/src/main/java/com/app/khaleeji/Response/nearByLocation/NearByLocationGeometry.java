package com.app.khaleeji.Response.nearByLocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Dcube on 11-10-2018.
 */

public class NearByLocationGeometry {


    @SerializedName("location")
    @Expose
    private NearByLocationDetails location;
    @SerializedName("viewport")
    @Expose
    private NearByLocationViewport viewport;

    public NearByLocationDetails getLocation() {
        return location;
    }

    public void setLocation(NearByLocationDetails location) {
        this.location = location;
    }

    public NearByLocationViewport getViewport() {
        return viewport;
    }

    public void setViewport(NearByLocationViewport viewport) {
        this.viewport = viewport;
    }

}
