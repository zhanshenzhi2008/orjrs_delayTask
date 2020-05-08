package com.orjrs.delaytask.task;

import java.util.concurrent.TimeUnit;

/**
 * 延时任务接口
 *
 * @author orjrs
 * @create 2020-05-08 17:12
 * @since 1.0.0
 */
public interface IDelayTask {

    /**
     * 添加任务
     *
     * @param topic    主题
     * @param key      键
     * @param time     time后延时添加任务
     * @param timeUnit 时间单位
     */
    void addDelayTask(String topic, String key, long time, TimeUnit timeUnit);

    /**
     * 删除任务
     *
     * @param topic    主题
     * @param key      键
     * @param time     time后延时删除任务
     * @param timeUnit 时间单位
     */
    void removeDelayTask(String topic, String key);
}
