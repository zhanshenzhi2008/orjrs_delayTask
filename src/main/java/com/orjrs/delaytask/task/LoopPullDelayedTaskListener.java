package com.orjrs.delaytask.task;

import com.orjrs.delaytask.constants.enums.BussinessTypeEnum;
import com.orjrs.delaytask.route.BussinessGetWayService;
import com.orjrs.delaytask.service.IDelayTaskBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 延时任务监听器
 *
 * @author orjrs
 * @create 2020-05-08 18:08
 * @since 1.0.0
 */
@Slf4j
@Component
public class LoopPullDelayedTaskListener {

    /** 各个模块延时任务的线程容器 */
    private static Map<BussinessTypeEnum, Thread> redisDelayTaskThreadMap = new ConcurrentHashMap<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /** 空轮询数，空轮询数到了 300 那么线程将会被暂停 */
    private final int DEFAULT_EMPTY_POLL_COUNT = 300;

    /** 导航具体业务实现类的网关 */
    @Autowired
    private BussinessGetWayService bussinessGetWayService;

    /** 用来添加或者删除延时任务的业务类 */
    @Resource(name = "loopPullDelayedTaskImpl")
    private IDelayTask loopPullDelayedTaskImpl;

    /** 异步执行业务的线程池，咱们因为是做实验所以就用缓存的线程池 */
    private static ExecutorService executors = Executors.newCachedThreadPool();

    /**
     * 根据业务类型获取线程对象
     *
     * @param bussinessTypeEnum 业务任务类型 枚举
     * @return Thread
     */
    public Thread getLoopPullDelayedTaskThread(BussinessTypeEnum bussinessTypeEnum) {
        return redisDelayTaskThreadMap.get(bussinessTypeEnum);
    }

    /**
     * 服务器启动后，启动各个业务的延时任务线程
     */
    @PostConstruct
    private void initTask() {
        //获取所有业务枚举对象
        List<BussinessTypeEnum> businessTypeEnumList = BussinessTypeEnum.getAllBusinessTypeList();
        //循环遍历所有枚举对象，然后每一个枚举都对应着一个线程
        for (BussinessTypeEnum businessTypeEnum : businessTypeEnumList) {
            if (businessTypeEnum.getValue().equals(BussinessTypeEnum.UN_KNOW.getValue())) {
                continue;
            }
            Thread thread = new Thread(new LoopPullDelayedTaskRunnable(businessTypeEnum));
            thread.start();
            //把这些线程一个一个的添加到容器中
            redisDelayTaskThreadMap.put(businessTypeEnum, thread);
        }
    }

    class LoopPullDelayedTaskRunnable implements Runnable {
        /** 业务类型 */
        private BussinessTypeEnum businessTypeEnum;

        /** 轮询的次数 */
        volatile int loopCount;

        LoopPullDelayedTaskRunnable(BussinessTypeEnum businessTypeEnum) {
            this.businessTypeEnum = businessTypeEnum;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 一秒钟轮询一次
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String topic = businessTypeEnum.getValue();
                if (loopCount == DEFAULT_EMPTY_POLL_COUNT) {
                    log.info("topic：{}开始进入等待阶段", topic);
                    // 线程等待
                    LockSupport.park();
                    // 如果有值或者被释放开则恢复初始值
                    loopCount = 0;
                }

                BoundZSetOperations<String, String> zSetOperations = stringRedisTemplate.boundZSetOps(topic);
                //  BoundZSetOperations  zSetOperations = operations;
                if (zSetOperations.zCard() == 0) {
                    log.info("topic：{}，还没有延时任务", topic);
                    loopCount++;
                    continue;
                }
                log.info("topic：{}，延时任务已经在监控中……", topic);
                loopCount = 0;
                // 获取最顶端的任务 set，其实是一个数据
                Set<String> rangeByScore = zSetOperations.rangeByScore(0, 0);

                // 获取最顶端的任务 set，其实是一个数据
                Set<ZSetOperations.TypedTuple<String>> scoreSets = zSetOperations.rangeWithScores(0, 0);
                // 获取下标为 0 的 TypedTuple 对象
                ZSetOperations.TypedTuple<String> tuple = (ZSetOperations.TypedTuple<String>) scoreSets.toArray()[0];
                double score = tuple.getScore();
                String value = tuple.getValue();
                LocalDateTime localDateTime = LocalDateTime.now();
                long times = (localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                if (times >= score) {
                    log.info("已从 zSet 中取出，开始是执行 topic：" + topic + "，value：" + value);
                    IDelayTaskBusinessService taskBusinessService = bussinessGetWayService.route(topic);
                    //异步执行任务防止堵塞主线程
                    executors.execute(() -> taskBusinessService.execute(topic, value, times, loopPullDelayedTaskImpl));
                    zSetOperations.remove(value);
                }
            }
        }
    }

}
