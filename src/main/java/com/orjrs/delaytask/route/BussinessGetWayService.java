package com.orjrs.delaytask.route;

import com.orjrs.delaytask.annotation.DelayTaskType;
import com.orjrs.delaytask.service.IDelayTaskBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 业务网关服务类
 *
 * @author orjrs
 * @create 2020-05-08 17:55
 * @since 1.0.0
 */
@Service
@Slf4j
public class BussinessGetWayService {
    @Autowired
    private List<IDelayTaskBusinessService> IDelayTaskBusinessServiceList;

    /**
     * 导航到具体的延时任务业务类
     *
     * @param topic 主题
     * @return DelayTaskBusinessService
     */
    public IDelayTaskBusinessService route(String topic) {
        for (IDelayTaskBusinessService service : IDelayTaskBusinessServiceList) {
            DelayTaskType delayTaskType = service.getClass().getAnnotation(DelayTaskType.class);
            if (delayTaskType.topic().getValue().equals(topic)) {
                log.info("已导航到具体的延时任务业务类，{}", service.getClass().getSimpleName());
                return service;
            }
        }
        throw new RuntimeException("topic出错~~~");
    }
}
