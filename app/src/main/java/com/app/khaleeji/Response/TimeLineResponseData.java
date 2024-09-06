package com.app.khaleeji.Response;

public class TimeLineResponseData extends BlockUnblockResponse {

    private TimeLineData data = new TimeLineData();

    public TimeLineData getData() {
        return data;
    }

    public void setData(TimeLineData data) {
        this.data = data;
    }
}
