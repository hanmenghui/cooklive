/**
 * Created by creekhan on 8/26/16.
 * 本包下封装RedisClient.
 * Vertx提供RedisClient对Redis的访问,RedisClient基于异步请求实现.然其对RedisCluster 集群方式不支持,
 * 无法在此使用.故通过Jedis JedisCluster 封装为RedisClient 异步请求Redis
 */
package com.daydaycook.cooklive.redis;