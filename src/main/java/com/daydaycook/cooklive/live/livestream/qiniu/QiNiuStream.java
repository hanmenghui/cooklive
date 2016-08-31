package com.daydaycook.cooklive.live.livestream.qiniu;

import com.daydaycook.cooklive.CookLiveApplication;
import com.daydaycook.cooklive.live.LiveStreamManager;
import com.daydaycook.cooklive.live.livestream.*;
import com.daydaycook.cooklive.live.livestream.problem.LiveStreamCreateFailException;
import com.daydaycook.cooklive.live.livestream.problem.LiveStreamNotFoundException;
import com.daydaycook.cooklive.utils.JsonHelper;
import com.pili.Hub;
import com.pili.PiliException;
import com.pili.Stream;
import com.qiniu.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by creekhan on 7/6/16.
 */
@Component
public class QiNiuStream extends AbstractLiveStream implements LiveBroadCastStream {

    private static final Credentials credentials = new Credentials(StreamConfig.ACCESS_KEY, StreamConfig.SECRET_KEY); // Credentials Object
    private static final Hub hub = new Hub(credentials, StreamConfig.hubName()); // Hub Object
    private static final String STREAM_TITLE_PREFIX = "cooklive_";
    private static Logger LOGGER = LoggerFactory.getLogger(QiNiuStream.class);

    static {
        LiveStreamManager.regisgerStreamDriver(CookLiveApplication.getBean(QiNiuStream.class));
    }


    @Override
    public StreamPublishResponse createPublishStream(String title) throws LiveStreamCreateFailException {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("创建直播流:{}", title);
        }

        try {
            Stream stream = hub.createStream(STREAM_TITLE_PREFIX.concat(title), StreamConfig.publishKey(), StreamConfig.PUBLISHSECURITY);
            System.out.println(JsonHelper.toJson(stream));
            String streamId = stream.getStreamId();
            Stream rtmpStream = hub.getStream(streamId);
            String rtmpURL = rtmpStream.rtmpPublishUrl();

            StreamPublishResponse streamResponse = new StreamPublishResponse(streamId, title, rtmpURL);
            return streamResponse;
        } catch (PiliException e) {
            LOGGER.error("Strem create error:{}", e.getMessage());
            throw new LiveStreamCreateFailException(e.getMessage());
        }
    }

    @Override
    public StreamLiveResponse getPullStream(String streamId) throws LiveStreamNotFoundException {
        try {
            Stream stream = hub.getStream(streamId);
            List<StreamLiveAddr> liveAddrs = new ArrayList<>(4);

            Map<String, String> rtmpUrls = stream.rtmpLiveUrls();
            String orignUrl = rtmpUrls.get("ORIGIN");
            String _720p = orignUrl.concat("@720p");
            String _480p = orignUrl.concat("@480p");

            List<StreamLiveAddr.LiveAddr> liveAddrList = new ArrayList<>();
            liveAddrList.add(new StreamLiveAddr.LiveAddr(PixelType.ORIGIN, orignUrl));
            liveAddrList.add(new StreamLiveAddr.LiveAddr(PixelType._720P, _720p));
            liveAddrList.add(new StreamLiveAddr.LiveAddr(PixelType._480P, _480p));
            StreamLiveAddr streamLiveAddr = new StreamLiveAddr(StreamProtocol.RTMP, liveAddrList);
            liveAddrs.add(streamLiveAddr);

            Map<String, String> hlsUrls = stream.hlsLiveUrls();
            String hlsOrginUrl = hlsUrls.get("ORIGIN");
            String hls720p = hlsOrginUrl.replace(".m3u8", "@720p.m3u8");
            String hls480p = hlsOrginUrl.replace(".m3u8", "@480p.m3u8");

            List<StreamLiveAddr.LiveAddr> hlsLiveAddrList = new ArrayList<>();
            hlsLiveAddrList.add(new StreamLiveAddr.LiveAddr(PixelType.ORIGIN, hlsOrginUrl));
            hlsLiveAddrList.add(new StreamLiveAddr.LiveAddr(PixelType._720P, hls720p));
            hlsLiveAddrList.add(new StreamLiveAddr.LiveAddr(PixelType._480P, hls480p));
            StreamLiveAddr hlsStreamLiveAddr = new StreamLiveAddr(StreamProtocol.HLS, hlsLiveAddrList);
            liveAddrs.add(hlsStreamLiveAddr);


            Map<String, String> httpflvUrls = stream.httpFlvLiveUrls();
            String flvOrginUrl = httpflvUrls.get("ORIGIN");
            String flv720p = flvOrginUrl.replace(".flv", "@720p.flv");
            String flv480p = flvOrginUrl.replace(".flv", "@480p.flv");

            List<StreamLiveAddr.LiveAddr> httpLiveAddrList = new ArrayList<>();
            httpLiveAddrList.add(new StreamLiveAddr.LiveAddr(PixelType.ORIGIN, flvOrginUrl));
            httpLiveAddrList.add(new StreamLiveAddr.LiveAddr(PixelType._720P, flv720p));
            httpLiveAddrList.add(new StreamLiveAddr.LiveAddr(PixelType._480P, flv480p));
            StreamLiveAddr httpFlvStreamLiveAddr = new StreamLiveAddr(StreamProtocol.HTTPFLV, httpLiveAddrList);
            liveAddrs.add(httpFlvStreamLiveAddr);

            StreamLiveResponse streamLiveResponse = new StreamLiveResponse();
            streamLiveResponse.setStreamLiveAddrs(liveAddrs);

            return streamLiveResponse;
        } catch (PiliException e) {
            LOGGER.error("Find stream:{}  error:{}", streamId, e.getMessage());
            throw new LiveStreamNotFoundException("根据流ID:" + streamId + "未找到对应的流信息");
        }

    }

    @Override
    public String getStream(String streamId) {
        try {
            Stream stream = hub.getStream(streamId);
            return stream.toJsonString();
        } catch (PiliException e) {
            throw new LiveStreamNotFoundException("根据流ID:" + streamId + "未找到对应的流信息");
        }
    }

    @Override
    public boolean deleteStream(String streamId) throws LiveStreamNotFoundException {
        Stream stream = null;
        try {
            stream = hub.getStream(streamId);
        } catch (PiliException e) {
            throw new LiveStreamNotFoundException("根据流ID:" + streamId + "未找到对应的流信息");
        }
        try {
            stream.delete();
        } catch (PiliException p) {
            LOGGER.error("delete stream:{}  error:{}", streamId, p.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String getPlayBackUrl(String streamId, long startTime) throws LiveStreamNotFoundException {
        LOGGER.info("QiNiu Stream  get {} playback url.startTime:{}", streamId, startTime);
        Stream stream = null;
        try {
            stream = hub.getStream(streamId);
        } catch (PiliException e) {
            throw new LiveStreamNotFoundException("根据流ID:" + streamId + "未找到对应的流信息");
        }

        long endHlsPlayback = System.currentTimeMillis() / 1_000;  // <= 0
        try {
            return stream.hlsPlaybackUrls(startTime / 1_000, endHlsPlayback).get(Stream.ORIGIN);
        } catch (PiliException e) {
            LOGGER.error("getPlayBackUrls stream:{}  error:{}", streamId, e.getMessage());

        }
        return null;
    }


}
