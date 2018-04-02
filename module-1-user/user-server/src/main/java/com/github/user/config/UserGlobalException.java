package com.github.user.config;

import com.github.common.exception.ForbiddenException;
import com.github.common.exception.NotLoginException;
import com.github.common.exception.ServiceException;
import com.github.common.json.JsonResult;
import com.github.common.util.A;
import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
        bindAndPrintLog(e);

        String msg = String.format("Not found(%s %s)", e.getHttpMethod(), e.getRequestURL());
        return new ResponseEntity<>(JsonResult.notFound(msg), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<JsonResult> missParam(MissingServletRequestParameterException e) {
        bindAndPrintLog(e);

        String msg = String.format("缺少必须的参数(%s), 类型(%s)", e.getParameterName(), e.getParameterType());
        return new ResponseEntity<>(JsonResult.badRequest(msg), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<JsonResult> notSupported(HttpRequestMethodNotSupportedException e) {
        bindAndPrintLog(e);

        String msg = String.format("不支持此种请求方式! 当前方式(%s), 支持方式(%s)",
                e.getMethod(), A.toStr(e.getSupportedMethods()));
        return new ResponseEntity<>(JsonResult.fail(msg), FAIL);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<JsonResult> uploadSizeExceeded(MaxUploadSizeExceededException e) {
        bindAndPrintLog(e);

        // 右移 20 位相当于除以两次 1024, 正好表示从字节到 Mb
        String msg = String.format("上传文件太大! 请保持在 %sM 以内", (e.getMaxUploadSize() >> 20));
        return new ResponseEntity<>(JsonResult.fail(msg), FAIL);
    }
    private void bindAndPrintLog(Exception e) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.bind(RequestUtils.logContextInfo());
            try {
                LogUtil.ROOT_LOG.debug(e.getMessage(), e);
            } finally {
                LogUtil.unbind();
            }
        }
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
