package com.daydaycook.cooklive.im.controller;

import com.daydaycook.cooklive.CookLiveResponse;
import com.daydaycook.cooklive.im.IMServerManager;
import com.daydaycook.cooklive.im.imserver.problem.IMException;
import org.springframework.web.bind.annotation.*;

/**
 * 即时通讯对外 请求地址 已废弃.参考 @class CookLiveHttpServer
 * Created by creekhan on 7/13/16.
 */
@RestController
@RequestMapping("live/im")
public class CookIMController {


    @RequestMapping("user/token")
    @ResponseBody
    public String createOpenId(@RequestParam long uid, @RequestParam String nickName, String portraitUri) {

        try {
            String token = IMServerManager.getIMServer().relateUserWithToken(String.valueOf(uid), nickName, portraitUri);
            return CookLiveResponse.success(token).toJson();
        } catch (IMException e) {
            return CookLiveResponse.GET_USER_TOKEN_FAIL.toJson();
        }

    }

    @ResponseBody
    @RequestMapping("{uid}/join/{roomId}/in")
    public String joinRoom(@PathVariable String uid, @PathVariable String roomId) {

        try {
            IMServerManager.getIMServer().join(uid, roomId);
            return CookLiveResponse.success(null).toJson();
        } catch (IMException e) {
            e.printStackTrace();
            return CookLiveResponse.USER_JOIN_ROOM_FAIL.toJson();
        }
    }
}
