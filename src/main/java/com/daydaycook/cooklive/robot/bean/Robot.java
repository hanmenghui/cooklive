package com.daydaycook.cooklive.robot.bean;


import java.io.Serializable;

/**
 * Created by creekhan on 7/8/16.
 */
public class Robot implements Serializable {

    private static final long serialVersionUID = 3437705062372428753L;
    private String id;

    private String icon;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
