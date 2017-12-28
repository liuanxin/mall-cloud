package com.github.config;

import com.github.common.exception.ForbiddenException;
import com.github.common.exception.NotLoginException;
import com.github.common.exception.ServiceException;
import com.github.common.json.JsonResult;
import com.github.common.util.A;
import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import com.github.common.util.U;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理全局异常的控制类
 *
 * @see org.springframework.boot.autoconfigure.web.ErrorController
 * @see org.springframework.boot.autoconfigure.web.ErrorProperties
 * @see org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration
 */
@ControllerAdvice
public class WebGlobalException {

    @Value("${online:false}")
    private boolean online;

    /** 业务异常 */
    @ExceptionHandler(ServiceException.class)
    public void serviceException(ServiceException e, HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
        }
        RequestUtils.toJson(JsonResult.fail(e.getMessage()), response);
    }

    /** 未登录 */
    @ExceptionHandler(NotLoginException.class)
    public void noLogin(NotLoginException e, HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
        }
        RequestUtils.toJson(JsonResult.notLogin(), response);
    }

    /** 无权限 */
    @ExceptionHandler(ForbiddenException.class)
    public void notFound(ForbiddenException e, HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
        }
        RequestUtils.toJson(JsonResult.fail(e.getMessage()), response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public void forbidden(NoHandlerFoundException e, HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
        }
        RequestUtils.toJson(JsonResult.fail("无对应的请求"), response);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public void notSupported(HttpRequestMethodNotSupportedException e, HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
        }

        String msg = U.EMPTY;
        if (!online) {
            msg = String.format(" 当前方式(%s), 支持方式(%s)", e.getMethod(), A.toStr(e.getSupportedMethods()));
        }
        RequestUtils.toJson(JsonResult.fail("不支持此种请求方式!" + msg), response);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public void notFound(MaxUploadSizeExceededException e, HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("文件太大: " + e.getMessage(), e);
        }
        // 右移 20 位相当于除以两次 1024, 正好表示从字节到 Mb
        RequestUtils.toJson(JsonResult.fail("上传文件太大! 请保持在 " + (e.getMaxUploadSize() >> 20) + "M 以内"), response);
    }

    /** 未知的所有其他异常 */
    @ExceptionHandler(Throwable.class)
    public void exception(Throwable e, HttpServletResponse response) throws IOException {
        if (LogUtil.ROOT_LOG.isErrorEnabled()) {
            LogUtil.ROOT_LOG.error("有错误: " + e.getMessage(), e);
        }
        RequestUtils.toJson(JsonResult.fail(online ? "请求时出现错误, 我们将会尽快处理." : e.getMessage()), response);
    }
}
