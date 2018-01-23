package com.github.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.common.util.A;
import com.github.common.util.U;
import com.google.common.primitives.Ints;

import java.util.Map;

/** 性别 */
public enum Gender {

    Male(0, "男"), Female(1, "女"), Nil(3, "未知"), Other(4, "其他");

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

    /** 返回给前台的下拉数据. 当需要自定义时使用此种方式 */
    public static Map<String, Object> select() {
        return A.linkedMaps(
                Male.code, Male.value,
                Female.code, Female.value,
                Ints.join(",", Nil.code, Other.code), Other.value
        );
    }
}
