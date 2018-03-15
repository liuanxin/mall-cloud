package com.github.common.resource;

import com.github.common.Const;
import com.github.common.util.A;
import com.github.common.util.U;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class CollectEnumUtil {

    /** 如果需要在 enum 中返回给前台的下拉数据, 添加一个这样的方法名, 返回结果用 Map. 如果有定义将无视下面的 code 和 value */
    private static final String METHOD = "select";
    /** 在 enum 中返回给前台的下拉数据的键, 如果没有将会以 ordinal() 为键 */
    private static final String CODE = "getCode";
    /** 在 enum 中返回给前台的下拉数据的值, 如果没有将会以 name() 为值 */
    private static final String VALUE = "getValue";

    private static Map<String, Map<Object, Object>> cacheMap = Maps.newLinkedHashMap();

    /** 获取所有枚举的说明 */
    public static Map<String, Map<Object, Object>> enumMap(Map<String, Class> enumClassMap) {
        if (A.isNotEmpty(cacheMap)) {
            return cacheMap;
        }
        Map<String, Class> enums = getEnumMap(enumClassMap);
        for (String type : enums.keySet()) {
            enumInfo(type, enums);
        }
        return cacheMap;
    }

    /** 根据枚举的名字获取单个枚举的说明 */
    @SuppressWarnings("unchecked")
    public static Map<Object, Object> enumInfo(String type, Map<String, Class> enumClassMap) {
        if (U.isBlank(type)) {
            return Collections.emptyMap();
        }
        Map<Object, Object> returnMap = cacheMap.get(type);
        if (A.isNotEmpty(returnMap)) {
            return returnMap;
        }

        Map<String, Class> enums = getEnumMap(enumClassMap);
        Class enumClass = enums.get(type.toLowerCase());
        if (enumClass == null || !enumClass.isEnum()) {
            return Collections.emptyMap();
        }
        try {
            Method select = enumClass.getMethod(METHOD);
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

        Map<Object, Object> map = Maps.newHashMap();
        for (Object anEnum : enumClass.getEnumConstants()) {
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


    /**
     * 从 指定模块指定类 获取所有的枚举类(放入 view 的上下文).
     *
     * key 表示模块名(包含在枚举所在类的包名上), value 表示枚举类所在包的类(用来获取 ClassLoader)
     */
    public static Class[] getEnumClass(Map<String, Class> enumMap) {
        Set<Class> set = Sets.newHashSet();
        for (Map.Entry<String, Class> entry : enumMap.entrySet()) {
            Class[] enums = Loader.getEnumArray(entry.getValue(), Const.enumPath(entry.getKey()));
            for (Class anEnum : enums) {
                if (U.isNotBlank(anEnum) && anEnum.isEnum()) {
                    // 将每个模块里面的枚举都收集起来, 然后会放入到渲染上下文里面去
                    set.add(anEnum);
                }
            }
        }
        return set.toArray(new Class[set.size()]);
    }

    /**
     * 从 指定模块指定类 获取所有的枚举类(生成枚举说明接口).
     *
     * key 表示模块名(包含在枚举所在类的包名上), value 表示枚举类所在包的类(用来获取 ClassLoader)
     */
    private static Map<String, Class> getEnumMap(Map<String, Class> enumMap) {
        Map<String, Class> returnMap = Maps.newHashMap();
        for (Map.Entry<String, Class> entry : enumMap.entrySet()) {
            Class[] enums = Loader.getEnumArray(entry.getValue(), Const.enumPath(entry.getKey()));
            for (Class anEnum : enums) {
                if (U.isNotBlank(anEnum) && anEnum.isEnum()) {
                    // 将每个模块里面的枚举都收集起来供请求接口使用
                    returnMap.put(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, anEnum.getSimpleName()), anEnum);
                }
            }
        }
        return returnMap;
    }
}
