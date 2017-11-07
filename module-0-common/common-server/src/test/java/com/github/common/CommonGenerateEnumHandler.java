package com.github.common;

import com.github.common.Const;
import com.github.common.util.GenerateEnumHandler;
import com.github.common.config.CommonConst;
import org.junit.Test;

/**
 * 公共服务模块生成 enumHandle 的工具类
 *
 * @author https://github.com/liuanxin
 */
public class CommonGenerateEnumHandler {

    @Test
    public void generate() {
        GenerateEnumHandler.generateEnum(getClass(), Const.BASE_PACKAGE, CommonConst.MODULE_NAME);
    }
}
