package com.github.common.config;

import com.google.common.collect.Lists;
import com.github.common.Const;
import com.github.common.resource.CollectHandlerUtil;
import com.github.common.resource.CollectResourceUtil;
import com.github.common.resource.LoaderHandler;
import com.github.common.resource.LoaderResource;
import com.github.global.config.GlobalConst;
import com.github.common.config.CommonConst;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 公共服务模块的配置数据. 主要是 mybatis 的多配置目录和类型处理器
 *
 * @author https://github.com/liuanxin
 */
public final class CommonConfigData {

    private static final String[] RESOURCE_PATH = new String[] {
            CommonConst.MODULE_NAME + "/*.xml",
            CommonConst.MODULE_NAME + "-custom/*.xml"
    };
    private static final List<Resource[]> RESOURCES = Lists.newArrayList();
    static {
        RESOURCES.add(LoaderResource.getResourceArray(CommonConfigData.class, RESOURCE_PATH));
    }

    private static final List<TypeHandler[]> HANDLERS = Lists.newArrayList();
    static {
        HANDLERS.add(LoaderHandler.getHandleArray(GlobalConst.class, Const.handlerPath(GlobalConst.MODULE_NAME)));
        HANDLERS.add(LoaderHandler.getHandleArray(CommonConfigData.class, Const.handlerPath(CommonConst.MODULE_NAME)));
    }

    /** 要加载的 mybatis 的配置文件目录 */
    public static final Resource[] RESOURCE_ARRAY = CollectResourceUtil.resource(RESOURCES);
    /** 要加载的 mybatis 类型处理器的目录 */
    public static final TypeHandler[] HANDLER_ARRAY = CollectHandlerUtil.handler(HANDLERS);
}
