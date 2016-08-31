package com.daydaycook.cooklive.im.client;

import com.daydaycook.cooklive.CookLiveManager;
import com.daydaycook.cooklive.MongoConfig;
import com.daydaycook.cooklive.im.CookIMVerticle;
import com.daydaycook.cooklive.im.MessageType;
import com.daydaycook.cooklive.im.User;
import com.daydaycook.cooklive.utils.JsonHelper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 即时通讯client
 * Created by creekhan on 7/18/16.
 */
public class IMClient {


    private static Logger LOGGER = LoggerFactory.getLogger(IMClient.class);
    MongoClient mongo = null;
    private Vertx vertx;

    public static IMClient create(Vertx vertx) {
        IMClient imClient = new IMClient();
        imClient.vertx = vertx;
        imClient.mongo = MongoClient.createShared(vertx, MongoConfig.getMongoOptions());
        return imClient;
    }

    /***
     * 获取用户token
     *
     * @param userId      用户ID
     * @param nickName    用户昵称
     * @param portraitUri 用户图像地址
     * @param handler     回到操作
     * @return
     */
    public IMClient getUserToken(String userId, String nickName, String portraitUri,
                                 Handler<AsyncResult<String>> handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("method", IMServerMethod.GET_TOKEN);
        jsonObject.put("userId", userId);
        jsonObject.put("nickName", nickName);
        jsonObject.put("portraitUri", portraitUri);

        vertx.eventBus().send(CookIMVerticle.getServerAddress(), jsonObject, (AsyncResult<Message<String>> ay) -> {
            if (ay.succeeded()) {
                Message<String> message = ay.result();
                String tocken = message.body();
                Future<String> result = Future.succeededFuture(tocken);
                handler.handle(result);
            } else {
                handler.handle(Future.failedFuture(ay.cause()));
            }
        });
        return this;
    }

    /**
     * 加入聊天室
     *
     * @param userId  用户ID
     * @param roomId  房间
     * @param handler
     * @return
     */
    public IMClient joinRoom(String userId, String roomId, Handler<AsyncResult<Void>> handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("method", IMServerMethod.JOIN_ROOM);
        jsonObject.put("userId", userId);
        jsonObject.put("roomId", roomId);

        vertx.eventBus().send(CookIMVerticle.getServerAddress(), jsonObject, ay -> {
            if (handler != null) {
                if (ay.succeeded()) {
                    handler.handle(Future.succeededFuture());
                } else {
                    handler.handle(Future.failedFuture(ay.cause()));
                }
            }
        });

        return this;
    }

    /**
     * 获取用户昵称图像信息
     *
     * @param userId
     * @param handler
     * @return
     */
    public IMClient getUserInfo(String userId, Handler<AsyncResult<User>> handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("method", IMServerMethod.GET_USERINFO);
        jsonObject.put("userId", userId);

        vertx.eventBus().send(CookIMVerticle.getServerAddress(), jsonObject, (AsyncResult<Message<JsonObject>> ay) -> {
            if (ay.succeeded()) {
                Message<JsonObject> message = ay.result();
                JsonObject jsonResult = message.body();
                User user = new User();
                user.setName(jsonResult.getString("nickName"));
                user.setPortraitUri(jsonResult.getString("portraitUri"));
                Future<User> future = Future.succeededFuture(user);
                handler.handle(future);
            } else {
                handler.handle(Future.failedFuture(ay.cause()));
            }
        });
        return this;
    }

    /**
     * 发送消息
     *
     * @param user        发送者
     * @param roomId      房间编号
     * @param messageType 消息类型
     * @param content     消息内容
     * @param handler     回调handler
     * @return
     */
    public IMClient publishMessage(User user, String roomId, MessageType messageType, String content,
                                   Handler<AsyncResult<User>> handler) {
        if (messageType != MessageType.TXT_MSG) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("method", IMServerMethod.CHAT);
            jsonObject.put("userId", user.getUserId());
            jsonObject.put("roomId", roomId);
            jsonObject.put("messageType", messageType);
            jsonObject.put("content", content);

            vertx.eventBus().send(CookIMVerticle.getServerAddress(), jsonObject, ay -> {
            });
        }

        //记录聊天消息
        if (messageType == MessageType.TXT_MSG) {
            CookLiveManager.instance().getLiveStartTime(roomId, asy -> {
                if (asy.succeeded()) {
                    long startTime = asy.result();
                    long timeDiff = System.currentTimeMillis() - startTime;
                    JsonObject msg = new JsonObject().put("message", content).put("user", new JsonObject(JsonHelper.toJson(user)))
                            .put("time", timeDiff);
                    mongo.save(roomId, msg, asyMongo -> {
                        if (!asyMongo.succeeded()) {
                            LOGGER.error("Mongo save message Error:{}", asyMongo.cause().getMessage());
                        }
                    });
                } else {
                    LOGGER.error("Get Room {} start time error:{}", roomId, asy.cause().getMessage());
                }

            });

        }

        //将time 段设为索引
        if (messageType == MessageType.LIVE_OVER) {
            JsonObject key = new JsonObject().put("key", new JsonObject().put("time", 1));
            key.put("name", roomId + "_time_index");
            key.put("unique", true);

            JsonArray jsonArray = new JsonArray().add(key);
            JsonObject command = new JsonObject()
                    .put("createIndexes", roomId)
                    .put("indexes", jsonArray);
            LOGGER.info("Crate mondo collection {} index.command:{}", roomId, command);
            mongo.runCommand("createIndexes", command, res -> {
                if (res.succeeded()) {
                    LOGGER.info("Create room {} mondo index success.");
                } else {
                    LOGGER.error("Create room {} mondo index fail {} ", roomId, res.cause().getMessage());
                }
            });
        }

        if (handler != null) {
            handler.handle(Future.succeededFuture());
        }

        return this;
    }

    /**
     * 直播回放聊天消息
     *
     * @param roomId   房间
     * @param timediff 时间差
     * @param second   时长
     * @param handler
     * @return
     */
    public IMClient subtitles(String roomId, long timediff, int second, Handler<AsyncResult<List<JsonObject>>> handler) {
        JsonObject query = new JsonObject();
        query.put("time", new JsonObject().put("$gt", timediff).put("$lte", timediff + (second * 1_000)));

        System.out.println(query);
        mongo.find(roomId, query, asyFind -> {
            if (asyFind.succeeded()) {
                List<JsonObject> jsonObjectList = asyFind.result();
                Future<List<JsonObject>> future = Future.succeededFuture(jsonObjectList);
                handler.handle(future);
            } else {
                LOGGER.error("IMClient subtitles error:{}", asyFind.cause().getMessage());
                Future future = Future.failedFuture(asyFind.cause());
                handler.handle(future);
            }
        });
        return this;
    }


    /***
     * 销毁房间
     *
     * @param roomId
     * @param handler
     * @return
     */
    public IMClient destroyChatRoom(String roomId, Handler<AsyncResult<Void>> handler) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("method", IMServerMethod.DESTROY);
        jsonObject.put("roomId", roomId);

        vertx.eventBus().send(CookIMVerticle.getServerAddress(), jsonObject, ay -> {
            if (handler != null) {
                if (ay.succeeded()) {
                    handler.handle(Future.succeededFuture());
                } else {
                    handler.handle(Future.failedFuture(ay.cause().getMessage()));
                }
            }
        });

        return this;
    }

}
