package com.daydaycook.cooklive;

import com.daydaycook.cooklive.utils.JsonHelper;

/**
 * 直播服务请求返回管理
 * Created by creekhan on 7/7/16.
 */
public class CookLiveResponse {


    protected static final String SUCCESS_CODE = "200";
    public static CookLiveResponse ILLEGAL_REQUEST_PARAMETER = new CookLiveResponse("0", "请求参数无效");
    public static CookLiveResponse REQUEST_URL_ILLEGAL = new CookLiveResponse("201", "请求地址无效");
    public static CookLiveResponse STREAM_CREATE_FAILE = new CookLiveResponse("202", "直播流创建失败");
    public static CookLiveResponse STREAM_GET_FAILE = new CookLiveResponse("203", "直播流获取失败");
    public static CookLiveResponse GET_USER_TOKEN_FAIL = new CookLiveResponse("204", "获取用户token失败");
    public static CookLiveResponse USER_JOIN_ROOM_FAIL = new CookLiveResponse("205", "加入聊天室失败");
    public static CookLiveResponse GET_USER_INFO_FAIL = new CookLiveResponse("206", "获取用户信息失败");
    public static CookLiveResponse LIKE_FAILE = new CookLiveResponse("207", "点赞失败");
    public static CookLiveResponse STATISTIC_FAIL = new CookLiveResponse("208", "统计失败");
    public static CookLiveResponse GET_LIVE_VIEW_USERS_FAIL = new CookLiveResponse("209", "获取观看用户数据失败");
    public static CookLiveResponse GET_PLAYBACK_MSG_ERROR = new CookLiveResponse("210", "获取回播聊天消息失败");
    private String code;
    private String msg;
    private Object data;

    private CookLiveResponse(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private CookLiveResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static CookLiveResponse success(Object jsonStream) {
        if (jsonStream == null) {
            jsonStream = "success";
        }
        return new CookLiveResponse(SUCCESS_CODE, "success", jsonStream);
    }

    public static CookLiveResponse success() {

        return new CookLiveResponse(SUCCESS_CODE, "success", null);
    }

    public String toJson() {
        return JsonHelper.toJson(this);
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}
