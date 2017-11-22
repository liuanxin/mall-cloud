package com.github.common.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.Date;

/** 日志管理, 使用此 utils 获取 log, 不要在类中使用 LoggerFactory.getLogger 的方式! */
public final class LogUtil {

    /** 根日志: 在类里面使用 LoggerFactory.getLogger(XXX.class) 跟这种方式一样! */
    public static final Logger ROOT_LOG = LoggerFactory.getLogger("root");

    /** SQL 相关的日志 */
    public static final Logger SQL_LOG = LoggerFactory.getLogger("sqlLog");


    /** 接收到请求的时间. 在 log.xml 中使用 %X{recordTime} 获取  */
    private static final String RECEIVE_TIME = "receiveTime";
    /** 请求信息: 包括 ip、url, param 等  */
    private static final String REQUEST_INFO = "requestInfo";

    /** 输出当前请求信息, 在日志中显示 */
    public static void bind(RequestLogContext logContextInfo) {
        recordTime();
        MDC.put(REQUEST_INFO, logContextInfo.requestInfo());
    }
    public static void unbind() {
        MDC.clear();
    }

    public static void recordTime() {
        MDC.put(RECEIVE_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date()) + " -> ");
    }


    @Setter
    @Getter
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class RequestLogContext {
        boolean online;

        String id;
        String name;
        /** 访问 ip */
        String ip;
        /** 访问方法 */
        String method;
        /** 访问地址 */
        String url;
        /** 请求 body 中的参数 */
        String param;
        /** 请求 header 中的参数 */
        String headParam;

        private String requestInfo() {
            StringBuilder sbd = new StringBuilder();
            sbd.append("[");
            if (U.isBlank(id) && U.isBlank(name)) {
                sbd.append(String.format("%s (%s %s) param(%s)", ip, method, url, param));
            } else {
                sbd.append(String.format("%s (%s/%s) (%s %s) param(%s)", ip, id, name, method, url, param));
            }
            sbd.append(" ");
            // 非线上环境则输出 head 信息
            if (!online) {
                sbd.append(String.format("header(%s)", headParam));
            }
            sbd.append("]");
            if (!online) {
                sbd.append("\n");
            }
            return sbd.toString();
        }
    }
}
