package com.daydaycook.cooklive.im.imserver.rongyun;

import com.daydaycook.cooklive.CookLiveApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by creekhan on 7/13/16.
 */
@Configuration
@PropertySource(value = {"classpath:cooklive/rongyun/imconfig.properties"})
@Component
@Lazy(value = false)
public class RongYunConfig {

    @Value("${apiurl}")
    private String apiurl;

    @Value("${appkey}")
    private String appkey;

    @Value("${appsecret}")
    private String appsecret;


    public static String appKey() {
        return CookLiveApplication.getBean(RongYunConfig.class).appkey;
    }

    public static String appSecret() {
        return CookLiveApplication.getBean(RongYunConfig.class).appsecret;
    }

    public static String apiUrl() {
        return CookLiveApplication.getBean(RongYunConfig.class).apiurl;
    }


}
