package com.github.order.config;

/**
 * 订单模块相关的常数设置类
 *
 * @author https://github.com/liuanxin
 */
public final class OrderConst {

    /** 当前模块名. 要与 application.yml 中的一致 */
    public static final String MODULE_NAME = "order";

    /** 当前模块说明. 当用在文档中时有用 */
    public static final String MODULE_INFO = MODULE_NAME + "-订单";
    /** 当前模块版本. 当用在文档中时有用 */
    public static final String MODULE_VERSION = "1.0";


    // ========== url 说明 ==========

    /** 测试地址 */
    public static final String ORDER_DEMO = MODULE_NAME + "/demo";
}
