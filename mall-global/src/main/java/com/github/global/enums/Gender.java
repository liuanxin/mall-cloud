package com.github.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.common.util.U;

/** 性别 */
public enum Gender {

    Male(0, "男"), Female(1, "女");

    int code;
    String value;
    Gender(int code, String value) {
        this.code = code;
        this.value = value;
    }

    /** 显示用 */
    public String getValue() {
        return value;
    }
    /** 数据关联用 */
    @JsonValue
    public int getCode() {
        return code;
    }
    /** 数据反序列化时调用 */
    @JsonCreator
    public static Gender deserializer(Object obj) {
        return U.toEnum(Gender.class, obj);
    }
}
