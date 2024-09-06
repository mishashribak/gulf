package com.app.khaleeji.Response.fetchHotspotTimeLine;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dcube on 04-07-2018.
 */

public class HotspotMedialist {

    @SerializedName("HotspotDetails")
    @Expose
    private HotspotDetails hotspotDetails;
    @SerializedName("daily")
    @Expose
    private List<HotspotDaily> daily = new ArrayList<>();
    @SerializedName("memories")
    @Expose
    private List<HotspotMemory> memories = new ArrayList<>();
    @SerializedName("topMemories")
    @Expose
    private List<HotspotTopMemory> topMemories = new ArrayList<>();

    public List<HotspotDaily> getDaily() {
        return daily;
    }

    public void setDaily(List<HotspotDaily> daily) {
        this.daily = daily;
    }

    public List<HotspotMemory> getMemories() {
        return memories;
    }

    public void setMemories(List<HotspotMemory> memories) {
        this.memories = memories;
    }

    public List<HotspotTopMemory> getTopMemories() {
        return topMemories;
    }

    public void setTopMemories(List<HotspotTopMemory> topMemories) {
        this.topMemories = topMemories;
    }

    public HotspotDetails getHotspotDetails() {
        return hotspotDetails;
    }

    public void setHotspotDetails(HotspotDetails hotspotDetails) {
        this.hotspotDetails = hotspotDetails;
    }
}
