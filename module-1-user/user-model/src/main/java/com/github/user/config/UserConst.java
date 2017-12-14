package com.github.user.config;

/**
 * 用户模块相关的常数设置类
 *
 * @author https://github.com/liuanxin
 */
public final class UserConst {

    /** 当前模块名. 要与 application.yml 中的一致 */
    public static final String MODULE_NAME = "user";

    /** 当前模块说明. 当用在文档中时有用 */
    public static final String MODULE_INFO = MODULE_NAME + "-用户";
    /** 当前模块版本. 当用在文档中时有用 */
    public static final String MODULE_VERSION = "1.0";


    // ========== url 说明 ==========

    /** 测试地址 */
    public static final String USER_DEMO = MODULE_NAME + "/demo";
}
