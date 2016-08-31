package com.daydaycook.cooklive.statistic;

/**
 * Created by creekhan on 7/20/16.
 */
public class Statistic {

    private int like;

    private int liveView;

    private transient int robotNumber;

    private String playBackUrl;

    private String streamingTime;


    public int getLike() {
        return like;
    }

    public Statistic setLike(int like) {
        this.like = like;
        return this;
    }


    public String getPlayBackUrl() {
        return playBackUrl;
    }

    public Statistic setPlayBackUrl(String playBackUrl) {
        this.playBackUrl = playBackUrl;
        return this;
    }

    public int getRobotNumber() {
        return robotNumber;
    }

    public Statistic setRobotNumber(int robotNumber) {
        this.robotNumber = robotNumber;
        return this;
    }

    public int getLiveView() {
        return liveView;
    }

    public Statistic setLiveView(int liveView) {
        this.liveView = liveView;
        return this;
    }

    public String getStreamingTime() {
        return streamingTime;
    }

    public Statistic setStreamingTime(String streamingTime) {
        this.streamingTime = streamingTime;
        return this;
    }
}
