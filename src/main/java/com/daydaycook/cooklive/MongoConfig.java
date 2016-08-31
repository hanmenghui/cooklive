package com.daydaycook.cooklive;

import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by creekhan on 8/15/16.
 */
@Configuration
@PropertySource(value = {"classpath:mongo.properties"})
@Component
@Lazy(value = false)
public class MongoConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${mongo.cluster}")
    private boolean cluster;

    @Value("${mongo.hostName}")
    private String hostName;

    @Value("${mongo.port}")
    private int port;

    @Value("${mongo.dbname}")
    private String dbName;

    @Value("${mongo.cluster.nodes}")
    private String clusterNodes;

    public static JsonObject getMongoOptions() {
        MongoConfig mongoConfig = CookLiveApplication.getBean(MongoConfig.class);
        JsonObject jsonObject = new JsonObject();
        if (!mongoConfig.cluster) {
            jsonObject
                    .put("connection_string", "mongodb://" + mongoConfig.hostName
                            + ":" + mongoConfig.port);
        } else {
            List<Map> list = new ArrayList<>();
            for (String str : mongoConfig.clusterNodes.split(",")) {
                Map<String, Object> map = new HashMap<>();
                String[] nodeInfo = str.split(":");
                map.put("host", nodeInfo[0]);
                map.put("port", Integer.valueOf(nodeInfo[1]));
                list.add(map);

            }
            jsonObject.put("hosts", list);

        }
        LOGGER.info("Mongo Cluster info:{}", jsonObject.toString());
        return jsonObject;
    }
}
