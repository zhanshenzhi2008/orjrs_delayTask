package com.orjrs.delaytask.task;

import com.orjrs.delaytask.constants.enums.BussinessTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 循环拉取的任务具体实现类
 *
 * @author orjrs
 * @create 2020-05-08 19:34
 * @since 1.0.0
 */
@Slf4j
@Service("loopPullDelayedTaskImpl")
public class LoopPullDelayedTaskImpl implements IDelayTask {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private LoopPullDelayedTaskListener loopPullDelayedTaskListener;


    @Override
    public void addDelayTask(String topic, String value, long times, TimeUnit timeUnit) {
        BoundZSetOperations<String, String> zset = stringRedisTemplate.boundZSetOps(topic);
        LocalDateTime now = LocalDateTime.now();
        long time = 0L;
        if (timeUnit == TimeUnit.SECONDS) {
            time = now.plusSeconds(times).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else if (timeUnit == TimeUnit.MINUTES) {
            time = now.plusMinutes(times).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else if (timeUnit == TimeUnit.HOURS) {
            time = now.plusHours(times).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else if (timeUnit == TimeUnit.DAYS) {
            time = now.plusDays(times).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        boolean status = zset.add(value, time);
        log.info("topic(键key)：{} , value：{} 添加延时任务{}", topic, value, status ? "成功" : "失败");
        if (!status) {
            return;
        }
        // 获取当前的 topic 线程如果 waiting 的状态则唤醒
        Thread t = loopPullDelayedTaskListener.getLoopPullDelayedTaskThread(BussinessTypeEnum.getByValue(topic));
        if (t.getState() == Thread.State.WAITING) {
            LockSupport.unpark(t);
            log.info("topic(键key)：{} 线程已经从 waiting 中重新唤醒", topic);
        }
    }

    @Override
    public void removeDelayTask(String topic, String key) {
        BoundZSetOperations<String, String> zset = stringRedisTemplate.boundZSetOps(topic);
        long count = zset.remove(key);
        log.info("topic：{} , key：{} 移除延时任务{}", topic, key, count > 0 ? "成功" : "失败");

    }
}
