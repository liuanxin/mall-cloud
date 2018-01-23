package com.github.common.resource;

import com.github.common.util.A;
import com.github.common.util.U;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class CollectEnumUtil {

    /** 如果需要在 enum 中返回给前台的下拉数据, 添加一个这样的方法名, 返回结果用 Map. 如果有定义将无视下面的 code 和 value */
    private static final String METHOD = "select";
    /** 在 enum 中返回给前台的下拉数据的键, 如果没有将会以 ordinal() 为键 */
    private static final String CODE = "getCode";
    /** 在 enum 中返回给前台的下拉数据的值, 如果没有将会以 name() 为值 */
    private static final String VALUE = "getValue";

    private static List<Map<Object, Object>> cacheList = Lists.newArrayList();
    private static Map<String, Map<Object, Object>> cacheMap = Maps.newHashMap();

    /** 获取所有枚举的说明 */
    public static List<Map<Object, Object>> enumList(Map<String, Object> enums) {
        if (A.isNotEmpty(cacheList)) {
            return cacheList;
        }
        List<Map<Object, Object>> enumList = Lists.newArrayList();
        for (String type : enums.keySet()) {
            Map<Object, Object> enumInfo = enumInfo(type, enums);
            if (A.isNotEmpty(enumInfo)) {
                enumList.add(A.maps(type, enumInfo));
            }
        }
        cacheList = enumList;
        return enumList;
    }

    /** 根据枚举的名字获取单个枚举的说明 */
    @SuppressWarnings("unchecked")
    public static Map<Object, Object> enumInfo(String type, Map<String, Object> enums) {
        if (U.isBlank(type)) {
            return Collections.emptyMap();
        }
        Map<Object, Object> returnMap = cacheMap.get(type);
        if (A.isNotEmpty(returnMap)) {
            return returnMap;
        }

        Object enumClass = enums.get(type.toLowerCase());
        if (enumClass == null || !((Class) enumClass).isEnum()) {
            return Collections.emptyMap();
        }
        try {
            Method select = ((Class) enumClass).getMethod(METHOD);
            if (U.isNotBlank(select)) {
                Object result = select.invoke(null);
                if (U.isNotBlank(result) && result instanceof Map) {
                    Map<Object, Object> resultMap = (Map<Object, Object>) result;
                    cacheMap.put(type, resultMap);
                    return resultMap;
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // ignore
        }

        Map<Object, Object> map = A.newLinkedHashMap();
        for (Object anEnum : ((Class) enumClass).getEnumConstants()) {
            // 没有 getCode 方法就使用枚举的 ordinal
            Object key = U.getMethod(anEnum, CODE);
            if (key == null) {
                key = ((Enum) anEnum).ordinal();
            }

            // 没有 getValue 方法就使用枚举的 name
            Object value = U.getMethod(anEnum, VALUE);
            if (value == null) {
                value = ((Enum) anEnum).name();
            }

            map.put(key, value);
        }
        cacheMap.put(type, map);
        return map;
    }
}
