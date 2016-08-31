package com.daydaycook.cooklive;

import com.daydaycook.cooklive.im.IMServerManager;
import com.daydaycook.cooklive.live.LiveStreamManager;
import com.daydaycook.cooklive.live.livestream.LiveBroadCastStream;
import com.daydaycook.cooklive.live.livestream.StreamLiveResponse;
import com.daydaycook.cooklive.live.livestream.StreamPublishResponse;
import com.daydaycook.cooklive.live.livestream.problem.LiveStreamCreateFailException;
import com.daydaycook.cooklive.redis.RedisClientFactory;
import com.daydaycook.cooklive.robot.RobotService;
import com.daydaycook.cooklive.statistic.LiveStatistic;
import com.daydaycook.cooklive.statistic.Statistic;
import com.daydaycook.cooklive.utils.JsonHelper;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 直播管理入口,提供创建直播,结束直播,直播间统计入口. 约定为roomId==streamId
 * Created by creekhan on 7/21/16.
 */
public class CookLiveManager extends AbstractVerticle {

    private static final String LIVE_START_TIME_REDIS = "LIVE_START_TIME";

    private static Logger LOGGER = LoggerFactory.getLogger(CookLiveManager.class);

    private static CookLiveManager cookLiveManager = null;

    private LiveStatistic liveStatistic;

    private RedisClient redis;

    public static CookLiveManager instance() {
        return cookLiveManager;
    }

    @Override
    public void init(Vertx vertx, Context context) {
        LOGGER.info(Thread.currentThread().toString());
        super.init(vertx, context);
        liveStatistic = LiveStatistic.create(vertx);
        redis = RedisClientFactory.create(vertx);
        cookLiveManager = this;
    }

    /**
     * 创建直播流同时创建房间信息
     *
     * @param streamId 流ID 确保唯一性
     * @return 直播流信息
     * @throws LiveStreamCreateFailException 流创建失败
     */
    public static StreamResponse createStream(String streamId) throws LiveStreamCreateFailException {
        LOGGER.info("Create live stream :{}", streamId);

        LiveBroadCastStream liveBroadCastStream = LiveStreamManager.getLiveBroadCastStream();
        StreamPublishResponse streamPublishResponse = liveBroadCastStream.createPublishStream(streamId);
        StreamLiveResponse streamLiveResponse = liveBroadCastStream.getPullStream(streamPublishResponse.getStreamId());
        LOGGER.info("Live {} create success:{}", streamId, JsonHelper.toJson(streamPublishResponse));

        //创建房间
        IMServerManager.getIMServer().create(streamPublishResponse.getStreamId(), streamPublishResponse.getTitle());

        StreamResponse streamResponse = new StreamResponse();
        streamResponse.setStreamPublishResponse(streamPublishResponse);
        streamResponse.setStreamLiveResponse(streamLiveResponse);

        return streamResponse;
    }


    /**
     * 开始直播,申请机器人服务
     *
     * @param streamId
     */
    public void startLive(String streamId) {
        JsonObject jsonObject = new JsonObject().put("roomId", streamId);
        if (LiveServerConfig.robotService()) {
            vertx.eventBus().send(RobotService.ROBOT_TASK_APPLY, jsonObject, asyHandler -> {
                if (!asyHandler.succeeded()) {
                    LOGGER.info("Room {} apply robot service fail:{}", streamId, asyHandler.cause().getMessage());
                } else {
                    LOGGER.info("Room {} apply robot service success.", streamId);
                }
            });
        }

        //记录直播开始时间
        redis.hset(LIVE_START_TIME_REDIS, streamId, String.valueOf(System.currentTimeMillis()), null);

    }


    /**
     * 获取直播开始时间
     *
     * @param streamId 流id
     * @param handler
     * @return
     */
    public CookLiveManager getLiveStartTime(String streamId, Handler<AsyncResult<Long>> handler) {
        redis.hget(LIVE_START_TIME_REDIS, streamId, asyHandler -> {
            if (asyHandler.succeeded()) {
                String startTimestr = asyHandler.result();
                Future<Long> future = Future.succeededFuture(Long.valueOf(startTimestr));
                handler.handle(future);
            } else {
                handler.handle(Future.failedFuture(asyHandler.cause()));
            }
        });

        return this;
    }


    /**
     * 获取房间观看信息
     *
     * @param streamId 流ID
     * @return
     */
    public Future<Statistic> statistic(String streamId) {
        Future<Statistic> statisticFuture = Future.future();

        liveStatistic.statistic(streamId, asyHandler -> {
            if (asyHandler.succeeded()) {
                statisticFuture.complete(asyHandler.result());
            } else {
                LOGGER.error("LiveStatistic.statistic error:{}", asyHandler.cause().getMessage());
            }
        });

        return statisticFuture;
    }


    /**
     * 直播结束
     *
     * @param streamId
     * @param startTime
     * @return
     */
    public Future<Statistic> overLive(String streamId, long startTime) {
        LOGGER.info(" Over live broadcast,srteamId{},startTime:{}", streamId, startTime);
        Future<Statistic> statisticFuture = Future.future();

        liveStatistic.statistic(streamId, asyHandler -> {
            Statistic statistic = asyHandler.result();
            vertx.executeBlocking((Future<String> future) -> {
                String playBackUrl = LiveStreamManager.getLiveBroadCastStream().getPlayBackUrl(streamId, startTime);
                System.out.println(playBackUrl);
                future.complete(playBackUrl);
            }, asy -> {
                JsonObject jsonObject = new JsonObject().put("roomId", streamId);
                if (LiveServerConfig.robotService()) {
                    vertx.eventBus().send(RobotService.ROBOT_OVER_SERVICE, jsonObject,
                            (AsyncResult<Message<Integer>> robotHandler) -> {
                                if (robotHandler.succeeded()) {
                                    Message<Integer> message = robotHandler.result();
                                    int robotNumbers = message.body();
                                    statistic.setRobotNumber(robotNumbers);
                                } else {
                                    LOGGER.error("OverLive send robot server over fail:{}", robotHandler.cause().getMessage());
                                }

                            });
                }
                String playBackUrl = asy.result();
                statistic.setPlayBackUrl(playBackUrl);
                statisticFuture.complete(statistic);

            });

        });


        liveStatistic.overLive(streamId, startTime, null);

        //移除直播开始时间缓存
        redis.hdel(LIVE_START_TIME_REDIS, streamId, null);

        return statisticFuture;

    }


    public static class StreamResponse {

        private StreamPublishResponse streamPublishResponse;

        private StreamLiveResponse streamLiveResponse;

        public StreamPublishResponse getStreamPublishResponse() {
            return streamPublishResponse;
        }

        public void setStreamPublishResponse(StreamPublishResponse streamPublishResponse) {
            this.streamPublishResponse = streamPublishResponse;
        }

        public StreamLiveResponse getStreamLiveResponse() {
            return streamLiveResponse;
        }

        public void setStreamLiveResponse(StreamLiveResponse streamLiveResponse) {
            this.streamLiveResponse = streamLiveResponse;
        }
    }

}
