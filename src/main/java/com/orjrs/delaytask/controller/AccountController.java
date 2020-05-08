package com.orjrs.delaytask.controller;

import com.orjrs.delaytask.constants.enums.BussinessTypeEnum;
import com.orjrs.delaytask.task.IDelayTask;
import com.orjrs.delaytask.task.LoopPullDelayedTaskListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 账号控制类
 *
 * @author orjrs
 * @create 2020-05-08 20:22
 * @since 1.0.0
 */
@RestController
public class AccountController {


    @Resource
    private LoopPullDelayedTaskListener loopPullDelayedTaskListener;

    @Resource(name = "loopPullDelayedTaskImpl")
    private IDelayTask loopPullDelayedTaskImpl;

    /**
     * 添加主动拉取循环的任务
     *
     * @return
     */
    @RequestMapping("/account/addLoopPullDelayedTask")
    @ResponseBody
    public String addLoopPullDelayedTask() {
        loopPullDelayedTaskImpl.addDelayTask(BussinessTypeEnum.OPEN_ACCOUNT.getValue(), "12345", 10, TimeUnit.SECONDS);
        return "success";
    }
}
