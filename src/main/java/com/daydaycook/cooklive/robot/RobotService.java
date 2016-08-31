package com.daydaycook.cooklive.robot;

import com.caipu.service.UserService;
import com.daydaycook.cooklive.CookLiveApplication;
import com.daydaycook.cooklive.im.MessageType;
import com.daydaycook.cooklive.im.User;
import com.daydaycook.cooklive.im.client.IMClient;
import com.daydaycook.cooklive.redis.RedisClientFactory;
import com.daydaycook.cooklive.robot.bean.Robot;
import com.daydaycook.cooklive.statistic.LiveStatistic;
import com.daydaycook.cooklive.utils.JsonHelper;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 直播机器人任务
 * Created by creekhan on 7/11/16.
 */
public class RobotService extends AbstractVerticle {

    public static final String ROBOT_TASK_APPLY = "RobotService.applayRobootTask";

    public static final String ROBOT_OVER_SERVICE = "RobotService.overRobotService";

    private static final int frequency = 10;  //添加频率

    private static final int ROBOT_PEAR = 5; //单次人数

    //申请机器人服务的房间 KEY
    private static final String REDIS_ROOM_NAME_KEY = "APPLIED_ROBOT_ROOM";
    //机器人
    private static final String REDIS_ROBOOT_KEY = "ROBOTS";

    private static final int UPPER_BOUNDS = 2_388; //上线

    private static Logger LOGGER = LoggerFactory.getLogger(RobotService.class);
    //机器人个数
    private static int ROOTBOOT_NUMBERS;

    private static Map<String, Object> map = new HashMap<String, Object>(4);

    private LiveStatistic liveStatistic;

    private RedisClient redis;

    private IMClient imClient;

    /**
     * 用户加入房间消息类型
     *
     * @param robot 机器人
     * @return
     */
    private static String getUserLoginCMDMsg(Robot robot) {
        map.put("loginStateType", 0);
        map.put("user", robot);
        return JsonHelper.toJson(map);
    }

    @Override
    public void init(Vertx vertx, Context context) {
        LOGGER.info(Thread.currentThread().toString());
        super.init(vertx, context);
        redis = RedisClientFactory.create(vertx);
        liveStatistic = LiveStatistic.create(vertx);
        this.imClient = IMClient.create(vertx);

    }

    /**
     * start
     *
     * @param future
     * @throws Exception
     */
    @Override
    public void start(Future<Void> future) throws Exception {
        EventBus eb = vertx.eventBus();
        MessageConsumer consumer = eb.consumer(ROBOT_TASK_APPLY, (Message<JsonObject> message) -> {
            JsonObject jsonObject = message.body();
            String roomId = jsonObject.getString("roomId");
            applayRobootTask(roomId, handler -> {
                if (handler.succeeded()) {
                    message.reply(roomId);
                } else {
                    message.fail(0, handler.cause().getMessage());
                }
            });
        });


        eb.consumer(ROBOT_OVER_SERVICE, (Message<JsonObject> message) -> {
            JsonObject jsonObject = message.body();
            String roomId = jsonObject.getString("roomId");
            overRobotService(roomId, handler -> {
                if (handler.succeeded()) {
                    message.reply(handler.result());
                } else {
                    message.fail(0, handler.cause().getMessage());
                }
            });
        });

        loadRobots();
        future.complete();
    }

    /**
     * 结束机器服务
     *
     * @param roomId 房间id
     * @return
     */
    public void overRobotService(String roomId, Handler<AsyncResult<Integer>> handler) {

        redis.hget(REDIS_ROOM_NAME_KEY, roomId, robotHandler -> {
            if (robotHandler.succeeded()) {
                String str = robotHandler.result();
                if (StringUtils.isEmpty(str)) {
                    str = "0";
                }
                Future<Integer> integerFuture = Future.succeededFuture(Integer.valueOf(str));
                handler.handle(integerFuture);
                redis.hdel(REDIS_ROOM_NAME_KEY, roomId, robotSDelHandel -> {
                    if (robotSDelHandel.succeeded()) {
                        LOGGER.info("Remove room {} robot  service success.", roomId);
                    } else {
                        LOGGER.error("Remove room {} robot  service fail:{}", roomId, robotSDelHandel.cause().getMessage());
                    }
                });
            }
        });
    }

