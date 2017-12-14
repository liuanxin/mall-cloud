package com.github.order.config;

import com.google.common.collect.Lists;
import com.github.common.Const;
import com.github.common.resource.CollectHandlerUtil;
import com.github.common.resource.CollectResourceUtil;
import com.github.common.resource.LoaderHandler;
import com.github.common.resource.LoaderResource;
import com.github.global.config.GlobalConst;
import com.github.order.config.OrderConst;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 订单模块的配置数据. 主要是 mybatis 的多配置目录和类型处理器
 *
 * @author https://github.com/liuanxin
 */
final class OrderConfigData {

    private static final String[] RESOURCE_PATH = new String[] {
            OrderConst.MODULE_NAME + "/*.xml",
            OrderConst.MODULE_NAME + "-custom/*.xml"
    };
    private static final List<Resource[]> RESOURCES = Lists.newArrayList();
    static {
        RESOURCES.add(LoaderResource.getResourceArray(OrderConfigData.class, RESOURCE_PATH));
    }

    private static final List<TypeHandler[]> HANDLERS = Lists.newArrayList();
    static {
        HANDLERS.add(LoaderHandler.getHandleArray(GlobalConst.class, Const.handlerPath(GlobalConst.MODULE_NAME)));
        HANDLERS.add(LoaderHandler.getHandleArray(OrderConfigData.class, Const.handlerPath(OrderConst.MODULE_NAME)));
    }

    /** 要加载的 mybatis 的配置文件目录 */
    static final Resource[] RESOURCE_ARRAY = CollectResourceUtil.resource(RESOURCES);
    /** 要加载的 mybatis 类型处理器的目录 */
    static final TypeHandler[] HANDLER_ARRAY = CollectHandlerUtil.handler(HANDLERS);
}
