package com.github.common.config;

import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 公共模块的 web 拦截器
 *
 * @author https://github.com/liuanxin
 */
public class CommonInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        LogUtil.bind(RequestUtils.logContextInfo());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        if (ex != null) {
            if (LogUtil.ERROR_LOG.isErrorEnabled()) {
                LogUtil.ERROR_LOG.error("request was over, but have exception", ex);
            }
        }
        LogUtil.unbind();
    }
}
