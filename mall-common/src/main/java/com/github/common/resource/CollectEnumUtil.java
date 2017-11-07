package com.github.common.resource;

import com.google.common.collect.Lists;
import com.github.common.util.U;
import com.github.common.util.A;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class CollectEnumUtil {

    /** 获取所有枚举的说明 */
    @SuppressWarnings("unchecked")
    public static List<Map<Object, Object>> enumList(Map<String, Object> enums) {
        List<Map<Object, Object>> enumList = Lists.newArrayList();
        for (String type : enums.keySet()) {
            Map<Object, Object> enumInfo = enumInfo(type, enums);
            if (A.isNotEmpty(enumInfo)) {
                enumList.add(A.maps(type, enumInfo));
            }
        }
        return enumList;
    }
    /** 根据枚举的名字获取单个枚举的说明 */
    @SuppressWarnings("unchecked")
    public static Map<Object, Object> enumInfo(String type, Map<String, Object> enums) {
        if (U.isBlank(type)) {
            return Collections.EMPTY_MAP;
        }

        Object enumClass = enums.get(type.toLowerCase());
        if (enumClass == null) {
            return Collections.EMPTY_MAP;
        }

        if (!((Class) enumClass).isEnum()) {
            return Collections.EMPTY_MAP;
        }

        Map<Object, Object> map = A.newLinkedHashMap();
        for (Object anEnum : ((Class) enumClass).getEnumConstants()) {
            // 没有 getCode 方法就使用枚举的 ordinal
            Object key = U.getMethod(anEnum, "getCode");
            if (key == null) {
                key = ((Enum) anEnum).ordinal();
            }

            // 没有 getValue 方法就使用枚举的 name
            Object value = U.getMethod(anEnum, "getValue");
            if (value == null) {
                value = ((Enum) anEnum).name();
            }

            map.put(key, value);
        }
        return map;
    }
}
