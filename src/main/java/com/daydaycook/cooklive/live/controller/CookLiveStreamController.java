package com.daydaycook.cooklive.live.controller;

import com.daydaycook.cooklive.CookLiveResponse;
import com.daydaycook.cooklive.im.IMServerManager;
import com.daydaycook.cooklive.live.LiveStreamManager;
import com.daydaycook.cooklive.live.livestream.LiveBroadCastStream;
import com.daydaycook.cooklive.live.livestream.StreamPublishResponse;
import com.daydaycook.cooklive.live.livestream.StreamSource;
import com.daydaycook.cooklive.live.livestream.problem.LiveStreamCreateFailException;
import com.daydaycook.cooklive.live.livestream.problem.LiveStreamNotFoundException;
import com.daydaycook.cooklive.utils.JsonHelper;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 已废弃.请参考@class CookLiveHttpServer
 * Created by creekhan on 7/6/16.
 */
@RestController(value = "liveStreamController")
@RequestMapping("live/stream")
public class CookLiveStreamController {

    private static Logger LOGGER = LoggerFactory.getLogger(CookLiveStreamController.class);

    @Autowired
    private ApplicationContext applicationContext;

    ///@Autowired
    // private RobotService robotService;


    @RequestMapping("/create")
    @ResponseBody
    public String createStream(String steamTitle) {

        StreamPublishResponse streamPublishResponse = null;
        try {
            streamPublishResponse = LiveStreamManager.getLiveBroadCastStream().createPublishStream(steamTitle);
        } catch (LiveStreamCreateFailException e) {
            LOGGER.error("  create stream failure.");
            return CookLiveResponse.STREAM_CREATE_FAILE.toJson();
        }


        //创建IM房间
        IMServerManager.getIMServer().create(streamPublishResponse.getStreamId(), streamPublishResponse.getTitle());

        //启动机器人服务
        //  robotService.applayRobootTask(streamPublishResponse.getStreamId());

        String jsonStream = CookLiveResponse.success(streamPublishResponse).toJson();
        LOGGER.info(" create stream success:{}", jsonStream);
        return jsonStream;
    }


    @RequestMapping("{streamId}/get")
    @ResponseBody
    public String getStream(@PathVariable String streamId) {

        String streamJson = null;
        try {
            streamJson = LiveStreamManager.getLiveBroadCastStream().getStream(streamId);

        } catch (LiveStreamNotFoundException e) {
            LOGGER.error(" getStream:{}  failure.", streamId);
            return CookLiveResponse.STREAM_GET_FAILE.toJson();
        }
        JsonObject jsonObject = JsonHelper.parse(streamJson);
        String jsonLive = CookLiveResponse.success(jsonObject).toJson();
        LOGGER.info("get stream success:{}", jsonLive);
        return jsonLive;

    }


    @RequestMapping("{streamSource}/{streamId}/delete")
    public String deleteStream(@PathVariable StreamSource streamSource, @PathVariable String streamId) {
        if (streamSource == null || StringUtils.isEmpty(streamId)) {
            return CookLiveResponse.REQUEST_URL_ILLEGAL.toJson();
        }
        LOGGER.info("*receive delete stream:{} request", streamId);

        Class<?> clas = streamSource.getSourceClass();
        LiveBroadCastStream liveBroadCastStream = (LiveBroadCastStream) applicationContext.getBean(clas);

        try {
            liveBroadCastStream.deleteStream(streamId);
        } catch (LiveStreamNotFoundException e) {
            LOGGER.error("Class {}  getStream:{}  failure.", clas.getName(), streamId);
            return CookLiveResponse.STREAM_GET_FAILE.toJson();
        }

        return CookLiveResponse.success("删除流:" + streamId + "成功").toJson();
    }


}
