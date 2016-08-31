package com.daydaycook.cooklive.statistic;

import com.daydaycook.cooklive.im.MessageType;
import com.daydaycook.cooklive.im.User;
import com.daydaycook.cooklive.im.client.IMClient;
import com.daydaycook.cooklive.live.LiveStreamManager;
import com.daydaycook.cooklive.redis.RedisClientFactory;
import com.daydaycook.cooklive.utils.JsonHelper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 直播房间信息统计
 * Created by creekhan on 7/20/16.
 */
public class LiveStatistic {

    //房间统计 Redis hash key
    private static final String ROOM_PREFIX = "LIVE_ROOM_STATISTIC:";

    //房间统计 Redis ROOM_PREFIX  点赞
    private static final String ROOM_LIKE = "like";

    //房间统计 Redis ROOM_PREFIX  人数
    private static final String LIVE_VIEW = "liveView";

    //房间观看用户 Redis key
    private static final String REDIS_LIVEING_USER = "LIVING_ROOM_VIEWER:";

    //房间统计通知 Redis key
    private static final String LIVE_ROOM_NOTIFY_REDIS = "LIVE_ROOM_NOTIFY:";

    private static final ScheduledExecutorService NOTIFY_EXECUTOR = Executors.newScheduledThreadPool(1);

    private static Logger LOGGER = LoggerFactory.getLogger(LiveStatistic.class);

    //缓存上线
    private static int CAHCHE_USER_NUMBER = 200;

    //用户分页煤每页用户数
    private static int LIVE_USERS_PAGE_LIMIT = 30;

    private Vertx vertx;
    private RedisClient redis = null;
    private IMClient imClient;

    public static LiveStatistic create(Vertx vertx) {
        LiveStatistic liveStatistic = new LiveStatistic();
        liveStatistic.redis = RedisClientFactory.create(vertx);
        liveStatistic.imClient = IMClient.create(vertx);
        liveStatistic.vertx = vertx;
        return liveStatistic;

    }

    private static String getLikeCMDMsg(int number) {
        Map<String, Integer> map = new HashMap<>(2);
        map.put("likeCount", number);
        return JsonHelper.toJson(map);
    }

    private static String getRoomOnlineCMDMsg(int number) {
        Map<String, Integer> map = new HashMap<>(2);
        map.put("userCount", number);
        return JsonHelper.toJson(map);
    }

    /**
     * 更具直播房间ID统计房间用户及点赞信息
     *
     * @param roomId
     * @return
     */
    public LiveStatistic statistic(String roomId, Handler<AsyncResult<Statistic>> handler) {
        String roomKey = ROOM_PREFIX.concat(roomId);

        redis.hgetall(roomKey, asyH -> {
            JsonObject jsonObject = asyH.result();
            int like = Integer.parseInt(jsonObject.getString(ROOM_LIKE, "0"));
            int online = Integer.parseInt(jsonObject.getString(LIVE_VIEW, "0"));
            Future<Statistic> statisticFuture = Future.future();
            statisticFuture.complete(new Statistic().setLike(like).setLiveView(online));
            handler.handle(statisticFuture);

        });

        return this;
    }

    /**
     * 结束直播
     *
     * @param roomId
     * @param startTime
     */
    public LiveStatistic overLive(String roomId, long startTime, Handler<AsyncResult<Void>> handler) {

        //过期用户点赞以及在线人数
        redis.expire(ROOM_PREFIX.concat(roomId), 1 * 60, null);


        //过期在线用户信息
        redis.expire(REDIS_LIVEING_USER.concat(roomId), 1 * 60, null);

        //通知直播结束
        notifyLiveOver(roomId, startTime);

        return this;
    }

