package com.daydaycook.cooklive;

import com.daydaycook.cooklive.robot.RobotService;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务初始化
 * Created by creekhan on 7/14/16.
 */
@Component
public class CookLiveInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static Vertx vertx = Vertx.vertx();
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static Logger LOGGER = LoggerFactory.getLogger(CookLiveInitializer.class);
    VertxOptions vertxOptions = new VertxOptions();

    /**
     * 根据class 全称发布Vert.x服务
     *
     * @param clas
     */
    public static void deployVerticle(Class clas) {
        executorService.execute(() -> {
            vertx.deployVerticle(clas.getName(), asy -> {
                if (asy.succeeded()) {
                    LOGGER.info("Deploy verticle {}  finish.", clas.getName());
                } else {
                    System.out.println(asy.cause());
                    LOGGER.error("Deploy verticle {}  ERROR:{}.", clas.getName(), asy.cause().getMessage());
                }
            });

        });
    }


    /**
     * 根据实例发布vert.x 服务
     *
     * @param verticle
     */
    public static void deployVerticle(Verticle verticle) {
        executorService.execute(() -> {
            vertx.deployVerticle(verticle, asy -> {
                if (asy.succeeded()) {
                    LOGGER.info("Deploy verticle {}  finish.", verticle.getClass().getName());
                } else {
                    LOGGER.error("Deploy verticle {}  ERROR:{}.", verticle.getClass().getName(), asy.cause().getMessage());
                }
            });
        });
    }


    /**
     * 服务启动成功后初始化
     *
     * @param event
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {

        LOGGER.info("Start deploy CookLiveManager verticle.");
        deployVerticle(CookLiveManager.class);

        LOGGER.info("Start deploy CookLiveHttpServer verticle.");
        deployVerticle(CookLiveHttpServer.class);

        LOGGER.info("start or not  RobotService :{}", LiveServerConfig.robotService());
        if (LiveServerConfig.robotService()) {
            LOGGER.info("Start deploy RobotService verticle.");
            deployVerticle(RobotService.class);
        }
    }


}