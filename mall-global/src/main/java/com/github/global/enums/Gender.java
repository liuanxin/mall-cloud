package com.github.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.common.util.A;
import com.github.common.util.U;

import java.util.Map;

/** 性别 */
public enum Gender {

    Male(0, "男"), Female(1, "女"), Nil(3, "未知");

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
    public int getCode() {
        return code;
    }


    private static final String CODE = "code";
    /** 序列化给前端时. 默认返回 name(), 如果只想给前端返回数值, 把注解挪到 getCode 即可 */
    @JsonValue
    public Map<String, String> serializer() {
        return A.maps(
                CODE, code,    // 前端传递这个, 数据库也存这个
                "value", value // 前端显示这个
        );
    }
    /** 数据反序列化回来时 */
    @JsonCreator
    public static Gender deserializer(Object obj) {
        Object tmp;
        if (obj instanceof Map) {
            tmp = ((Map) obj).get(CODE);
        } else {
            tmp = obj.toString();
        }
        return U.toEnum(Gender.class, tmp);
    }
}
