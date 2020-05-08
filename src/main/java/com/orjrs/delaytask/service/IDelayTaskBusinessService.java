package com.orjrs.delaytask.service;

import com.orjrs.delaytask.task.IDelayTask;

/**
 * 延时任务业务接口
 *
 * @author orjrs
 * @create 2020-05-08 17:12
 * @since 1.0.0
 */
public interface IDelayTaskBusinessService {

    /**
     * 执行任务
     *
     * @param topic    主题
     * @param key      键
     * @param time     时间
     * @param IDelayTask 延时任务接口
     *
     */
    boolean execute(String topic, String key, long time, IDelayTask IDelayTask);
}
