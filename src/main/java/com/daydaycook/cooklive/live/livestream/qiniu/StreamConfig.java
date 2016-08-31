package com.daydaycook.cooklive.live.livestream.qiniu;

import com.daydaycook.cooklive.CookLiveApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

/**
 * Created by creekhan on 7/6/16.
 */
@Configuration
@PropertySource(value = {"classpath:cooklive/qiniu/stream.properties"})
@Component
@Lazy(value = false)
public class StreamConfig {

    //为确保安全 将Access/Secret Key 直接写入代码   也可通过加密解密算法加以保存
    static final String ACCESS_KEY = "0pUkcav-pOsX3gyZjSPvRA0RW-n7awfoHre1gyo3";

    static final String SECRET_KEY = "eUfsrW4LEYWWB3b28wGZ1o3RabvqI3q_mtrXmEU0";

    static final String PUBLISHSECURITY = "static";

    @Value("${publishkey}")
    private String publishkey = "879948b7-e2ad-42df-8b84-c0dd7cf566a2";

    @Value("${hub_name}")
    private String hub_name = "ririzhu"; // The Hub must be exists before use


    public static String hubName() {
        return CookLiveApplication.getBean(StreamConfig.class).hub_name;
    }

    public static String publishKey() {
        return CookLiveApplication.getBean(StreamConfig.class).publishkey;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }


}
