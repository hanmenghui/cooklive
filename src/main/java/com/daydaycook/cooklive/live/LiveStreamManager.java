package com.daydaycook.cooklive.live;

import com.daydaycook.cooklive.live.livestream.LiveBroadCastStream;
import com.daydaycook.cooklive.live.livestream.problem.LiveStreamNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 直播流管理
 * Created by creekhan on 7/15/16.
 */
public class LiveStreamManager {

    private static List<LiveBroadCastStream> streamCache = new ArrayList<>();

    static {
        loadInitialStreamSupport();
    }

    public static void regisgerStreamDriver(LiveBroadCastStream cls) {
        if (streamCache.contains(cls)) {
            throw new RuntimeException("已包含:" + cls.getClass().getName());
        }
        streamCache.add(cls);
    }

    public static LiveBroadCastStream getLiveBroadCastStream() {
        if (streamCache.isEmpty()) {
            throw new LiveStreamNotFoundException("没有可用的直播流提供者");
        }
        return streamCache.get(0);
    }


    private static void loadInitialStreamSupport() {
        ServiceLoader<LiveBroadCastStream> serviceLoader = ServiceLoader.load(LiveBroadCastStream.class);
        Iterator<LiveBroadCastStream> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            LiveBroadCastStream liveBroadCastStream = iterator.next();
        }
    }


}
