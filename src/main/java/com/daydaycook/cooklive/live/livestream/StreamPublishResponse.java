package com.daydaycook.cooklive.live.livestream;

/**
 * Created by creekhan on 7/7/16.
 */
public final class StreamPublishResponse {

    /**
     * title
     */
    private final String title;

    /**
     * 推流地址
     */
    private final String publishUrl;

    /**
     * 流Id
     */
    private final String streamId;

    public StreamPublishResponse(String streamId, String title, String publishUrl) {
        this.publishUrl = publishUrl;
        this.title = title;
        this.streamId = streamId;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishUrl() {
        return publishUrl;
    }

    public String getStreamId() {
        return streamId;
    }
}
