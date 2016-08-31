package com.daydaycook.cooklive.im.imserver.rongyun;

import com.daydaycook.cooklive.im.IMServerManager;
import com.daydaycook.cooklive.im.MessageType;
import com.daydaycook.cooklive.im.imserver.AbstractIMServer;
import com.daydaycook.cooklive.im.imserver.IMServer;
import com.daydaycook.cooklive.im.imserver.problem.IMException;
import com.daydaycook.cooklive.utils.HttpUtil;
import com.daydaycook.cooklive.utils.JsonHelper;
import com.daydaycook.cooklive.utils.URLEncoderHelper;
import com.daydaycook.cooklive.utils.URLEncoderHelper.EncodeType;
import com.google.gson.JsonObject;
import io.rong.models.FormatType;
import io.rong.models.SdkHttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * 融云即时通讯实现
 * Created by creekhan on 7/13/16.
 */
public class RongYunImServer extends AbstractIMServer implements IMServer {

    private static final String url = RongYunConfig.apiUrl();
    private static final String appKey = RongYunConfig.appKey();
    private static final String appSecret = RongYunConfig.appSecret();
    private static final IMException ioException = new IMException("请求操作失败,请稍后再试!");
    private static Logger LOGGER = LoggerFactory.getLogger(RongYunImServer.class);


    public RongYunImServer() {
        IMServerManager.registerIMServer(this);
    }


    /**
     * @param roomId
     * @param roomName
     * @return
     * @throws IMException
     */
    @Override
    public String create(String roomId, String roomName) throws IMException {

        LOGGER.info("Create room roomId:{} ,roomName:{} ", roomId, roomName);

        String RONGCLOUDURI = url + "/chatroom/create." + FormatType.json.toString();

        String parameter = "chatroom[".concat(URLEncoderHelper.encode(roomId, EncodeType.UTF8)).concat("]=").concat(
                URLEncoderHelper.encode(roomName, EncodeType.UTF8));

        try {
            HttpURLConnection conn = HttpUtil.createPostHttpConnection(appKey, appSecret, RONGCLOUDURI);
            HttpUtil.setBodyParameter(parameter, conn);
            SdkHttpResult sdkHttpResult = HttpUtil.returnResult(conn);
            if (sdkHttpResult.getHttpCode() == 200) {
                JsonObject jsonObject = JsonHelper.parse(sdkHttpResult.getResult());
                if (jsonObject.get("code").toString().equals("200")) {

                    LOGGER.info("Create room roomId:{} SUCCESS ", roomId);

                    return roomId;
                }
            }
            LOGGER.error("create room fail:{}", JsonHelper.toJson(sdkHttpResult));
            throw ioException;
        } catch (IOException e) {
            LOGGER.error("create room fail:{}", e.getMessage());
            e.printStackTrace();
            throw ioException;
        }

    }

    /**
     * @param userId
     * @param roomId
     * @throws IMException
     */
    @Override
    public void join(String userId, String roomId) throws IMException {
        String RONGCLOUDURI = url + "/chatroom/join." + FormatType.json.toString();

        StringBuilder sb = new StringBuilder(32);
        sb.append("userId=").append(URLEncoderHelper.encode(userId, EncodeType.UTF8));
        sb.append("&chatroomId=").append(URLEncoderHelper.encode(roomId, EncodeType.UTF8));

        try {
            HttpURLConnection conn = HttpUtil.createPostHttpConnection(appKey, appSecret, RONGCLOUDURI);
            HttpUtil.setBodyParameter(sb, conn);
            SdkHttpResult sdkHttpResult = HttpUtil.returnResult(conn);

            if (sdkHttpResult.getHttpCode() == 200) {
                JsonObject jsonObject = JsonHelper.parse(sdkHttpResult.getResult());
                if (jsonObject.get("code").toString().equals("200")) {
                    return;
                }
            }
            LOGGER.error("join room fail:{}", JsonHelper.toJson(sdkHttpResult));
            throw ioException;
        } catch (IOException e) {
            LOGGER.error("join room fail:{}", e.getMessage());
            throw ioException;
        }

    }

