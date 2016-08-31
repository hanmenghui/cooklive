package com.daydaycook.cooklive;

import com.daydaycook.cooklive.im.MessageType;
import com.daydaycook.cooklive.im.User;
import com.daydaycook.cooklive.im.client.IMClient;
import com.daydaycook.cooklive.statistic.LiveStatistic;
import com.daydaycook.cooklive.statistic.Statistic;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 直播对外服务监听
 * Created by creekhan on 7/14/16.
 */
public class CookLiveHttpServer extends AbstractVerticle {

    //监听host
    // private static final String HTTP_HOST = LiveServerConfig.listenHost();
    //监听端口
    private static final int HTTP_PORT = LiveServerConfig.listenPort();

    private static final String SERVER_PATH = LiveServerConfig.listenPath();

    private static Logger LOGGER = LoggerFactory.getLogger(CookLiveHttpServer.class);

    Set<String> allowHeaders = new HashSet<String>() {{
        add("x-requested-with");
        add("Access-Control-Allow-Origin");
        add("origin");
        add("Content-Type");
        add("accept");
    }};

    Set<HttpMethod> allowMethods = new HashSet<HttpMethod>() {{
        add(HttpMethod.GET);
        add(HttpMethod.POST);
        add(HttpMethod.DELETE);
        add(HttpMethod.PATCH);
    }};

    private HttpServer httpServer;

    private IMClient imClient;

    private LiveStatistic liveStatistic;


    @Override
    public void init(Vertx vertx, Context context) {
        LOGGER.info(Thread.currentThread().toString());
        super.init(vertx, context);
        imClient = IMClient.create(vertx);
        liveStatistic = LiveStatistic.create(vertx);
    }

    /***
     * @param future
     * @throws Exception
     */
    @Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);
        CorsHandler corsHandler = CorsHandler.create("*").allowedHeaders(allowHeaders).allowedMethods(allowMethods);

        router.route("/").handler(corsHandler).failureHandler(ctx -> {
            String message = ctx.failure().getMessage();
            LOGGER.error("CookLiveHttpServer process exception:{}", message);
            ctx.response().end(message);

        });

       /*
        router.get(SERVER_PATH +"/room/:roomId/statistic")
                .handler(this::handleStatistic);*/
