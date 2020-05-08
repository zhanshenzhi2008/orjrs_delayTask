package com.orjrs.delaytask.constants.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务任务类型 枚举
 *
 * @author orjrs
 * @create 2020-05-08 17:36
 * @since 1.0.0
 */
public enum BussinessTypeEnum {

    /** 未知 */
    UN_KNOW("unKnow"),

    /** 开户 */
    OPEN_ACCOUNT("openAccount");

    /** 业务编码 */
    private String value;

    public String getValue() {
        return value;
    }

    BussinessTypeEnum(String value) {
        this.value = value;
    }

    /**
     * 获取所有的枚举值
     *
     * @return List<String>
     */
    public static List<String> getAllBusinessTypeStringList() {
        return Arrays.stream(values()).map(BussinessTypeEnum::getValue).collect(Collectors.toList());
    }

    /**
     * 根据业务编码value获取枚举对象
     *
     * @param value 业务编码
     * @return BusinessTaskTypeEnum
     */
    public static BussinessTypeEnum getByValue(String value) {
        return Arrays.stream(values()).filter(v -> v.getValue().equals(value)).findFirst().get();
    }

    /**
     * 获取所有的枚举对象
     *
     * @return List<BusinessTaskTypeEnum>
     */
    public static List<BussinessTypeEnum> getAllBusinessTypeList() {
        return Arrays.stream(values()).collect(Collectors.toList());
    }
}
