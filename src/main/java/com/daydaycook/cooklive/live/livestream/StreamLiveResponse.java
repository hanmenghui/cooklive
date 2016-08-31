package com.daydaycook.cooklive.live.livestream;


import java.util.List;

/**
 * Created by creekhan on 7/7/16.
 */
public final class StreamLiveResponse {

    List<StreamLiveAddr> streamLiveAddrs;

    public List<StreamLiveAddr> getStreamLiveAddrs() {
        return streamLiveAddrs;
    }

    public void setStreamLiveAddrs(List<StreamLiveAddr> streamLiveAddrs) {
        this.streamLiveAddrs = streamLiveAddrs;
    }



 /*   private Rtmp rtmp;

    private Hls hls;

    private HttpFlv httpflv;


    public Rtmp getRtmp() {
        return rtmp;
    }

    public void setRtmp(Rtmp rtmp) {
        this.rtmp = rtmp;
    }

    public Hls getHls() {
        return hls;
    }

    public void setHls(Hls hls) {
        this.hls = hls;
    }

    public HttpFlv getHttpflv() {
        return httpflv;
    }

    public void setHttpflv(HttpFlv httpflv) {
        this.httpflv = httpflv;
    }

    public final class Rtmp {

        private final String origin;

        private final String _720p;

        private final String _480p;

        public Rtmp(String origin, String _720p, String _480p) {
            this.origin = origin;
            this._480p = _480p;
            this._720p = _720p;
        }

        public String getOrigin() {
            return origin;
        }

        public String get_720p() {
            return _720p;
        }

        public String get_480p() {
            return _480p;
        }
    }

    public final class Hls {

        private final String origin;

        private final String _720p;

        private final String _480p;

        public Hls(String origin, String _720p, String _480p) {
            this.origin = origin;
            this._480p = _480p;
            this._720p = _720p;
        }

        public String getOrigin() {
            return origin;
        }

        public String get_720p() {
            return _720p;
        }

        public String get_480p() {
            return _480p;
        }
    }

    public final class HttpFlv {

        private final String origin;

        private final String _720p;

        private final String _480p;

        public HttpFlv(String origin, String _720p, String _480p) {
            this.origin = origin;
            this._480p = _480p;
            this._720p = _720p;
        }

        public String getOrigin() {
            return origin;
        }

        public String get_720p() {
            return _720p;
        }

        public String get_480p() {
            return _480p;
        }
    }*/


}
