package com.daydaycook.cooklive.im;

import com.daydaycook.cooklive.CookLiveInitializer;
import com.daydaycook.cooklive.im.imserver.IMServer;
import com.daydaycook.cooklive.im.imserver.problem.IMException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created by creekhan on 7/20/16.
 */
public class IMServerManager {

    private static List<IMServer> cacheIMServer = new ArrayList<>(2);

    static {
        //  EventBus
        loadInitialIMServerProvide();
        CookLiveInitializer.deployVerticle(CookIMVerticle.class);
    }

    public static void registerIMServer(IMServer server) {
        if (cacheIMServer.contains(server)) {
            throw new IMException("there have exists  imserver:" + server.getClass().getName());
        }
        cacheIMServer.add(server);
    }

    public static IMServer getIMServer() {
        if (cacheIMServer.isEmpty()) {
            throw new IMException("there's no IMServer");
        }
        return cacheIMServer.get(0);
    }


    private static void loadInitialIMServerProvide() {
        ServiceLoader<IMServer> serviceLoader = ServiceLoader.load(IMServer.class);
        Iterator<IMServer> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            IMServer imServer = iterator.next();
            System.out.println("loaded IMServerProvide:" + imServer.getClass());
        }
    }

}
