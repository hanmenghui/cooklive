package com.daydaycook.cooklive.live.livestream;

import java.util.List;

/**
 * Created by creekhan on 7/15/16.
 */
public final class StreamLiveAddr {

    private final StreamProtocol protocol;

    private final List<LiveAddr> liveAddrs;


    public StreamLiveAddr(StreamProtocol protocol, List<LiveAddr> liveAddrs) {
        this.protocol = protocol;
        this.liveAddrs = liveAddrs;
    }

    public StreamProtocol getProtocol() {
        return protocol;
    }

    public List<LiveAddr> getLiveAddrs() {
        return liveAddrs;
    }


    public static class LiveAddr {
        private final PixelType pixel;
        private final String source;

        public LiveAddr(PixelType pixelType, String source) {
            this.pixel = pixelType;
            this.source = source;
        }

        public PixelType getPixel() {
            return pixel;
        }

        public String getSource() {
            return source;
        }
    }


}
