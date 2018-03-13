package com.github.util;

import com.github.common.Const;
import com.github.common.constant.CommonConst;
import com.github.common.resource.Loader;
import com.github.common.util.A;
import com.github.common.util.U;
import com.github.global.constant.GlobalConst;
import com.github.order.constant.OrderConst;
import com.github.product.constant.ProductConst;
import com.github.user.constant.UserConst;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** 从各模块中收集数据的工具类 */
public final class ManagerDataCollectUtil {

    private static final List<Class[]> ALL_MODEL_ENUM = Lists.newArrayList();
    static {
        Map<String, Class> enumMap = A.maps(
                GlobalConst.MODULE_NAME, GlobalConst.class,

                CommonConst.MODULE_NAME, CommonConst.class,
                UserConst.MODULE_NAME, UserConst.class,
                ProductConst.MODULE_NAME, ProductConst.class,
                OrderConst.MODULE_NAME, OrderConst.class
        );
        for (Map.Entry<String, Class> entry : enumMap.entrySet()) {
            ALL_MODEL_ENUM.add(Loader.getEnumArray(entry.getValue(), Const.enumPath(entry.getKey())));
        }
    }

    /** 放入渲染上下文中去的枚举列表 */
    public static final Class[] ENUM_CLASS;
    /** 将所有的枚举整成一个 map 供接口调用 */
    public static final Map<String, Object> ENUMS = Maps.newHashMap();
    static {
        Set<Class> set = Sets.newHashSet();
        for (Class[] enums : ALL_MODEL_ENUM) {
            for (Class anEnum : enums) {
                if (U.isNotBlank(anEnum) && anEnum.isEnum()) {
                    // 将每个模块里面的枚举都收集起来, 然后会放入到渲染上下文里面去
                    set.add(anEnum);
                    // 将每个模块里面的枚举都收集起来供请求接口使用
                    ENUMS.put(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, anEnum.getSimpleName()), anEnum);
                }
            }
        }
        ENUM_CLASS = set.toArray(new Class[set.size()]);
    }
}
