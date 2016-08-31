package com.daydaycook.cooklive;

import io.vertx.redis.RedisOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by creekhan on 7/22/16.
 */
@Configuration
@PropertySource(value = {"classpath:redis.properties"})
@Component
@Lazy(value = false)
public class RedisConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);
    private static RedisClusterConfig redisClusterConfig = null;
    @Value("${redis.cluster}")
    private boolean cluster = false;
    @Value("${redis.hostName}")
    private String hostName = "127.0.0.1";
    // Replace with your hub name
    @Value("${redis.port}")
    private int port = 6379;
    @Value("${redis.cluster.nodes}")
    private String clusterNodes;
    @Value("${redis.cluster.timeout}")
    private int timeOut = 2500;
    @Value("${redis.cluster.max_redirection}")
    private int maxRedirection = 6;

    public static boolean isCluster() {
        RedisConfig redisConfig = CookLiveApplication.getBean(RedisConfig.class);
        return redisConfig.cluster;
    }

    public static RedisOptions getRedisOptions() {
        RedisOptions redisOptions = new RedisOptions();
        RedisConfig redisConfig = CookLiveApplication.getBean(RedisConfig.class);
        redisOptions.setHost(redisConfig.hostName);
        redisOptions.setPort(redisConfig.port);
        return redisOptions;

    }

    public static RedisClusterConfig getRedisClusterConfig() {
        if (redisClusterConfig == null) {
            synchronized (RedisClusterConfig.class) {
                if (redisClusterConfig == null) {
                    RedisConfig redisConfig = CookLiveApplication.getBean(RedisConfig.class);
                    redisClusterConfig = new RedisClusterConfig();
                    Set<HostAndPort> jedsiClusterNode = new HashSet<>();
                    LOGGER.info("Redis Cluster(timeout:{}) nodes: ", redisConfig.timeOut);
                    for (String node : redisConfig.clusterNodes.split(",")) {
                        String[] nodeInfo = node.split(":");
                        HostAndPort hostAndPor = new HostAndPort(nodeInfo[0], Integer.parseInt(nodeInfo[1]));
                        jedsiClusterNode.add(hostAndPor);
                        LOGGER.info("{}:{}", nodeInfo[0], nodeInfo[1]);
                    }
                    redisClusterConfig.setClusterNodes(jedsiClusterNode);
                    redisClusterConfig.setTimeOut(redisConfig.timeOut);
                    redisClusterConfig.setMaxRedirection(redisConfig.maxRedirection);
                }
            }
        }
        return redisClusterConfig;

    }

    public static class RedisClusterConfig {

        private Set<HostAndPort> clusterNodes;

        private int timeOut;

        private int maxRedirection;

        public Set<HostAndPort> getClusterNodes() {
            return clusterNodes;
        }

        public void setClusterNodes(Set<HostAndPort> clusterNodes) {
            this.clusterNodes = clusterNodes;
        }

        public int getTimeOut() {
            return timeOut;
        }

        public void setTimeOut(int timeOut) {
            this.timeOut = timeOut;
        }

        public int getMaxRedirection() {
            return maxRedirection;
        }

        public void setMaxRedirection(int maxRedirection) {
            this.maxRedirection = maxRedirection;
        }
    }
}
