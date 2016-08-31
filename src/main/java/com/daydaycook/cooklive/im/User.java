package com.daydaycook.cooklive.im;

import com.daydaycook.cooklive.utils.JsonHelper;

import java.io.Serializable;

/**
 * 即时通讯 用户信息
 * Created by creekhan on 7/19/16.
 */
public class User implements Serializable {


    private String userId;


    private String name;


    private String portraitUri;

    private transient boolean visitor;

    public User() {
    }

    public User(String userId, String nickName, String portraitUri) {
        this.userId = userId;
        this.name = nickName;
        this.portraitUri = portraitUri;
    }

    public static void main(String[] args) {
        System.out.println(JsonHelper.toJson(new User()));
    }

    public boolean isVisitor() {
        return visitor;
    }

    public void setVisitor(boolean visitor) {
        this.visitor = visitor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
