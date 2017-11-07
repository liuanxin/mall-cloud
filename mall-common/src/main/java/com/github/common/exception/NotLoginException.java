package com.github.common.exception;

import java.io.Serializable;

/** 用户未登录的统一处理 */
public class NotLoginException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_MSG = "请先登录";

    public NotLoginException() {
        super(DEFAULT_MSG);
    }
    public NotLoginException(String msg) {
        super(msg);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
