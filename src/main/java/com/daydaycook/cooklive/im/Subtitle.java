package com.daydaycook.cooklive.im;

/**
 * 直播聊天回放实体
 * Created by creekhan on 8/15/16.
 */
public class Subtitle {

    //消息发送者
    private User user;

    //消息内容
    private String message;

    //时间差
    private long time;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
