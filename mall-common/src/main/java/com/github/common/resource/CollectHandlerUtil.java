package com.github.common.resource;

import com.github.common.util.A;
import com.github.common.util.LogUtil;
import com.google.common.collect.Lists;
import org.apache.ibatis.type.TypeHandler;

import java.util.Arrays;
import java.util.List;

public final class CollectHandlerUtil {

    public static TypeHandler[] handler(List<TypeHandler[]> allModelHandler) {
        List<TypeHandler> handlerList = Lists.newArrayList();
        for (TypeHandler[] typeHandlers : allModelHandler) {
            // 将模块里面的 mybatis 类型处理器都收集起来装载进 mybatis 上下文
            handlerList.addAll(Arrays.asList(typeHandlers));
        }
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("mybatis load type handle:({})", A.toStr(handlerList));
        }
        return handlerList.toArray(new TypeHandler[handlerList.size()]);
    }
}
