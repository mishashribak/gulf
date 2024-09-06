package com.app.khaleeji.Response;

import java.util.ArrayList;
import java.util.List;

public class TimeLineData {

    private List<DialiesData> dialies = new ArrayList<>();
    private MemoriesData memories = new MemoriesData();
    private List<HotspotData> hotspots = new ArrayList<>();

    public List<DialiesData> getDialies() {
        return dialies;
    }

    public void setDialies(List<DialiesData> dialies) {
        this.dialies = dialies;
    }

    public MemoriesData getMemories() {
        return memories;
    }

    public void setMemories(MemoriesData memories) {
        this.memories = memories;
    }

    public List<HotspotData> getHotspots() {
        return hotspots;
    }

    public void setHotspots(List<HotspotData> hotspots) {
        this.hotspots = hotspots;
    }
}
