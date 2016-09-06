package com.daydaycook.cooklive.redis;

import com.daydaycook.cooklive.RedisConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisTransaction;
import io.vertx.redis.op.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 对于Redis 集群(Redis Cluster) 下的Redis 操作实现. 此实现尽针对Redis集群, 此类实现了大部分常用操作,其它不常用的则未实现,使用者调
 * 用具体操作前请事先检查本类是否已具体实现.
 * Created by creekhan on 8/26/16.
 */
public class RedisClusterClientImpl implements RedisClient {

    private static Logger LOGGER = LoggerFactory.getLogger(RedisClusterClientImpl.class);

    private JedisCluster jedisCluster;

    private Vertx vertx;

    public RedisClusterClientImpl(Vertx vertx, RedisConfig.RedisClusterConfig redisClusterConfig) {
        try {
            jedisCluster = new JedisCluster(redisClusterConfig.getClusterNodes(), redisClusterConfig.getTimeOut(),
                    redisClusterConfig.getMaxRedirection());
        } catch (Exception e) {
            LOGGER.error("**ERROR:{}", e.getMessage());
            throw e;
        }
        this.vertx = vertx;
    }


    @Override
    public void close(Handler<AsyncResult<Void>> handler) {

        vertx.executeBlocking(future -> {
            try {
                jedisCluster.close();
                future.succeeded();
            } catch (IOException e) {
                LOGGER.error("ERROR:{}", e.getMessage());
                future.fail(e.getMessage());
            }

        }, asyResult -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture());
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }

        });

    }

    @Override
    public RedisClient append(String key, String value, Handler<AsyncResult<Long>> handler) {
        vertx.executeBlocking(futrue -> {
            try {
                Long result = jedisCluster.append(key, value);
                futrue.complete(result);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                futrue.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient auth(String password, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient bgrewriteaof(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient bgsave(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient bitcount(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient bitcountRange(String key, long start, long end, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient bitop(BitOperation operation, String destkey, List<String> keys, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient bitpos(String key, int bit, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient bitposFrom(String key, int bit, int start, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient bitposRange(String key, int bit, int start, int stop, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient blpop(String key, int seconds, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient blpopMany(List<String> keys, int seconds, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient brpop(String key, int seconds, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient brpopMany(List<String> keys, int seconds, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient brpoplpush(String key, String destkey, int seconds, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient clientKill(KillFilter filter, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient clientList(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient clientGetname(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient clientPause(long millis, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient clientSetname(String name, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterAddslots(List<Long> slots, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterCountFailureReports(String nodeId, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterCountkeysinslot(long slot, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterDelslots(long slot, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterDelslotsMany(List<Long> slots, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterFailover(Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterFailOverWithOptions(FailoverOptions options, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterForget(String nodeId, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterGetkeysinslot(long slot, long count, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterInfo(Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterKeyslot(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterMeet(String ip, long port, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterNodes(Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterReplicate(String nodeId, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterReset(Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterResetWithOptions(ResetOptions options, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterSaveconfig(Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterSetConfigEpoch(long epoch, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterSetslot(long slot, SlotCmd subcommand, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterSetslotWithNode(long slot, SlotCmd subcommand, String nodeId, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterSlaves(String nodeId, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient clusterSlots(Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient command(Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient commandCount(Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient commandGetkeys(Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient commandInfo(List<String> commands, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient configGet(String parameter, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient configRewrite(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient configSet(String parameter, String value, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient configResetstat(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient dbsize(Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient debugObject(String key, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient debugSegfault(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient decr(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient decrby(String key, long decrement, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient del(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient delMany(List<String> keys, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient dump(String key, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient echo(String message, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient eval(String script, List<String> keys, List<String> args, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient evalsha(String sha1, List<String> keys, List<String> values, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient exists(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient expire(String key, int seconds, Handler<AsyncResult<Long>> handler) {
        vertx.executeBlocking(future -> {
            try {
                Long result = jedisCluster.expire(key, seconds);
                future.complete(result);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient expireat(String key, long seconds, Handler<AsyncResult<Long>> handler) {

        vertx.executeBlocking(future -> {
            try {
                Long result = jedisCluster.expireAt(key, seconds);
                future.complete(result);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient flushall(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient flushdb(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient get(String key, Handler<AsyncResult<String>> handler) {

        vertx.executeBlocking(future -> {
            try {
                String result = jedisCluster.get(key);
                future.complete(result);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<String> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;

    }

    @Override
    public RedisClient getBinary(String key, Handler<AsyncResult<Buffer>> handler) {
        return null;
    }

    @Override
    public RedisClient getbit(String key, long offset, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient getrange(String key, long start, long end, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient getset(String key, String value, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient hdel(String key, String field, Handler<AsyncResult<Long>> handler) {
        vertx.executeBlocking(future -> {
            try {
                Long result = jedisCluster.hdel(key, field);
                future.complete(result);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient hdelMany(String key, List<String> fields, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient hexists(String key, String field, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient hget(String key, String field, Handler<AsyncResult<String>> handler) {

        vertx.executeBlocking(future -> {
            try {
                String hreuslt = jedisCluster.hget(key, field);
                future.complete(hreuslt);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<String> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });

        return this;
    }

    @Override
    public RedisClient hgetall(String key, Handler<AsyncResult<JsonObject>> handler) {

        vertx.executeBlocking(future -> {
            Map<String, String> allValues = null;
            try {
                allValues = jedisCluster.hgetAll(key);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
                return;
            }
            JsonObject jsonObject = new JsonObject();
            for (Map.Entry<String, String> entry : allValues.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                jsonObject.put(k, v);
            }
            future.complete(jsonObject);
        }, (AsyncResult<JsonObject> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });

        return this;
    }

    @Override
    public RedisClient hincrby(String key, String field, long increment, Handler<AsyncResult<Long>> handler) {

        vertx.executeBlocking(future -> {
            try {
                Long incrReuslt = jedisCluster.hincrBy(key, field, increment);
                future.complete(incrReuslt);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });

        return this;
    }

    @Override
    public RedisClient hincrbyfloat(String key, String field, double increment, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient hkeys(String key, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient hlen(String key, Handler<AsyncResult<Long>> handler) {
        vertx.executeBlocking(future -> {
            try {
                Long setResult = jedisCluster.hlen(key);
                future.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient hmget(String key, List<String> fields, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient hmset(String key, JsonObject values, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient hset(String key, String field, String value, Handler<AsyncResult<Long>> handler) {

        vertx.executeBlocking(future -> {
            try {
                Long setResult = jedisCluster.hset(key, field, value);
                future.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient hsetnx(String key, String field, String value, Handler<AsyncResult<Long>> handler) {
        vertx.executeBlocking(future -> {
            try {
                Long setResult = jedisCluster.hsetnx(key, field, value);
                future.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient hvals(String key, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient incr(String key, Handler<AsyncResult<Long>> handler) {
        vertx.executeBlocking(futuer -> {
            try {
                Long setResult = jedisCluster.incr(key);
                futuer.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                futuer.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient incrby(String key, long increment, Handler<AsyncResult<Long>> handler) {
        vertx.executeBlocking(futuer -> {
            try {
                Long setResult = jedisCluster.incrBy(key, increment);
                futuer.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                futuer.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient incrbyfloat(String key, double increment, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient info(Handler<AsyncResult<JsonObject>> handler) {
        return null;
    }

    @Override
    public RedisClient infoSection(String section, Handler<AsyncResult<JsonObject>> handler) {
        return null;
    }

    @Override
    public RedisClient keys(String pattern, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient lastsave(Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient lindex(String key, int index, Handler<AsyncResult<String>> handler) {
        vertx.executeBlocking(futuer -> {
            try {
                String setResult = jedisCluster.lindex(key, index);
                futuer.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                futuer.fail(e);
            }
        }, (AsyncResult<String> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient linsert(String key, InsertOptions option, String pivot, String value, Handler<AsyncResult<Long>> handler) {

        vertx.executeBlocking(futuer -> {
            BinaryClient.LIST_POSITION list_position = option == InsertOptions.BEFORE ?
                    BinaryClient.LIST_POSITION.BEFORE : BinaryClient.LIST_POSITION.AFTER;
            try {
                Long setResult = jedisCluster.linsert(key, list_position, pivot, value);
                futuer.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                futuer.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient llen(String key, Handler<AsyncResult<Long>> handler) {
        vertx.executeBlocking(futuer -> {
            try {
                Long setResult = jedisCluster.llen(key);
                futuer.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                futuer.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient lpop(String key, Handler<AsyncResult<String>> handler) {
        vertx.executeBlocking(futuer -> {
            try {
                String setResult = jedisCluster.lpop(key);
                futuer.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                futuer.fail(e);
            }
        }, (AsyncResult<String> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient lpushMany(String key, List<String> values, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient lpush(String key, String value, Handler<AsyncResult<Long>> handler) {

        vertx.executeBlocking(futuer -> {
            try {
                Integer.valueOf("");
                Long setResult = jedisCluster.lpush(key, value);
                futuer.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                futuer.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient lpushx(String key, String value, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient lrange(String key, long from, long to, Handler<AsyncResult<JsonArray>> handler) {

        vertx.executeBlocking(future -> {
            try {
                List<String> lrangReuslt = jedisCluster.lrange(key, from, to);
                future.complete(new JsonArray(lrangReuslt));
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                future.fail(e);
            }
        }, (AsyncResult<JsonArray> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;

    }

    @Override
    public RedisClient lrem(String key, long count, String value, Handler<AsyncResult<Long>> handler) {
        vertx.executeBlocking(futuer -> {
            try {
                Long setResult = jedisCluster.lrem(key, count, value);
                futuer.complete(setResult);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                futuer.fail(e);
            }
        }, (AsyncResult<Long> asyResult) -> {
            if (handler != null) {
                if (asyResult.succeeded()) {
                    handler.handle(Future.succeededFuture(asyResult.result()));
                } else {
                    handler.handle(Future.failedFuture(asyResult.cause()));
                }
            }
        });
        return this;
    }

    @Override
    public RedisClient lset(String key, long index, String value, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient ltrim(String key, long from, long to, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient mget(String key, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient mgetMany(List<String> keys, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient migrate(String host, int port, String key, int destdb, long timeout, MigrateOptions options, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient monitor(Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient move(String key, int destdb, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient mset(JsonObject keyvals, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient msetnx(JsonObject keyvals, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient object(String key, ObjectCmd cmd, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient persist(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient pexpire(String key, long millis, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient pexpireat(String key, long millis, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient pfadd(String key, String element, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient pfaddMany(String key, List<String> elements, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient pfcount(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient pfcountMany(List<String> keys, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient pfmerge(String destkey, List<String> keys, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient ping(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient psetex(String key, long millis, String value, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient psubscribe(String pattern, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient psubscribeMany(List<String> patterns, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient pubsubChannels(String pattern, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient pubsubNumsub(List<String> channels, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient pubsubNumpat(Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient pttl(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient publish(String channel, String message, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient punsubscribe(List<String> patterns, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient randomkey(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient rename(String key, String newkey, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient renamenx(String key, String newkey, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient restore(String key, long millis, String serialized, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient role(Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient rpop(String key, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient rpoplpush(String key, String destkey, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient rpushMany(String key, List<String> values, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient rpush(String key, String value, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient rpushx(String key, String value, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient sadd(String key, String member, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient saddMany(String key, List<String> members, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient save(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient scard(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient scriptExists(String script, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient scriptExistsMany(List<String> scripts, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient scriptFlush(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient scriptKill(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient scriptLoad(String script, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient sdiff(String key, List<String> cmpkeys, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient sdiffstore(String destkey, String key, List<String> cmpkeys, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient select(int dbindex, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient set(String key, String value, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient setWithOptions(String key, String value, SetOptions options, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient setBinary(String key, Buffer value, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient setBinaryWithOptions(String key, Buffer value, SetOptions options, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient setbit(String key, long offset, int bit, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient setex(String key, long seconds, String value, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient setnx(String key, String value, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient setrange(String key, int offset, String value, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient sinter(List<String> keys, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient sinterstore(String destkey, List<String> keys, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient sismember(String key, String member, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient slaveof(String host, int port, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient slaveofNoone(Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient slowlogGet(int limit, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient slowlogLen(Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient slowlogReset(Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient smembers(String key, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient smove(String key, String destkey, String member, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient sort(String key, SortOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient spop(String key, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient spopMany(String key, int count, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient srandmember(String key, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient srandmemberCount(String key, int count, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient srem(String key, String member, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient sremMany(String key, List<String> members, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient strlen(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient subscribe(String channel, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient subscribeMany(List<String> channels, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient sunion(List<String> keys, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient sunionstore(String destkey, List<String> keys, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient sync(Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient time(Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisTransaction transaction() {
        return null;
    }

    @Override
    public RedisClient ttl(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient type(String key, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient unsubscribe(List<String> channels, Handler<AsyncResult<Void>> handler) {
        return null;
    }

    @Override
    public RedisClient wait(long numSlaves, long timeout, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient zadd(String key, double score, String member, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zaddMany(String key, Map<String, Double> members, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zcard(String key, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zcount(String key, double min, double max, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zincrby(String key, double increment, String member, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient zinterstore(String destkey, List<String> sets, AggregateOptions options, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zinterstoreWeighed(String destkey, Map<String, Double> sets, AggregateOptions options, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zlexcount(String key, String min, String max, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zrange(String key, long start, long stop, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient zrangeWithOptions(String key, long start, long stop, RangeOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient zrangebylex(String key, String min, String max, LimitOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient zrangebyscore(String key, String min, String max, RangeLimitOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient zrank(String key, String member, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zrem(String key, String member, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zremMany(String key, List<String> members, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zremrangebylex(String key, String min, String max, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zremrangebyrank(String key, long start, long stop, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zremrangebyscore(String key, String min, String max, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zrevrange(String key, long start, long stop, RangeOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient zrevrangebylex(String key, String max, String min, LimitOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient zrevrangebyscore(String key, String max, String min, RangeLimitOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient zrevrank(String key, String member, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zscore(String key, String member, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient zunionstore(String destkey, List<String> sets, AggregateOptions options, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient zunionstoreWeighed(String key, Map<String, Double> sets, AggregateOptions options, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient scan(String cursor, ScanOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient sscan(String key, String cursor, ScanOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient hscan(String key, String cursor, ScanOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient zscan(String key, String cursor, ScanOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient geoadd(String key, double longitude, double latitude, String member, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient geoaddMany(String key, List<GeoMember> members, Handler<AsyncResult<Long>> handler) {
        return null;
    }

    @Override
    public RedisClient geohash(String key, String member, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient geohashMany(String key, List<String> members, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient geopos(String key, String member, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient geoposMany(String key, List<String> members, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient geodist(String key, String member1, String member2, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient geodistWithUnit(String key, String member1, String member2, GeoUnit unit, Handler<AsyncResult<String>> handler) {
        return null;
    }

    @Override
    public RedisClient georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient georadiusWithOptions(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient georadiusbymember(String key, String member, double radius, GeoUnit unit, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }

    @Override
    public RedisClient georadiusbymemberWithOptions(String key, String member, double radius, GeoUnit unit, GeoRadiusOptions options, Handler<AsyncResult<JsonArray>> handler) {
        return null;
    }
}
