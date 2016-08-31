package com.daydaycook.cooklive.im.imserver;

import com.daydaycook.cooklive.im.MessageType;
import com.daydaycook.cooklive.im.imserver.problem.IMException;

/**
 * 即时通讯 接口
 * Created by creekhan on 7/13/16.
 */
public interface IMServer {

    /**
     * 获取token
     *
     * @param userId      用户ID
     * @param nickName    昵称
     * @param portraitUri 图像
     * @return
     * @throws IMException
     */
    String relateUserWithToken(String userId, String nickName, String portraitUri)
            throws IMException;

    /**
     * 创建房间
     *
     * @param roomId   房间id
     * @param roomName 房间名称
     * @return
     * @throws IMException
     */
    String create(String roomId, String roomName) throws IMException;

    /***
     * 用户加入房间
     *
     * @param userId 用户id
     * @param roomId 房间id
     * @throws IMException
     */
    void join(String userId, String roomId) throws IMException;

    /***
     * 销毁房间
     *
     * @param roomId 房间id
     * @throws IMException
     */
    void destroy(String roomId) throws IMException;

    /**
     * 发送消息
     *
     * @param fromUserId   消息发送者id
     * @param toChatroomId 房间id
     * @param messageType  消息类型
     * @param context      内容
     */
    void publishMessage(String fromUserId, String toChatroomId, MessageType messageType, String context);


}