    /**
     * 申请机器人服务
     *
     * @param roomId 房间id
     */
    public void applayRobootTask(String roomId, Handler<AsyncResult<String>> handler) {
        LOGGER.info("Live:{} apply  robot service.", roomId);

        redis.hget(REDIS_ROOM_NAME_KEY, roomId, asy -> {
            if (asy.succeeded()) {
                if (StringUtils.isEmpty(asy.result())) {
                    redis.hset(REDIS_ROOM_NAME_KEY, roomId, String.valueOf("0"), asySet -> {
                        if (!asySet.succeeded()) {
                            LOGGER.error("Room {} apply robot service error:{}", asy.cause().getMessage());
                            if (handler != null) {
                                handler.handle(Future.failedFuture(asySet.cause()));
                            }
                        } else {
                            if (handler != null) {
                                handler.handle(Future.succeededFuture());
                            }
                        }
                    });
                } else {
                    if (handler != null) {
                        handler.handle(Future.succeededFuture());
                    }
                    LOGGER.info("Live {} exists in Robot service.", roomId);
                }
            }
        });

    }

    /**
     * 开始直播机器人任务
     */
    private void startRobootTask() {
        LOGGER.info("Task robot service start");
        vertx.setPeriodic(1_000 * frequency, id -> {

            redis.hgetall(REDIS_ROOM_NAME_KEY, asyHandler -> {
                if (asyHandler.succeeded()) {
                    JsonObject jsonObject = asyHandler.result();

                    if (jsonObject != null) {
                        jsonObject.forEach(val -> {
                            String roomId = val.getKey();
                            String robotNum = val.getValue().toString();
                            LOGGER.info("Room {} has robot {}", roomId, robotNum);

                            if (Integer.parseInt(robotNum) < UPPER_BOUNDS) {
                                Random random = new Random();

                                for (int i = 0; i < ROBOT_PEAR; i++) {
                                    int num = random.nextInt(ROOTBOOT_NUMBERS);
                                    redis.lindex(REDIS_ROBOOT_KEY, num, lresult -> {
                                        Robot robot = JsonHelper.parese(lresult.result(), Robot.class);
                                        User user = new User(robot.getId(), robot.getName(), robot.getIcon());
                                        liveStatistic.incrAudience(user, roomId, incrResult -> {
                                            String cmdMsg = getUserLoginCMDMsg(robot);
                                            imClient.publishMessage(user, roomId, MessageType.JOIN_ROOM, cmdMsg, null);

                                        });
                                    });
                                }

                                redis.hincrby(REDIS_ROOM_NAME_KEY, roomId.toString(), ROBOT_PEAR, null);
                            } else {
                                LOGGER.info("Room {} robot number had upper to bounds.", roomId);
                            }

                        });
                    } else {
                        LOGGER.info("There's no Live Broadcast.");
                    }

                } else {
                    LOGGER.error("error:" + asyHandler.cause().getMessage());
                }
            });
        });

    }

    /**
     * 加载机器人
     */
    private void loadRobots() {
        vertx.executeBlocking(future -> {
            UserService userService = CookLiveApplication.getBean(UserService.class);
            List<com.caipu.entity.User> listUsers = userService.getRobot();
            for (int i = 0; i < listUsers.size(); i++) {
                com.caipu.entity.User user = listUsers.get(i);
                //忽略没图像的用户
                if (StringUtils.isEmpty(user.getImage())) {
                    continue;
                }
                ROOTBOOT_NUMBERS += 1;
                Robot robot = new Robot();
                robot.setId(String.valueOf(user.getId()));
                robot.setIcon(user.getImage());
                robot.setName(user.getNickName());
                if (StringUtils.isEmpty(robot.getName())) {
                    robot.setName(robot.getId());
                }

                redis.lpush(REDIS_ROBOOT_KEY, JsonHelper.toJson(robot), asy -> {
                    if (!asy.succeeded()) {
                        LOGGER.error("push robot error:{}", asy.cause().getMessage());
                    }
                });

            }
            future.complete("success");

        }, asyResult -> {
            if (asyResult.succeeded()) {
                LOGGER.info("LoadAllRobots done:{}", ROOTBOOT_NUMBERS);
                startRobootTask();
            } else {
                LOGGER.error("LoadAllRobots error:{}", asyResult.cause().getMessage());
            }
        });

    }
}