    @Override
    public void destroy(String roomId) throws IMException {
        String RONGCLOUDURI = url + "/chatroom/destroy." + FormatType.json.toString();
        StringBuilder stringBuilder = new StringBuilder(16);
        stringBuilder.append("chatroomId=").append(roomId);

        JsonObject jsonObject = postRequest(RONGCLOUDURI, stringBuilder.toString());
        if (!jsonObject.get("code").toString().equals("200")) {
            throw new IMException("destroy room " + roomId + " fail:" + jsonObject.get("code").toString());
        }

    }

    @Override
    public void publishMessage(String fromUserId, String toChatroomId, MessageType messageType, String context) {
        String RONGCLOUDURI = url + "/message/chatroom/publish." + FormatType.json.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("fromUserId=").append(URLEncoderHelper.encode(fromUserId, EncodeType.UTF8));
        sb.append("&toChatroomId=").append(URLEncoderHelper.encode(toChatroomId, EncodeType.UTF8));
        sb.append("&objectName=").append(URLEncoderHelper.encode(this.getMessageType(messageType), EncodeType.UTF8));
        sb.append("&content=").append(URLEncoderHelper.encode(context, EncodeType.UTF8));

        JsonObject jsonObject = postRequest(RONGCLOUDURI, sb.toString());
        LOGGER.info("Send {} im notfiy message:{} to " + toChatroomId, messageType.name(), context);
        if (!jsonObject.get("code").toString().equals("200")) {
            throw new IMException("消息发送失败:" + jsonObject.get("code").toString());
        }

    }

    /**
     * 获取用户tocken
     *
     * @param userId      用户id
     * @param nickName    昵称
     * @param portraitUri 图像
     * @return
     * @throws IMException
     */
    @Override
    public String relateUserWithToken(String userId, String nickName, String portraitUri) throws IMException {
        String RONGCLOUDURI = url + "/user/getToken." + FormatType.json.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("userId=").append(URLEncoderHelper.encode(userId, EncodeType.UTF8));
        sb.append("&name=").append(URLEncoderHelper.encode(nickName == null ? "" : nickName, EncodeType.UTF8));
        sb.append("&portraitUri=").append(URLEncoderHelper.encode(portraitUri == null ? "" : portraitUri, EncodeType.UTF8));

        JsonObject jsonObject = postRequest(RONGCLOUDURI, sb.toString());
        if (jsonObject.get("code").toString().equals("200")) {
            return jsonObject.get("token").getAsString();

        } else {
            throw new IMException("获取token失败:" + jsonObject.get("code").toString());
        }


    }

    private JsonObject postRequest(String url, String parm) throws IMException {

        try {
            HttpURLConnection conn = HttpUtil.createPostHttpConnection(appKey, appSecret, url);
            HttpUtil.setBodyParameter(parm, conn);
            SdkHttpResult sdkHttpResult = HttpUtil.returnResult(conn);

            if (sdkHttpResult.getHttpCode() == 200) {
                JsonObject jsonObject = JsonHelper.parse(sdkHttpResult.getResult());
                return jsonObject;
            } else {

                LOGGER.error("getOpenId fail:{}", JsonHelper.toJson(sdkHttpResult));
                throw ioException;
            }
        } catch (IOException e) {
            LOGGER.error("getOpenId fail:{}", e.getMessage());
            throw ioException;
        }


    }


    /**
     * 将消息类型转换为IOS端自定义消息类型
     *
     * @param messageType
     * @return
     */
    private String getMessageType(MessageType messageType) {

        switch (messageType) {
            case TXT_MSG:
                return "RC:TxtMsg";
            case CMD_MSG:
                return "RC:CmdMsg";
            case JOIN_ROOM:
                return "DDCLiveLoginMessage";
            case ROOM_LIEK:
                return "DDCLiveZanCountMessage";
            case LIVE_VIEW:
                return "DDCLiveUserCountMessage";
            case LIVE_OVER:
                return "DDCLiveEndMessage";
            default:
                return "RC:TxtMsg";
        }
    }


}
