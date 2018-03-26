package com.github.user.config;

import com.github.common.exception.ForbiddenException;
import com.github.common.exception.NotLoginException;
import com.github.common.exception.ServiceException;
import com.github.common.json.JsonResult;
import com.github.common.util.A;
import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import com.github.common.util.U;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 处理全局异常的控制类. 如果要自定义错误处理类
 *
 * @see org.springframework.boot.autoconfigure.web.ErrorController
 * @see org.springframework.boot.autoconfigure.web.ErrorProperties
 * @see org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration
 *
 * @author https://github.com/liuanxin
 */
@RestControllerAdvice
public class UserGlobalException {

    private static final HttpStatus FAIL = HttpStatus.INTERNAL_SERVER_ERROR;

    @Value("${online:false}")
    private boolean online;

    /** 业务异常 */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<JsonResult> service(ServiceException e) {
        String msg = e.getMessage();
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(msg);
        }
        return new ResponseEntity<>(JsonResult.fail(msg), FAIL);
    }
    /** 未登录 */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<JsonResult> notLogin(NotLoginException e) {
        String msg = e.getMessage();
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(msg);
        }
        return new ResponseEntity<>(JsonResult.notLogin(msg), HttpStatus.UNAUTHORIZED);
    }
    /** 无权限 */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<JsonResult> forbidden(ForbiddenException e) {
        String msg = e.getMessage();
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug(msg);
        }
        return new ResponseEntity<>(JsonResult.notPermission(msg), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<JsonResult> noHandler(NoHandlerFoundException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.bind(RequestUtils.logContextInfo());
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
            LogUtil.unbind();
        }
        return new ResponseEntity<>(JsonResult.notFound(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<JsonResult> notSupported(HttpRequestMethodNotSupportedException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.bind(RequestUtils.logContextInfo());
            LogUtil.ROOT_LOG.debug(e.getMessage(), e);
            LogUtil.unbind();
        }

        String msg = U.EMPTY;
        if (!online) {
            msg = " 当前方式(" + e.getMethod() + "), 支持方式(" + A.toStr(e.getSupportedMethods()) + ")";
        }
        return new ResponseEntity<>(JsonResult.fail("不支持此种请求方式!" + msg), FAIL);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<JsonResult> uploadSizeExceeded(MaxUploadSizeExceededException e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("文件太大: " + e.getMessage(), e);
        }
        // 右移 20 位相当于除以两次 1024, 正好表示从字节到 Mb
        JsonResult<Object> result = JsonResult.fail("上传文件太大! 请保持在 " + (e.getMaxUploadSize() >> 20) + "M 以内");
        return new ResponseEntity<>(result, FAIL);
    }

    /** 未知的所有其他异常 */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<JsonResult> other(Throwable e) {
        String msg;
        if (online) {
            msg = "请求时出现错误, 我们会尽快处理";
        } else if (e instanceof NullPointerException) {
            msg = "空指针异常, 联系后台查看日志进行处理";
        } else {
            msg = e.getMessage();
        }

        if (LogUtil.ROOT_LOG.isErrorEnabled()) {
            LogUtil.ROOT_LOG.error("有错误", e);
        }
        return new ResponseEntity<>(JsonResult.fail(msg), FAIL);
    }
}
