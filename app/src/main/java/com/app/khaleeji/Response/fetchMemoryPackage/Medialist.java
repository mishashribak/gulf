package com.app.khaleeji.Response.fetchMemoryPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dcube on 04-07-2018.
 */

public class Medialist {

    @SerializedName("daily")
    @Expose
    private List<Daily> daily = new ArrayList<>();
    @SerializedName("memories")
    @Expose
    private List<Memory> memories = new ArrayList<>();
    @SerializedName("topMemories")
    @Expose
    private List<TopMemory> topMemories = new ArrayList<>();

    public List<Daily> getDaily() {
        return daily;
    }

    public void setDaily(List<Daily> daily) {
        this.daily = daily;
    }

    public List<Memory> getMemories() {
        return memories;
    }

    public void setMemories(List<Memory> memories) {
        this.memories = memories;
    }

    public List<TopMemory> getTopMemories() {
        return topMemories;
    }

    public void setTopMemories(List<TopMemory> topMemories) {
        this.topMemories = topMemories;
    }

}
