package com.github.common.config;

import com.github.common.resource.CollectMybatisTypeHandlerUtil;
import com.github.common.resource.CollectResourceUtil;
import com.github.common.util.A;
import com.github.global.constant.GlobalConst;
import com.github.common.constant.CommonConst;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.core.io.Resource;

/**
 * 公共服务模块的配置数据. 主要是 mybatis 的多配置目录和类型处理器
 *
 * @author https://github.com/liuanxin
 */
final class CommonConfigData {

    private static final String[] RESOURCE_PATH = new String[] {
            CommonConst.MODULE_NAME + "/*.xml",
            CommonConst.MODULE_NAME + "-custom/*.xml"
    };
    /** 要加载的 mybatis 的配置文件目录 */
    static final Resource[] RESOURCE_ARRAY = CollectResourceUtil.resource(A.maps(
            CommonConfigData.class, RESOURCE_PATH
    ));
    
    /** 要加载的 mybatis 类型处理器的目录 */
    static final TypeHandler[] HANDLER_ARRAY = CollectMybatisTypeHandlerUtil.handler(A.maps(
            GlobalConst.MODULE_NAME, GlobalConst.class,
            CommonConst.MODULE_NAME, CommonConfigData.class
    ));
}
