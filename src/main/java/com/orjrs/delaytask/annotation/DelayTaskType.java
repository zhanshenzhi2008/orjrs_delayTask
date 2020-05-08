package com.orjrs.delaytask.annotation;

import com.orjrs.delaytask.constants.enums.BussinessTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 延时任务类型
 *
 * @author orjrs
 * @create 2020-05-08 17:44
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DelayTaskType {

    /**
     * 主题
     *
     * @return TaskTypeEnum
     */
    BussinessTypeEnum topic() default BussinessTypeEnum.UN_KNOW;

    /**
     * 描述
     *
     * @return String
     */
    String topicDesc() default "";
}