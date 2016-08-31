package com.daydaycook.cooklive.im;

import com.caipu.entity.User;
import com.caipu.service.UserService;
import com.daydaycook.cooklive.CookLiveApplication;
import com.daydaycook.cooklive.im.client.IMServerMethod;
import com.daydaycook.cooklive.im.imserver.IMServer;
import com.daydaycook.cooklive.im.imserver.problem.IMException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * 即时通讯 Verticle
 * Created by creekhan on 7/17/16.
 */
public class CookIMVerticle extends AbstractVerticle {


    public static String getServerAddress() {
        return IMServerManager.getIMServer().getClass().getSimpleName().concat(".verticle");
    }

    @Override
    public void start() throws Exception {
        System.out.println(getServerAddress());
        IMServer imServer = IMServerManager.getIMServer();
        vertx.eventBus().consumer(getServerAddress(), (Message<JsonObject> message) -> {

            JsonObject jsonObject = message.body();
            IMServerMethod imServerMethod = IMServerMethod.valueOf(jsonObject.getString("method"));

            switch (imServerMethod) {
                case GET_TOKEN:
                    String uId = jsonObject.getString("userId");
                    String nickName = jsonObject.getString("nickName");
                    String portraitUri = jsonObject.getString("portraitUri");
                    vertx.executeBlocking((Future<String> future) -> {
                        try {
                            String tokeen = imServer.relateUserWithToken(uId, nickName, portraitUri);
                            future.complete(tokeen);
                        } catch (IMException im) {
                            future.fail(im);
                        }
                    }, asyhanle -> {
                        if (asyhanle.succeeded()) {
                            message.reply(asyhanle.result());
                        } else {
                            message.fail(1, asyhanle.cause().getMessage());
                        }

                    });
                    break;

                case JOIN_ROOM:
                    String joinUId = jsonObject.getString("userId");
                    String roomId = jsonObject.getString("roomId");
                    vertx.executeBlocking((Future<Void> future) -> {
                        try {
                            imServer.join(joinUId, roomId);
                            future.complete();
                        } catch (IMException im) {
                            future.fail(im);
                        }
                    }, asy -> {
                        if (asy.succeeded()) {
                            message.reply(null);
                        } else {
                            message.fail(1, asy.cause().getMessage());
                        }
                    });
                    break;

                case GET_USERINFO:
                    uId = jsonObject.getString("userId");
                    UserService userService = CookLiveApplication.getBean(UserService.class);
                    User user = userService.getUserById(Long.valueOf(uId));
                    String name = user.getNickName();
                    String pic = user.getImage();
                    JsonObject response = new JsonObject();
                    response.put("nickName", name);
                    response.put("portraitUri", pic);
                    message.reply(response);
                    break;

                case CHAT:
                    uId = jsonObject.getString("userId");
                    String chatRoom = jsonObject.getString("roomId");
                    String context = jsonObject.getString("content");
                    MessageType messageType = MessageType.valueOf(jsonObject.getValue("messageType").toString());

                    vertx.executeBlocking((Future<Void> future) -> {
                        try {
                            imServer.publishMessage(uId, chatRoom, messageType, context);
                            future.complete();
                        } catch (IMException e) {
                            future.fail(e);
                        }
                    }, asy -> {
                        if (asy.succeeded()) {
                            message.reply(null);
                        } else {
                            message.fail(1, asy.cause().getMessage());
                        }
                    });
                    break;

                case DESTROY:
                    String destroyRoom = jsonObject.getString("roomId");
                    vertx.executeBlocking(future -> {
                        try {
                            imServer.destroy(destroyRoom);
                            future.complete();
                        } catch (IMException e) {
                            future.fail(e);
                        }
                    }, asy -> {
                        if (asy.succeeded()) {
                            message.reply(null);
                        } else {
                            message.fail(1, asy.cause().getMessage());
                        }
                    });
                    break;
            }
        });
    }


}
