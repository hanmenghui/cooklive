package com.daydaycook.cooklive.live.livestream;


import com.daydaycook.cooklive.live.livestream.problem.LiveStreamCreateFailException;
import com.daydaycook.cooklive.live.livestream.problem.LiveStreamNotFoundException;

/**
 * Created by creekhan on 7/6/16.
 */
public interface LiveBroadCastStream {

    /***
     * create LiveBroadCast Stream
     *
     * @return stream StreamPublishResponse ;
     */
    StreamPublishResponse createPublishStream(String streaId) throws LiveStreamCreateFailException;

    StreamLiveResponse getPullStream(String streamId) throws LiveStreamNotFoundException;

    String getStream(String streamId) throws LiveStreamNotFoundException;

    boolean deleteStream(String streamId) throws LiveStreamNotFoundException;

    String getPlayBackUrl(String streamId, long startTime) throws LiveStreamNotFoundException;


}
