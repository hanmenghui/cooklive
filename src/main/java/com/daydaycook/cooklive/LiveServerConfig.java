package com.daydaycook.cooklive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 直播服务 监听配置信息
 * Created by creekhan on 7/13/16.
 */
@Configuration
@PropertySource(value = {"classpath:cooklive/imserver.properties"})
@Component
@Lazy(value = false)
public class LiveServerConfig {


    //监听地址
    @Value("${listen_host}")
    private String listenHost;

    //监听端口
    @Value("${listen_port}")
    private int listenPort;

    //鉴定服务
    @Value("${listen_path}")
    private String listenPath;


    @Value("${robotService}")
    private boolean robotService;


    public static String listenHost() {
        return CookLiveApplication.getBean(LiveServerConfig.class).listenHost;
    }

    public static int listenPort() {
        return CookLiveApplication.getBean(LiveServerConfig.class).listenPort;
    }

    public static String listenPath() {
        return CookLiveApplication.getBean(LiveServerConfig.class).listenPath;
    }

    public static boolean robotService() {
        return CookLiveApplication.getBean(LiveServerConfig.class).robotService;
    }


}
