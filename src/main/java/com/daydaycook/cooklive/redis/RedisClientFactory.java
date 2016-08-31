package com.daydaycook.cooklive.redis;

import com.daydaycook.cooklive.RedisConfig;
import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.impl.RedisClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提供创建RedisClient.共有两种实现,一种为Vertx所支持的RedisClientImpl(不支持 Redis Cluster 集群方式),一种为支持Redis集群通过RedisTeplate封装的
 * RedisClientClusterImpl
 * Created by creekhan on 8/26/16.
 */
public class RedisClientFactory {


    private static Logger LOGGER = LoggerFactory.getLogger(RedisClientFactory.class);

    /**
     * 特别注意:当使用vertx 自带rdis连接时,其不支持Redis Cluster 集群方式.
     */

    public static RedisClient create(Vertx vertx) {
        RedisClient redisClient;

        if (!RedisConfig.isCluster()) { //非集群
            redisClient = new RedisClientImpl(vertx, RedisConfig.getRedisOptions());
        } else {
            redisClient = new RedisClusterClientImpl(vertx, RedisConfig.getRedisClusterConfig());
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Redis Client implment:[\n {} \n ]", redisClient.getClass().getName());
        }

        return redisClient;
    }

}
