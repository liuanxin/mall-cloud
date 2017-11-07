package com.github.user.config;

import com.google.common.collect.Lists;
import com.github.common.Const;
import com.github.common.resource.CollectHandlerUtil;
import com.github.common.resource.CollectResourceUtil;
import com.github.common.resource.LoaderHandler;
import com.github.common.resource.LoaderResource;
import com.github.global.config.GlobalConst;
import com.github.user.config.UserConst;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 用户模块的配置数据. 主要是 mybatis 的多配置目录和类型处理器
 *
 * @author https://github.com/liuanxin
 */
public final class UserConfigData {

    private static final String[] RESOURCE_PATH = new String[] {
            UserConst.MODULE_NAME + "/*.xml",
            UserConst.MODULE_NAME + "-custom/*.xml"
    };
    private static final List<Resource[]> RESOURCES = Lists.newArrayList();
    static {
        RESOURCES.add(LoaderResource.getResourceArray(UserConfigData.class, RESOURCE_PATH));
    }

    private static final List<TypeHandler[]> HANDLERS = Lists.newArrayList();
    static {
        HANDLERS.add(LoaderHandler.getHandleArray(GlobalConst.class, Const.handlerPath(GlobalConst.MODULE_NAME)));
        HANDLERS.add(LoaderHandler.getHandleArray(UserConfigData.class, Const.handlerPath(UserConst.MODULE_NAME)));
    }

    /** 要加载的 mybatis 的配置文件目录 */
    public static final Resource[] RESOURCE_ARRAY = CollectResourceUtil.resource(RESOURCES);
    /** 要加载的 mybatis 类型处理器的目录 */
    public static final TypeHandler[] HANDLER_ARRAY = CollectHandlerUtil.handler(HANDLERS);
}