    /**
     * 通知直播结束
     *
     * @param roomId
     * @param startTime
     */
    private void notifyLiveOver(String roomId, long startTime) {
        vertx.executeBlocking((Future<String> future) -> {
            String playBackUrl = LiveStreamManager.getLiveBroadCastStream().getPlayBackUrl(roomId, startTime);
            future.complete(playBackUrl);
        }, asy -> {
            //通知客户端直播结束
            DateTime streamTime = new DateTime(DateTime.now().getMillis() - startTime,
                    DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+:08:00")));
            statistic(roomId, asyH -> {
                if (asyH.succeeded()) {
                    int hour = streamTime.getHourOfDay();
                    int minute = streamTime.getMinuteOfHour();
                    String time = minute + " 分";
                    if (hour > 0) {
                        time = hour + " 小时 " + time;
                    }
                    String playBackUrl = asy.result();
                    Statistic statistic = asyH.result();
                    statistic.setStreamingTime(time);
                    statistic.setPlayBackUrl(playBackUrl);
                    User user = new User(roomId, "daydaycook", "");
                    imClient.publishMessage(user, roomId, MessageType.LIVE_OVER, JsonHelper.toJson(statistic), null);
                }
            });
        });

    }

    /***
     * 记录点赞
     *
     * @param roomId
     * @param number
     * @param handler
     * @return
     */
    public LiveStatistic like(String roomId, int number, Handler<AsyncResult<String>> handler) {
        String roomKey = ROOM_PREFIX.concat(roomId);
        redis.hincrby(roomKey, ROOM_LIKE, number, asyncResult -> {
            if (asyncResult.succeeded()) {
                Future<String> future = Future.succeededFuture();
                handler.handle(future);
                notifyLikeNumber(roomId);
            } else {
                Future<String> future = Future.failedFuture(asyncResult.cause().getMessage());
                handler.handle(future);
            }
        });
        return this;
    }

    /**
     * 通知点赞数量.接到通知后3秒后执行
     *
     * @param roomId
     */
    private void notifyLikeNumber(String roomId) {
        String roomKey = LIVE_ROOM_NOTIFY_REDIS.concat(roomId);

        redis.hsetnx(roomKey, ROOM_LIKE, "true", asyResult -> {
            if (asyResult.succeeded()) {
                long result = asyResult.result();
                if (result == 1L) {
                    NOTIFY_EXECUTOR.schedule(() -> {
                        redis.hdel(roomKey, ROOM_LIKE, null);
                        statistic(roomId, asyncResult -> {
                            if (asyncResult.succeeded()) {
                                int like = asyncResult.result().getLike();
                                String notifyMsg = getLikeCMDMsg(like);
                                LOGGER.info("Notify room {} like number:{}", roomId, notifyMsg);
                                User user = new User();
                                user.setUserId(roomId);
                                imClient.publishMessage(user, roomId, MessageType.ROOM_LIEK, notifyMsg, null);
                                redis.hdel(roomKey, ROOM_LIKE, null);
                            }
                        });


                    }, 3, TimeUnit.SECONDS);
                }
            }
        });


    }

    /**
     * 离开房间 统计在线人数
     *
     * @param roomId
     * @param handler
     * @return
     */
    public LiveStatistic leaveRoom(User user, String roomId, Handler<AsyncResult<String>> handler) {

        //移除当前观看用户
        String roomUsersKey = REDIS_LIVEING_USER.concat(roomId);
        String jsonUser = JsonHelper.toJson(user);
        redis.lrem(roomUsersKey, 1, jsonUser, async -> {
            if (!async.succeeded()) {
                LOGGER.error("Remove Liveing user {} fail:{}", jsonUser, async.cause().getMessage());
                Future<String> failFuture = Future.failedFuture(async.cause().getMessage());
                handler.handle(failFuture);
            } else {
                handler.handle(Future.succeededFuture());
            }
        });
        //用户数量减一
        //产品要求用户数量不减,So 不用通知
        //    notityRoomOnline(roomId);
        return this;
    }

    /**
     * 用户加入房间 统计在线人数
     *
     * @param roomId
     * @param handler
     * @return
     */
    public LiveStatistic incrAudience(User user, String roomId, Handler<AsyncResult<Void>> handler) {
        String roomUsersKey = REDIS_LIVEING_USER.concat(roomId);
        String roomKey = ROOM_PREFIX.concat(roomId);

        //用户数+1
        redis.hincrby(roomKey, LIVE_VIEW, 1, asyhandler -> {
            if (!asyhandler.succeeded()) {
                LOGGER.error("Live viewer plus 1 fail:{}", asyhandler.cause().getMessage());
                handler.handle(Future.failedFuture(asyhandler.cause()));
            } else {
                handler.handle(Future.succeededFuture());
            }
        });

        //非访客压入观看者
        if (!user.isVisitor()) {
            //将当前数据从左侧压入...
            String jsonUser = JsonHelper.toJson(user);
            redis.lpush(roomUsersKey, jsonUser, asyResult -> {
                if (!asyResult.succeeded()) {
                    LOGGER.error(asyResult.cause().getMessage());
                }
            });

            //弹出右侧数据
            redis.llen(roomUsersKey, asyncLen -> {
                Long result = asyncLen.result();
                if (result != null) {
                    if (result.longValue() > CAHCHE_USER_NUMBER) {
                        for (int i = 0; i < (result - CAHCHE_USER_NUMBER); i++) {
                            redis.rpop(roomUsersKey, null);
                        }
                    }
                }
            });
        }

        //通知新用户加入
        notityRoomOnline(roomId);

        return this;
    }


    /**
     * 通知在线用户数
     *
     * @param roomId
     */
    private void notityRoomOnline(String roomId) {
        String roomKey = LIVE_ROOM_NOTIFY_REDIS.concat(roomId);
        redis.hsetnx(roomKey, LIVE_VIEW, "true", asy -> {
            if (asy.succeeded()) {
                long lr = asy.result();
                if (lr == 1L) {
                    NOTIFY_EXECUTOR.schedule(() -> {
                        redis.hdel(roomKey, LIVE_VIEW, null);

                        statistic(roomId, asyncResult -> {
                            if (asyncResult.succeeded()) {
                                int online = asyncResult.result().getLiveView();
                                String notifyMsg = getRoomOnlineCMDMsg(online);
                                LOGGER.info("Notify room {} online viewer:{}", roomId, notifyMsg);
                                User user = new User();
                                user.setUserId(roomId);
                                imClient.publishMessage(user, roomId, MessageType.LIVE_VIEW, notifyMsg, null);
                            } else {
                                LOGGER.error(asyncResult.cause().getMessage());
                            }
                        });

                    }, 3, TimeUnit.SECONDS);

                }
            }
        });

    }

    /**
     * 获取正在观看的用户信息
     *
     * @param roomId  房间编号
     * @param page    第几页
     * @param handler
     * @return
     */
    public LiveStatistic getLiveViewUsers(String roomId, int page, Handler<AsyncResult<List<User>>> handler) {
        String roomUsersKey = REDIS_LIVEING_USER.concat(roomId);

        int offset = (page - 1) * LIVE_USERS_PAGE_LIMIT;
        int limit = offset + LIVE_USERS_PAGE_LIMIT - 1;

        redis.lrange(roomUsersKey, offset, limit, async -> {
            if (async.succeeded()) {
                JsonArray jsonArray = async.result();
                List<User> liveingUsers = new ArrayList<>(LIVE_USERS_PAGE_LIMIT);
                if (jsonArray != null && jsonArray.size() > 0) {
                    jsonArray.forEach(jsonObject -> {
                        User cacheUser = JsonHelper.parese(jsonObject.toString(), User.class);
                        liveingUsers.add(cacheUser);
                    });
                }
                Future<List<User>> futer = Future.succeededFuture(liveingUsers);
                handler.handle(futer);

            } else {
                LOGGER.error("LiveStatistic incrAudience error:{}", async.cause().getMessage());
                Future failFuture = Future.failedFuture(async.cause().getMessage());
                handler.handle(failFuture);
            }
        });

        return this;
    }

}
