package com.orjrs.delaytask.service.impl;

import com.orjrs.delaytask.annotation.DelayTaskType;
import com.orjrs.delaytask.constants.enums.BussinessTypeEnum;
import com.orjrs.delaytask.service.IDelayTaskBusinessService;
import com.orjrs.delaytask.task.IDelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 账号业务任务类
 *
 * @author orjrs
 * @create 2020-05-08 17:29
 * @since 1.0.0
 */
@Slf4j
@Service
@DelayTaskType(topic= BussinessTypeEnum.OPEN_ACCOUNT ,topicDesc = "账号任务")
public class AccountBusinessServiceImpl implements IDelayTaskBusinessService {
    @Override
    public boolean execute(String topic, String key, long time, IDelayTask IDelayTask) {
        log.info("账号延时任务开始执行，topic：{} ，key ：{}", topic, key);
        try {
            // 模拟业务
            Thread.sleep(500);
            log.info("账号延时任务执行成功，topic：{} ，key ：{}", topic, key);
        } catch (Exception e) {
            log.error("执行失败！！！", e);
            log.info("延时任务模块，topic：{} ,key：{}, addDelayedTask :{}, 延时任务执行失败，将要重新添加，下一次执行将会是 300s 以后",
                    topic, key, IDelayTask.getClass().getSimpleName());
            IDelayTask.addDelayTask(topic, key, 300, TimeUnit.SECONDS);

        }

        return false;
    }
}