//        router.get("/nc-check.jsp").handler(this::handleRoot);

        //点赞
        router.get(SERVER_PATH + "/room/:roomId/like/:number")
                .handler(this::handleLike);

        //加入房间
        router.get(SERVER_PATH + "/user/:userId/room/:roomId/join")
                .handler(this::handleJoinRoom);

        //离开房间
        router.get(SERVER_PATH + "/user/:userId/room/:roomId/leave")
                .handler(this::handleleaveRoom);

        //获取token
        router.get(SERVER_PATH + "/user/:userId/token")
                .handler(this::handleUserToken);

        //获取用户图像
        router.get(SERVER_PATH + "/user/portrait/:userId")
                .handler(this::handleUserPortrait);

        //获取 观看直播的用户
        router.get(SERVER_PATH + "/room/:roomId/users/page/:page")
                .handler(this::handleLiveUsers);

        //发送聊天消息
        router.get(SERVER_PATH + "/message/user/:userId/room/:roomId/chat")
                .handler(this::handleChat);

        //聊天回放
        router.get(SERVER_PATH + "/message/room/:roomId/:timediff/:second")
                .handler(this::handleSubtitles);


        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router::accept).listen(HTTP_PORT, (result) -> {
            if (result.succeeded()) {
                future.complete();
            } else {
                future.fail(result.cause().getMessage());
            }
        });
    }


    private void handleRoot(RoutingContext routingContext) {

        HttpServerResponse response = routingContext.response();
        response.end("This is a vert.x server!");

    }


    /**
     * 获取用户头像
     *
     * @param routingContext
     */
    private void handleUserToken(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*");

        String userStr = request.getParam("userId");
        String userId = "0".equals(userStr) ? UUID.randomUUID().toString() : userStr;//游客
        String nickName = StringUtils.defaultIfEmpty(request.getParam("nickName"), "daydaycook");
        String portraitUri = StringUtils.defaultIfEmpty(request.getParam("portraitUri"), "");

        imClient.getUserToken(userId, nickName, portraitUri, asyResult -> {
            if (asyResult.succeeded()) {
                Map<String, String> map = new HashMap<String, String>(2);
                map.put("userId", userId);
                map.put("token", asyResult.result());
                response.end(CookLiveResponse.success(map).toJson());
            } else {
                response.end(CookLiveResponse.GET_USER_TOKEN_FAIL.toJson());
            }
        });

    }


    /**
     * 点赞
     *
     * @param routingContext
     */
    private void handleLike(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*");

        String roomId = request.getParam("roomId");
        String numberStr = request.getParam("number");
        if (!NumberUtils.isDigits(numberStr)) {
            LOGGER.warn("Room {} get like wrong number:{}", roomId, numberStr);
            response.end(CookLiveResponse.ILLEGAL_REQUEST_PARAMETER.toJson());
            return;
        }

        int number = Integer.parseInt(numberStr);
        LOGGER.info("Room {} get like :{}", roomId, numberStr);
        if (number == 0) {
            response.end(CookLiveResponse.success().toJson());
            return;
        }

        liveStatistic.like(roomId, number, asyResult -> {
            if (asyResult.succeeded()) {
                response.end(CookLiveResponse.success().toJson());
            } else {
                response.end(CookLiveResponse.LIKE_FAILE.toJson());
            }
        });

    }


    /**
     * handle 加入房间
     *
     * @param routingContext
     */
    private void handleJoinRoom(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*");

        String userId = request.getParam("userId");
        String roomId = request.getParam("roomId");
        String nickName = StringUtils.defaultIfEmpty(request.getParam("nickName"), "daydaycook");
        String portraitUri = StringUtils.defaultIfEmpty(request.getParam("portraitUri"), "");
        String visitor = StringUtils.defaultIfEmpty(request.getParam("visitor"), "0");

        LOGGER.info("User {} (isvisitor:" + visitor + ") join room {}", userId + ":" + nickName, roomId);

        imClient.joinRoom(userId, roomId, async -> {
            if (async.succeeded()) {
                User user = new User(userId, nickName, portraitUri);
                if (visitor.equals("1")) {
                    user.setVisitor(true);
                }
                //增加观众人数 ---callback hell
                liveStatistic.incrAudience(user, roomId, asyStas -> {
                    if (asyStas.succeeded()) {
                        //获取房间人数及点赞信息
                        liveStatistic.statistic(roomId, asyHandler -> {
                            if (asyHandler.succeeded()) {
                                Statistic statistic = asyHandler.result();
                                //获取房间前30个观众
                                liveStatistic.getLiveViewUsers(roomId, 1, asyView -> {
                                    if (asyView.succeeded()) {
                                        List<User> users = asyView.result();
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("statistic", statistic);
                                        map.put("users", users);
                                        response.end(CookLiveResponse.success(map).toJson());
                                    } else {
                                        response.end(CookLiveResponse.USER_JOIN_ROOM_FAIL.toJson());
                                        LOGGER.error("User{} join room get viewers fail:{}", asyHandler.cause().getMessage());
                                    }
                                });

                            } else {
                                response.end(CookLiveResponse.USER_JOIN_ROOM_FAIL.toJson());
                                LOGGER.error("User{} join room get room statistic fail:{}", asyHandler.cause().getMessage());
                            }
                        });

                    } else {
                        response.end(CookLiveResponse.USER_JOIN_ROOM_FAIL.toJson());
                        LOGGER.error("User{} join room incrAudience {} fail:" + async.cause().getMessage(), userId, roomId);
                    }
                });
            } else {
                response.end(CookLiveResponse.USER_JOIN_ROOM_FAIL.toJson());
                LOGGER.error("User{} join room {} fail:" + async.cause().getMessage(), userId, roomId);
            }
        });

    }

    /**
     * handle 离开房间
     *
     * @param routingContext
     */
    private void handleleaveRoom(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*");

        String userId = request.getParam("userId");
        String nickName = StringUtils.defaultIfEmpty(request.getParam("nickName"), "daydaycook");
        String portraitUri = StringUtils.defaultIfEmpty(request.getParam("portraitUri"), "");
        String roomId = request.getParam("roomId");
        User user = new User(userId, nickName, portraitUri);
        liveStatistic.leaveRoom(user, roomId, asy -> {
            if (asy.succeeded()) {
                response.end(CookLiveResponse.success().toJson());
                LOGGER.info("User {} leave room {} success.", userId, roomId);
            } else {
                LOGGER.error("User {} leave room :{} fail:" + asy.cause().getMessage(), userId, roomId);
            }
        });
    }


    /**
     * handle 用户图像
     *
     * @param routingContext
     */
    private void handleUserPortrait(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*");

        String userId = request.getParam("userId");
        imClient.getUserInfo(userId, asyResult -> {
            if (asyResult.succeeded()) {
                User user = asyResult.result();
                response.end(CookLiveResponse.success(user).toJson());
            } else {
                response.end(CookLiveResponse.GET_USER_INFO_FAIL.toJson());
            }

        });
    }

    /**
     * handle 当前观看直播的用户
     *
     * @param routingContext
     */
    private void handleLiveUsers(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*");

        ///router.get(SERVER_PATH + "/room/:roomId/users/:offset/:limit")
        String roomId = request.getParam("roomId");
        String pageStr = request.getParam("page");

        if (!NumberUtils.isDigits(pageStr)) {
            LOGGER.warn("Get room {} view users wrong page:{}", roomId, pageStr);
            response.end(CookLiveResponse.ILLEGAL_REQUEST_PARAMETER.toJson());
            return;
        }

        int page = Integer.parseInt(pageStr);
        liveStatistic.getLiveViewUsers(roomId, page, asy -> {
            if (asy.succeeded()) {
                List<User> users = asy.result();
                response.end(CookLiveResponse.success(users).toJson());
            } else {
                LOGGER.error("Get room {} view users fail:", roomId, asy.cause().getMessage());
                response.end(CookLiveResponse.GET_LIVE_VIEW_USERS_FAIL.toJson());
            }
        });
    }

    /**
     * handle 聊天
     *
     * @param routingContext
     */
    private void handleChat(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*");
        String userId = request.getParam("userId");
        String roomId = request.getParam("roomId");
        String nickName = StringUtils.defaultIfEmpty(request.getParam("nickName"), "daydaycook");
        String portraitUri = StringUtils.defaultIfEmpty(request.getParam("portraitUri"), "");
        String content = request.getParam("content");
        User user = new User(userId, nickName, portraitUri);
        imClient.publishMessage(user, roomId, MessageType.TXT_MSG, content, null);
        response.end(CookLiveResponse.success().toJson());
    }

    /**
     * 直播消息回放
     *
     * @param routingContext
     */
    private void handleSubtitles(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpServerResponse response = routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "*");
        String roomId = request.getParam("roomId");
        String timediff = request.getParam("timediff");//时差
        String second = request.getParam("second"); //时长
        if (!NumberUtils.isDigits(timediff) || !NumberUtils.isDigits(second)) {
            response.end(CookLiveResponse.ILLEGAL_REQUEST_PARAMETER.toJson());
            return;
        }

        imClient.subtitles(roomId, Long.parseLong(timediff), Integer.parseInt(second), asyHandler -> {
            if (asyHandler.succeeded()) {
                List<JsonObject> jsonObjectList = asyHandler.result();
                response.end(CookLiveResponse.success(jsonObjectList).toJson());
            } else {
                response.end(CookLiveResponse.GET_PLAYBACK_MSG_ERROR.toJson());
            }
        });


    }

}
